package com.mtycreate.createiceplus.item;

import com.mtycreate.createiceplus.Config;
import com.mtycreate.createiceplus.client.IceCannonClientHooks;
import com.mtycreate.createiceplus.entity.IceChargeEntity;
import com.mtycreate.createiceplus.entity.ModEntities;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * 冰块发射器——参照 Create 的土豆发射器（{@code PotatoCannonItem}）复刻。
 *
 * <p>与土豆炮一致的部分：
 * <ul>
 *   <li>继承 {@link ProjectileWeaponItem}，弹药直接从背包里找（{@link Player#getProjectile}）</li>
 *   <li>每种弹药有独立的伤害 / 装填时间 / 击退 / 初速度</li>
 *   <li>右键开火，抢口位置由 {@link ShootableGadgetItemMethods#getGunBarrelVec} 算出</li>
 *   <li>开火后有冷却，物品管有耐久消耗</li>
 *   <li>第一人称有后坐力动画，手臂摆成弩的姿势，且不触发挥臂动作</li>
 *   <li>悬停时显示当前弹药的三项数值（会被力量 / 冲击附魔加成）</li>
 * </ul>
 */
public class IceCannonItem extends ProjectileWeaponItem implements CustomArmPoseItem {
    /** 被视为弹药的所有物品。 */
    private static final Predicate<ItemStack> AMMO_PREDICATE = IceAmmoType::isAmmo;

    /** 基础初速度，对应 Create 土豆炮里的 2.0。 */
    private static final float BASE_VELOCITY = 2.0F;

    public IceCannonItem(Properties properties) {
        super(properties);
    }

    /** 取出玩家当前会装载的弹药，没有则返回 null。 */
    @Nullable
    public static Ammo getAmmo(Player player, ItemStack heldStack) {
        ItemStack ammoStack = player.getProjectile(heldStack);
        if (ammoStack.isEmpty()) {
            return null;
        }
        IceAmmoType type = IceAmmoType.of(ammoStack);
        return type == null ? null : new Ammo(ammoStack, type);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        Ammo ammo = getAmmo(player, heldStack);

        if (ammo == null) {
            // 没有弹药，什么也不做（也不进冷却）
            return InteractionResultHolder.pass(heldStack);
        }

        ItemStack ammoStack = ammo.stack();
        IceAmmoType type = ammo.type();

        Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(
                player, hand == InteractionHand.MAIN_HAND, new Vec3(0.75D, -0.15D, 1.5D));
        Vec3 correction = ShootableGadgetItemMethods.getGunBarrelVec(
                        player, hand == InteractionHand.MAIN_HAND, new Vec3(-0.05D, 0.0D, 0.0D))
                .subtract(player.position().add(0.0D, player.getEyeHeight(), 0.0D));
        Vec3 lookVec = player.getLookAngle();
        Vec3 motion = lookVec.add(correction).normalize()
                .scale(BASE_VELOCITY)
                .scale(type.velocityMultiplier())
                .scale(Config.PROJECTILE_SPEED.get() / 2.0D);

        if (level.isClientSide) {
            // 客户端只负责表现：枪口粒子 + 后坐力动画
            IceCannonClientHooks.onFire(player, hand, ammoStack, barrelPos, motion);
            return InteractionResultHolder.success(heldStack);
        }

        float damage = type.damage() * Config.DAMAGE_MULTIPLIER.get().floatValue();
        float knockback = type.knockback();
        // 力量 / 冲击附魔的加成与土豆炮一致
        if (level instanceof ServerLevel serverLevel) {
            HolderLookup.RegistryLookup<Enchantment> lookup =
                    serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int power = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.POWER), heldStack);
            int punch = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.PUNCH), heldStack);
            damage *= 1.0F + power * 0.2F;
            knockback += punch * 0.5F;
        }

        for (int i = 0; i < type.split(); i++) {
            IceChargeEntity charge = new IceChargeEntity(ModEntities.ICE_CHARGE.get(), level);
            charge.setItem(ammoStack.copy());
            charge.setStats(damage, knockback, i == 0 ? type.recoveryChance() : 0.0F);

            Vec3 splitMotion = motion;
            if (type.split() > 1) {
                // 多发时向四周散开一点
                splitMotion = motion.add(
                        (level.getRandom().nextDouble() - 0.5D) * 0.2D,
                        (level.getRandom().nextDouble() - 0.5D) * 0.2D,
                        (level.getRandom().nextDouble() - 0.5D) * 0.2D);
            }

            charge.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
            charge.setDeltaMovement(splitMotion);
            charge.setOwner(player);
            level.addFreshEntity(charge);
        }

        if (Config.CONSUME_AMMO.get() && !player.isCreative()) {
            ammoStack.shrink(1);
            if (ammoStack.isEmpty()) {
                player.getInventory().removeItem(ammoStack);
            }
        }

        // 耐久消耗；没耐久时不会损坏（物品默认设了耐久）
        heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

        ShootableGadgetItemMethods.applyCooldown(player, heldStack, hand,
                s -> s.getItem() instanceof IceCannonItem, type.reloadTicks());

        return InteractionResultHolder.success(heldStack);
    }

    // ---------------- 弹药 / 姿态（与土豆炮一致） ----------------

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return AMMO_PREDICATE;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
                                   float inaccuracy, float angle, @Nullable LivingEntity target) {
        // 走自己的 use() 逻辑，这里不需要额外实现
    }

    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon,
                         List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit,
                         @Nullable LivingEntity target) {
        // 同上
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || newStack.getItem() != oldStack.getItem();
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.INFINITY)) {
            return false;
        }
        return enchantment.is(Enchantments.LOOTING) || super.supportsEnchantment(stack, enchantment);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        return !player.swinging ? ArmPose.CROSSBOW_HOLD : null;
    }

    // ---------------- 悬停提示：显示当前弹药数值 ----------------

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Ammo ammo = getAmmo(player, stack);
        if (ammo == null) {
            tooltip.add(Component.translatable("item.create_ice_plus.ice_cannon.no_ammo")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        IceAmmoType type = ammo.type();
        float damageMult = 1.0F;
        float knockbackAdd = 0.0F;

        var registries = context.registries();
        if (registries != null) {
            var lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
            int power = stack.getEnchantmentLevel(lookup.getOrThrow(Enchantments.POWER));
            int punch = stack.getEnchantmentLevel(lookup.getOrThrow(Enchantments.PUNCH));
            damageMult += power * 0.2F;
            knockbackAdd += punch * 0.5F;
        }

        ChatFormatting normal = ChatFormatting.DARK_GREEN;
        ChatFormatting boosted = ChatFormatting.GREEN;
        float damage = type.damage() * Config.DAMAGE_MULTIPLIER.get().floatValue() * damageMult;

        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(Component.translatable(ammo.stack().getDescriptionId())
                .append(Component.literal(":")).withStyle(ChatFormatting.GRAY));

        MutableComponent indent = CommonComponents.space().plainCopy();
        tooltip.add(indent.copy().append(Component.translatable("item.create_ice_plus.ice_cannon.damage",
                        formatNumber(damage)).withStyle(damageMult > 1.0F ? boosted : normal)));
        tooltip.add(indent.copy().append(Component.translatable("item.create_ice_plus.ice_cannon.reload",
                Component.literal(String.valueOf(type.reloadTicks()))).withStyle(normal)));
        tooltip.add(indent.copy().append(Component.translatable("item.create_ice_plus.ice_cannon.knockback",
                        formatNumber(type.knockback() + knockbackAdd))
                .withStyle(knockbackAdd > 0.0F ? boosted : normal)));
    }

    private static MutableComponent formatNumber(float value) {
        return Component.literal(value == Math.floor(value)
                ? String.valueOf((int) value)
                : String.valueOf(value));
    }

    /** 当前装载的弹药：物品栈 + 对应的类型。 */
    public record Ammo(ItemStack stack, IceAmmoType type) {
    }
}
