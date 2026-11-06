package com.mtycreate.createiceplus;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组通用配置。
 *
 * <p>文件位置：{@code <游戏目录>/config/create_ice_plus-common.toml}
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue DAMAGE_MULTIPLIER = BUILDER
            .comment("所有弹药伤害的倍率。三种弹药的基础伤害：冰 6、浮冰 9、蓝冰 12。")
            .defineInRange("damageMultiplier", 1.0D, 0.0D, 10.0D);

    public static final ModConfigSpec.DoubleValue PROJECTILE_SPEED = BUILDER
            .comment("冰弹的基础初速度，原版雪球为 1.5，机械动力土豆炮为 2.0。")
            .defineInRange("projectileSpeed", 2.0D, 0.1D, 10.0D);

    public static final ModConfigSpec.BooleanValue CONSUME_AMMO = BUILDER
            .comment("是否消耗背包里的冰作为弹药。关闭后可以无限发射（创造模式本来就不消耗）。")
            .define("consumeAmmo", true);

    public static final ModConfigSpec.IntValue FREEZE_TICKS = BUILDER
            .comment("冰弹命中生物时造成的冻结时间（游戏刻），同时附带等时长的缓慢效果。")
            .defineInRange("freezeTicks", 60, 0, 600);

    static final ModConfigSpec SPEC = BUILDER.build();
}
