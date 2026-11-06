package com.mtycreate.createiceplus;

import com.mtycreate.createiceplus.client.IceCannonRenderHandler;
import com.mtycreate.createiceplus.client.IceCannonRenderer;
import com.mtycreate.createiceplus.client.IceCogWheelRenderer;
import com.mtycreate.createiceplus.client.ModPartialModels;
import com.mtycreate.createiceplus.entity.ModBlockEntities;
import com.mtycreate.createiceplus.entity.ModEntities;
import com.mtycreate.createiceplus.item.ModItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.model.ModelSwapper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 客户端专用入口：实体渲染器、物品自定义渲染、配置界面。
 *
 * <p>这个类不会在专用服务器上加载，所以可以直接引用客户端专属类。
 *
 * <p>mod 总线上的事件都在构造函数里显式注册——{@code @EventBusSubscriber} 的
 * {@code bus} 参数在 NeoForge 1.21.1 已标记为待移除，用它编译会直接报错。
 * 游戏总线上的事件见 {@code IceCannonClientEvents}。
 */
@Mod(value = CreateIcePlus.MOD_ID, dist = Dist.CLIENT)
public class CreateIcePlusClient {
    public CreateIcePlusClient(IEventBus modEventBus, ModContainer container) {
        // 让 NeoForge 自动为本模组的配置生成界面（Mods 界面 -> 本模组 -> Config）
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerClientExtensions);
        modEventBus.addListener(this::onModifyBakingResult);

        // 提前加载 partial model 的静态字段，让它们在模型注册事件之前就存在
        IceCannonRenderer.init();
        ModPartialModels.init();

        // 后坐力动画：接管第一人称的 RenderHandEvent
        IceCannonRenderHandler.INSTANCE.registerListeners(NeoForge.EVENT_BUS);
    }

    /** 冰弹用原版的“投掷物品”渲染器，按物品模型渲染，不需要额外贴图。 */
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ICE_CHARGE.get(), ThrownItemRenderer::new);

        // 冰齿轮用自己的渲染器（Create 的齿轮是 Flywheel visual 渲染，换不了贴图）
        event.registerBlockEntityRenderer(ModBlockEntities.ICE_COGWHEEL.get(),
                IceCogWheelRenderer::new);
    }

    /** 给冰块发射器挂上自定义渲染器，让炮身上的齿轮能转。 */
    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private IceCannonRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new IceCannonRenderer();
                }
                return this.renderer;
            }
        }, ModItems.ICE_CANNON.get());
    }

    /**
     * 把冰块发射器的烘焙模型包一层 {@link CustomRenderedItemModel}，
     * 这样 Create 才会把渲染交给 {@link IceCannonRenderer}。
     */
    private void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ModelSwapper.swapModels(event.getModels(),
                ModelSwapper.getItemModelLocation(ModItems.ICE_CANNON.get()),
                CustomRenderedItemModel::new);
    }
}
