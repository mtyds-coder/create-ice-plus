package com.mtycreate.createiceplus.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;

/**
 * JEI 里画在工作盆下方的蓝冰方块。
 *
 * <p>照着 Create 的 {@code AnimatedBlazeBurner} 的绘制方式写：
 * 同样的视场角（X -15.5°、Y 22.5°）、同样的缩放和局部偏移，
 * 所以蓝冰和原版的烈焰人燃烧室在同一个位置、同一个透视下显示。
 */
public class AnimatedCoolingBlock extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate((float) xOffset, (float) yOffset, 200.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        poseStack.mulPose(Axis.YP.rotationDegrees(22.5F));

        int scale = 23;
        this.blockElement(Blocks.BLUE_ICE.defaultBlockState())
                .atLocal(0.0D, 1.65D, 0.0D)
                .scale((double) scale)
                .render(graphics);

        poseStack.popPose();
    }
}
