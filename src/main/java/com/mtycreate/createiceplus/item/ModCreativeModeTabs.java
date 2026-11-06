package com.mtycreate.createiceplus.item;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.mtycreate.createiceplus.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateIcePlus.MOD_ID);

    public static final Supplier<CreativeModeTab> CREATE_ICE_PLUS =
            CREATIVE_MODE_TABS.register("create_ice_plus", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.ICE_CANNON.get()))
                    .title(Component.translatable("itemGroup.create_ice_plus"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ICE_CANNON.get());
                        output.accept(ModItems.ICE_CRYSTAL_MECHANISM.get());
                        output.accept(ModBlocks.ICE_COGWHEEL_ITEM.get());
                        output.accept(ModBlocks.ICE_LARGE_COGWHEEL_ITEM.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
