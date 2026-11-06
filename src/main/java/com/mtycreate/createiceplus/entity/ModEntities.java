package com.mtycreate.createiceplus.entity;

import com.mtycreate.createiceplus.CreateIcePlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CreateIcePlus.MOD_ID);

    /** 冰弹。渲染交给原版的投掷物品渲染器，所以尺寸和原版雪球保持一致。 */
    public static final DeferredHolder<EntityType<?>, EntityType<IceChargeEntity>> ICE_CHARGE =
            ENTITY_TYPES.register("ice_charge", () -> EntityType.Builder
                    .<IceChargeEntity>of(IceChargeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    // 这个字符串只用作 DataFixer 实体树的类型名，必须写成完整 id
                    .build("create_ice_plus:ice_charge"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
