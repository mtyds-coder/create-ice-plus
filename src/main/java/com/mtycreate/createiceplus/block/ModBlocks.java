package com.mtycreate.createiceplus.block;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateIcePlus.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateIcePlus.MOD_ID);

    /** 冰齿轮。false = 小齿轮。 */
    public static final DeferredBlock<IceCogWheelBlock> ICE_COGWHEEL =
            BLOCKS.register("ice_cogwheel", () -> new IceCogWheelBlock(false, cogwheelProperties()));

    /** 冰大齿轮。true = 大齿轮。 */
    public static final DeferredBlock<IceCogWheelBlock> ICE_LARGE_COGWHEEL =
            BLOCKS.register("ice_large_cogwheel", () -> new IceCogWheelBlock(true, cogwheelProperties()));

    public static final DeferredItem<BlockItem> ICE_COGWHEEL_ITEM =
            ITEMS.register("ice_cogwheel", () -> new CogwheelBlockItem(ICE_COGWHEEL.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ICE_LARGE_COGWHEEL_ITEM =
            ITEMS.register("ice_large_cogwheel", () -> new CogwheelBlockItem(ICE_LARGE_COGWHEEL.get(), new Item.Properties()));

    /** 和 Create 的齿轮保持同样的手感：木质音效、软、不挡光。 */
    private static BlockBehaviour.Properties cogwheelProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.ICE)
                .sound(SoundType.GLASS)
                .strength(0.5F)
                .noOcclusion();
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
