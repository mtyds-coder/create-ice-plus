package com.mtycreate.createiceplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mtycreate.createiceplus.block.IceCogWheelBlock;
import com.mtycreate.createiceplus.entity.IceCogWheelBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

/**
 * 冰齿轮的方块渲染器。
 *
 * <p>Create 的齿轮由 Flywheel visual 渲染、模型写死在 {@code AllPartialModels} 里，
 * 贴图换不了，所以这里自己画。为了让转向、转速、齿相对齐都和原版齿轮一致，
 * 完全照抄 Create 的 {@code rotating.vert} 着色器逻辑：
 *
 * <pre>
 * degrees = offset + renderSeconds * speed          // speed = 转速(RPM) * 6，单位 度/秒
 * pos' = R(正轴向, degrees) * (R(朝向) * (pos - 0.5)) + 0.5
 * </pre>
 *
 * <p>关键点：<b>朝向必须把模型 +Y 映射到「正轴向」（+X / +Y / +Z）</b>。
 * 之前用方块状态的 {@code BlockModelRotation}（{@code rotationYXZ}）写法，
 * 对 axis=x 会把 +Y 映射到 −X，导致水平齿轮转向差 180°、看起来是反着转。
 */
public class IceCogWheelRenderer implements BlockEntityRenderer<IceCogWheelBlockEntity> {
    /** Create 的换算：rotationalSpeed = speed(RPM) * 6（度/秒），再除以 20 刻 = 0.3 度/刻。 */
    private static final float DEGREES_PER_RPM_TICK = 0.3F;

    public IceCogWheelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(IceCogWheelBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        boolean large = state.getBlock() instanceof IceCogWheelBlock cogwheel && cogwheel.isLargeCog();
        var partial = large ? ModPartialModels.ICE_LARGE_COGWHEEL : ModPartialModels.ICE_COGWHEEL;

        Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
        Direction positive = Direction.get(AxisDirection.POSITIVE, axis);

        // 和 Create 一样：贴图相位偏移 + 齿轮自身的角度偏移
        float offset = KineticBlockEntityVisual.rotationOffset(state, axis, be.getBlockPos())
                + (float) be.getRotationAngleOffset(axis);
        float degrees = (AnimationTickHolder.getRenderTime() * be.getSpeed() * DEGREES_PER_RPM_TICK
                + offset) % 360.0F;

        // 朝向：把模型自身的 +Y 转到方块的正轴向
        Quaternionf orientation = new Quaternionf().rotateTo(
                0.0F, 1.0F, 0.0F,
                positive.getStepX(), positive.getStepY(), positive.getStepZ());
        // 自转：绕世界空间的正轴向（着色器里 kineticRot 是在朝向之后应用的）
        Quaternionf spin = new Quaternionf().rotateAxis(
                (float) Math.toRadians(degrees),
                positive.getStepX(), positive.getStepY(), positive.getStepZ());

        SuperByteBuffer model = CachedBuffers.partial(partial, state);
        model.light(packedLight);

        poseStack.pushPose();
        // PoseStack 的每次 mulPose 都是右乘，所以这里要反着写：
        // 目标 T(0.5) · spin · orientation · T(-0.5)
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(spin);
        poseStack.mulPose(orientation);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.cutoutMipped());
        model.renderInto(poseStack, consumer);

        poseStack.popPose();
    }
}
