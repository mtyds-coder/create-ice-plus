package com.mtycreate.createiceplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mtycreate.createiceplus.item.IceCannonItem;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 冰块发射器的第一人称表现：后坐力动画 + 开火音效。
 *
 * <p>直接复用 Create 的 {@link ShootableGadgetRenderHandler}——它负责接管
 * {@code RenderHandEvent}、重写手臂与物品的姿态，并按 recoil 值做衰减插值。
 * 这里只需要给出物品/手臂各自的变换和音效，效果就和土豆炮一致。
 */
public class IceCannonRenderHandler extends ShootableGadgetRenderHandler {
    public static final IceCannonRenderHandler INSTANCE = new IceCannonRenderHandler();

    @Override
    protected void playSound(InteractionHand hand, Vec3 position) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        AllSoundEvents.FWOOMP.playAt(mc.level, position, 1.0F, 0.9F, false);
    }

    @Override
    protected boolean appliesTo(ItemStack stack) {
        return stack.getItem() instanceof IceCannonItem;
    }

    /** 物品本身的姿态：略微前移缩小，然后绕 X 轴随 recoil 抬起。 */
    @Override
    protected void transformTool(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.1F, 0.0F, 0.14F);
        ms.scale(0.75F, 0.75F, 0.75F);
        ms.mulPose(Axis.XP.rotationDegrees(recoil * 80.0F));
    }

    /** 手臂的姿态：和土豆炮完全一致。 */
    @Override
    protected void transformHand(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.09F, -0.275F, -0.25F);
        ms.mulPose(Axis.ZP.rotationDegrees(flip * -10.0F));
    }
}
