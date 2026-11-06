package com.mtycreate.createiceplus.item;

import com.mtycreate.createiceplus.CreateIcePlus;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateIcePlus.MOD_ID);

    /**
     * 冰块发射器：右键消耗背包里的冰 / 浮冰 / 蓝冰，射出一枚冰弹。
     *
     * <p>耐久按开火次数消耗，和机械动力的土豆炮一致。
     */
    public static final DeferredItem<IceCannonItem> ICE_CANNON = ITEMS.register("ice_cannon",
            () -> new IceCannonItem(new Item.Properties().stacksTo(1).durability(384)));

    /**
     * 冰晶核心构件——对照机械动力的精密构件。
     *
     * <p>由精密构件经序列装配（5 轮「机械手安装蓝冰 → 辊压」）得到，
     * 是冰块发射器机械合成配方的核心部件。
     */
    public static final DeferredItem<Item> ICE_CRYSTAL_MECHANISM = ITEMS.register("ice_crystal_mechanism",
            () -> new Item(new Item.Properties()));

    /**
     * 序列装配过程中的过渡物品（「未完成的冰晶核心构件」）。
     *
     * <p>玩家不会主动获得它，仅在装配线上流转；不进创造模式标签页。
     */
    public static final DeferredItem<Item> INCOMPLETE_ICE_CRYSTAL_MECHANISM =
            ITEMS.register("incomplete_ice_crystal_mechanism", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
