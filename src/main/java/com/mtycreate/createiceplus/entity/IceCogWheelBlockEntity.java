package com.mtycreate.createiceplus.entity;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 冰齿轮的方块实体。
 *
 * <p>逻辑全部继承自 Create 的 {@link BracketedKineticBlockEntity}（动能网络、速度、
 * 支架都白拿），这里只是给它一个双参数构造器，好让 {@code BlockEntityType.Builder.of}
 * 能创建它，并获得属于本模组的方块实体类型。
 */
public class IceCogWheelBlockEntity extends BracketedKineticBlockEntity {
    public IceCogWheelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICE_COGWHEEL.get(), pos, state);
    }
}
