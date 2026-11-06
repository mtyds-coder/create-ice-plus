package com.mtycreate.createiceplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mtycreate.createiceplus.CreateIcePlus;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 冰块发射器的客户端渲染：先画炮身，再在上面画一枚自转的齿轮。
 *
 * <p>齿轮建模时是炮身的一个独立零件（{@code models/item/ice_cannon/cog.json}），
 * 这里把它当作 Flywheel 的 {@link PartialModel} 单独渲染，从而可以让它转起来：
 * 平时慢转，开火后（物品处于冷却期间）快转。
 */
public class IceCannonRenderer extends CustomRenderedItemModelRenderer {
    /** 齿轮的独立模型。Flywheel 的 PartialModelEventHandler 会自动把它注册进模型管理器。 */
    protected static final PartialModel COG = PartialModel.of(CreateIcePlus.asResource("item/ice_cannon/cog"));

    /** 闲置转速（度/游戏刻），1.2 约等于 24 度/秒，转一圈 15 秒。 */
    private static final float IDLE_DEGREES_PER_TICK = 1.2F;
    /** 开火转速（度/游戏刻），配合默认 25 刻冷却，一发约转 450 度。 */
    private static final float FIRING_DEGREES_PER_TICK = 18.0F;
    /** 齿轮的旋转轴心比模型中心高 0.5 个模型单位，也就是 1/32（与 Create 土豆炮一致）。 */
    private static final float PIVOT_OFFSET = 0.03125F;

    private static float angle;
    private static float lastRenderTime = -1.0F;

    /**
     * 触发本类的静态初始化，让 {@link #COG} 在模型烘焙之前就创建出来。
     *
     * <p>必须在客户端初始化阶段调用一次；如果拖到第一次渲染才加载，
     * Flywheel 的注册事件已经过去了，{@code COG.get()} 会拿到 missing model。
     */
    public static void init() {
        // 仅用于触发静态字段初始化，方法体保持为空
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource buffer,
                          int light, int overlay) {
        // 先画炮身（原始烘焙模型）
        renderer.render(model.getOriginalModel(), light);

        // 计算这一帧齿轮应该转到哪
        float now = AnimationTickHolder.getRenderTime();
        if (lastRenderTime < 0.0F) {
            lastRenderTime = now;
        }
        float delta = now - lastRenderTime;
        lastRenderTime = now;
        // 防止切维度/暂停后一次性转一大圈
        delta = Math.max(0.0F, Math.min(delta, 5.0F));

        float speed = isFiring(stack) ? FIRING_DEGREES_PER_TICK : IDLE_DEGREES_PER_TICK;
        angle = (angle + speed * delta) % 360.0F;

        // 绕 Z 轴自转，枢轴比模型中心高 1/32
        poseStack.pushPose();
        poseStack.translate(0.0F, PIVOT_OFFSET, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(0.0F, -PIVOT_OFFSET, 0.0F);
        renderer.render(COG.get(), light);
        poseStack.popPose();
    }

    /** 物品处于使用冷却中 = 刚开过火。 */
    private static boolean isFiring(ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getCooldowns().isOnCooldown(stack.getItem());
    }
}
