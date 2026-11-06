package com.mtycreate.createiceplus.entity;

import com.mtycreate.createiceplus.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 冰块发射器射出的冰弹。
 *
 * <p>参照 Create 的 {@code PotatoProjectileEntity}：数值由发射时的弹药种类写入，
 * 命中造成伤害 + 击退 + 冻结，并按回收概率把弹药掉回地上。
 */
public class IceChargeEntity extends ThrowableItemProjectile {
    private float damage = 6.0F;
    private float knockback = 0.0F;
    private float recoveryChance = 0.0F;

    public IceChargeEntity(EntityType<? extends IceChargeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public IceChargeEntity(Level level, LivingEntity shooter) {
        super(ModEntities.ICE_CHARGE.get(), shooter, level);
    }

    /** 由发射方写入本次弹丸的数值。 */
    public void setStats(float damage, float knockback, float recoveryChance) {
        this.damage = damage;
        this.knockback = knockback;
        this.recoveryChance = recoveryChance;
    }

    public float getDamage() {
        return this.damage;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ICE;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 12; i++) {
                this.level().addParticle(ParticleTypes.SNOWFLAKE,
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.15D,
                        this.random.nextGaussian() * 0.15D,
                        this.random.nextGaussian() * 0.15D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        target.hurt(this.damageSources().thrown(this, owner), this.damage);

        if (target instanceof LivingEntity living) {
            // 额外击退：沿飞行方向推开
            if (this.knockback > 0.0F) {
                Vec3 dir = this.getDeltaMovement().normalize();
                living.knockback(this.knockback, -dir.x, -dir.z);
            }

            int freezeTicks = Config.FREEZE_TICKS.get();
            if (freezeTicks > 0) {
                living.setTicksFrozen(living.getTicksRequiredToFreeze() + freezeTicks);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, freezeTicks, 1), owner);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) {
            return;
        }

        this.level().broadcastEntityEvent(this, (byte) 3);

        // 按概率把弹药掉回来（对应 Create 的 potato_recovery 机制）
        ItemStack ammoStack = this.getItem();
        if (this.recoveryChance > 0.0F && !ammoStack.isEmpty()
                && this.random.nextFloat() < this.recoveryChance) {
            ItemEntity recovered = new ItemEntity(this.level(),
                    this.getX(), this.getY(), this.getZ(), ammoStack.copyWithCount(1));
            recovered.setDefaultPickUpDelay();
            this.level().addFreshEntity(recovered);
        }

        this.discard();
    }
}
