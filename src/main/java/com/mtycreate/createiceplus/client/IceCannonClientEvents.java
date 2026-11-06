package com.mtycreate.createiceplus.client;

import com.mtycreate.createiceplus.CreateIcePlus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 游戏总线（非 mod 总线）上的客户端事件。
 *
 * <p>后坐力动画的衰减是按 tick 推进的，所以每 tick 要让渲染处理器衰减一次，
 * 否则动画会一直停在最后一帧。
 */
@EventBusSubscriber(modid = CreateIcePlus.MOD_ID, value = Dist.CLIENT)
public class IceCannonClientEvents {
    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        IceCannonRenderHandler.INSTANCE.tick();
    }
}
