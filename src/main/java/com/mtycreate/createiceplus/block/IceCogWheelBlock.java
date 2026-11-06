package com.mtycreate.createiceplus.block;

import com.mtycreate.createiceplus.entity.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 冰齿轮 / 冰大齿轮。
 *
 * <p>直接继承 Create 的 {@link CogWheelBlock}，所以动能网络、轴向传动、
 * 齿轮啮合、扳手旋转、支架（bracket）这些行为全部白拿；唯一要改的是
 * 方块实体类型——指向我们自己的 {@link ModBlockEntities#ICE_COGWHEEL}，
 * 这样外观就能由自己的渲染器画（Create 的齿轮是 Flywheel visual 渲染，
 * 模型写死在 {@code AllPartialModels} 里，换不了贴图）。
 */
public class IceCogWheelBlock extends CogWheelBlock {
    public IceCogWheelBlock(boolean large, BlockBehaviour.Properties properties) {
        super(large, properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ICE_COGWHEEL.get();
    }
}
