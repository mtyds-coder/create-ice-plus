package com.mtycreate.createiceplus.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

/**
 * 冰块发射器支持的弹药种类。
 *
 * <p>对应 Create 里 {@code PotatoCannonProjectileType} 那套数据驱动注册表，
 * 这里简化成一个枚举——效果一致，但不需要额外的 datapack 文件。
 *
 * <p>字段含义与 Create 的土豆炮一致：伤害、装填时间（刻）、击退、初速度倍率、
 * 一次发射的弹丸数量、弹药回收概率。
 */
public enum IceAmmoType {
    ICE(Items.ICE, 6.0F, 10, 0.10F, 1.00F, 1, 0.30F),
    PACKED_ICE(Items.PACKED_ICE, 9.0F, 14, 0.30F, 1.05F, 1, 0.20F),
    BLUE_ICE(Items.BLUE_ICE, 12.0F, 20, 0.50F, 1.10F, 1, 0.10F);

    private final Item item;
    private final float damage;
    private final int reloadTicks;
    private final float knockback;
    private final float velocityMultiplier;
    private final int split;
    private final float recoveryChance;

    IceAmmoType(Item item, float damage, int reloadTicks, float knockback,
                float velocityMultiplier, int split, float recoveryChance) {
        this.item = item;
        this.damage = damage;
        this.reloadTicks = reloadTicks;
        this.knockback = knockback;
        this.velocityMultiplier = velocityMultiplier;
        this.split = split;
        this.recoveryChance = recoveryChance;
    }

    public Item item() {
        return this.item;
    }

    public float damage() {
        return this.damage;
    }

    public int reloadTicks() {
        return this.reloadTicks;
    }

    public float knockback() {
        return this.knockback;
    }

    public float velocityMultiplier() {
        return this.velocityMultiplier;
    }

    public int split() {
        return this.split;
    }

    public float recoveryChance() {
        return this.recoveryChance;
    }

    /** 按物品查弹药类型，不是弹药返回 null。 */
    @Nullable
    public static IceAmmoType of(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (IceAmmoType type : values()) {
            if (stack.is(type.item)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isAmmo(ItemStack stack) {
        return of(stack) != null;
    }
}
