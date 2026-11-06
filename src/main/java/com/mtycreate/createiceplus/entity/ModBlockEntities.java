package com.mtycreate.createiceplus.entity;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.mtycreate.createiceplus.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateIcePlus.MOD_ID);

    /**
     * 冰齿轮 / 冰大齿轮共用的方块实体类型。
     *
     * <p>逻辑复用 Create 的 {@code BracketedKineticBlockEntity}；给它单独注册类型，
     * 是为了让渲染走自己的 {@code IceCogWheelRenderer}，而不是 Create 的 Flywheel visual
     * （后者的模型写死，没法换贴图）。
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IceCogWheelBlockEntity>> ICE_COGWHEEL =
            BLOCK_ENTITY_TYPES.register("ice_cogwheel", () -> BlockEntityType.Builder
                    .of(IceCogWheelBlockEntity::new,
                            ModBlocks.ICE_COGWHEEL.get(),
                            ModBlocks.ICE_LARGE_COGWHEEL.get())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
