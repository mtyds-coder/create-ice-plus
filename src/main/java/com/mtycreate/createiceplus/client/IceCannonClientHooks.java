package com.mtycreate.createiceplus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 只在客户端运行的实物表现代码，从 {@code IceCannonItem#use} 里通过 isClientSide 分支调用。
 *
 * <p>单独放一个类，是为了让物品类本身不直接引用客户端类型。
 */
public final class IceCannonClientHooks {
    private IceCannonClientHooks() {
    }

    /** 开火时的枪口粒子 + 后坐力动画。 */
    public static void onFire(Player player, InteractionHand hand, ItemStack ammoStack,
                              Vec3 barrelPos, Vec3 motion) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        // 枪口：两簇弹药碎屑 + 寒气
        for (int i = 0; i < 2; i++) {
            Vec3 debris = motion.scale(0.1D).add(
                    (level.random.nextDouble() - 0.5D) * 0.05D,
                    (level.random.nextDouble() - 0.5D) * 0.05D,
                    (level.random.nextDouble() - 0.5D) * 0.05D);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, ammoStack),
                    barrelPos.x, barrelPos.y, barrelPos.z, debris.x, debris.y, debris.z);

            Vec3 air = motion.scale(2.0D).add(
                    (level.random.nextDouble() - 0.5D) * 0.5D,
                    (level.random.nextDouble() - 0.5D) * 0.5D,
                    (level.random.nextDouble() - 0.5D) * 0.5D);
            level.addParticle(ParticleTypes.SNOWFLAKE,
                    barrelPos.x, barrelPos.y, barrelPos.z, air.x, air.y, air.z);
        }

        IceCannonRenderHandler.INSTANCE.shoot(hand, barrelPos);
    }
}
