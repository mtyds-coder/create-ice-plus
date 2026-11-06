package com.mtycreate.createiceplus;

import com.mojang.logging.LogUtils;
import com.mtycreate.createiceplus.block.ModBlocks;
import com.mtycreate.createiceplus.entity.ModBlockEntities;
import com.mtycreate.createiceplus.entity.ModEntities;
import com.mtycreate.createiceplus.item.ModCreativeModeTabs;
import com.mtycreate.createiceplus.item.ModItems;
import com.mtycreate.createiceplus.recipe.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * 机械动力：冰+ 的主类。
 *
 * <p>具体注册都在各自的注册类里完成，这里只负责把它们挂到 mod 事件总线上。
 */
@Mod(CreateIcePlus.MOD_ID)
public class CreateIcePlus {
    public static final String MOD_ID = "create_ice_plus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateIcePlus(IEventBus modEventBus, ModContainer modContainer) {
        // 物品 / 方块 / 方块实体 / 实体 / 创造模式标签页
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        // 通用配置（伤害、冷却、弹药消耗等）
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Create: Ice Plus — common setup done");
    }

    /** 生成本模组命名空间下的 {@link ResourceLocation}。 */
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
