package com.mtycreate.createiceplus.recipe;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 本模组的配方类型注册。 */
public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateIcePlus.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateIcePlus.MOD_ID);

    /** 冷却配方的序列化器，复用 Create 的 StandardProcessingRecipe.Serializer。 */
    public static final DeferredHolder<RecipeSerializer<?>, StandardProcessingRecipe.Serializer<CoolingRecipe>>
            COOLING_SERIALIZER = SERIALIZERS.register("cooling",
                    () -> new StandardProcessingRecipe.Serializer<>(CoolingRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<CoolingRecipe>> COOLING_TYPE =
            TYPES.register("cooling", () -> RecipeType.simple(CreateIcePlus.asResource("cooling")));

    /** 给 CoolingRecipe 的父类构造器用的类型描述。 */
    public static final IRecipeTypeInfo COOLING_TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CreateIcePlus.asResource("cooling");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) COOLING_SERIALIZER.get();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) COOLING_TYPE.get();
        }
    };

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
