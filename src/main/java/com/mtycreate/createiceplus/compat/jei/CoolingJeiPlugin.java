package com.mtycreate.createiceplus.compat.jei;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.mtycreate.createiceplus.recipe.CoolingRecipe;
import com.mtycreate.createiceplus.recipe.ModRecipes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * JEI 支持：把「冷却」配方显示在 JEI 里。
 *
 * <p>直接复用机械动力自己的 {@link MixingCategory}，所以外观（搅拌器动画、工作盆、
 * 温度条）与机械动力的「混合」分类完全一致，不需要另外画 UI。
 *
 * <p>这个类只在 JEI 存在时才会被 JEI 的插件加载器实例化，
 * 因此不需要在 {@code neoforge.mods.toml} 里声明 JEI 依赖。
 */
@JeiPlugin
public class CoolingJeiPlugin implements IModPlugin {
    /** JEI 侧的分类标识。 */
    private static final RecipeType<RecipeHolder<BasinRecipe>> COOLING_TYPE =
            RecipeType.createRecipeHolderType(CreateIcePlus.asResource("cooling"));

    @Override
    public ResourceLocation getPluginUid() {
        return CreateIcePlus.asResource("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        IDrawable background = guiHelper.createBlankDrawable(177, 103);
        IDrawable icon = guiHelper.createDrawableItemStack(AllBlocks.MECHANICAL_MIXER.asStack());

        List<Supplier<? extends ItemStack>> catalysts = List.of(
                AllBlocks.BASIN::asStack,
                AllBlocks.MECHANICAL_MIXER::asStack,
                () -> new ItemStack(Items.BLUE_ICE));

        CreateRecipeCategory.Info<BasinRecipe> info = new CreateRecipeCategory.Info<>(
                COOLING_TYPE,
                Component.translatable("create_ice_plus.jei.cooling"),
                background,
                icon,
                CoolingJeiPlugin::coolingRecipes,
                catalysts);

        registration.addRecipeCategories(new CoolingCategory(info));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(COOLING_TYPE, coolingRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 让玩家在 JEI 里搜这几个物品时也能翻到冷却配方
        registration.addRecipeCatalyst(AllBlocks.BASIN.get(), COOLING_TYPE);
        registration.addRecipeCatalyst(AllBlocks.MECHANICAL_MIXER.get(), COOLING_TYPE);
        registration.addRecipeCatalyst(Items.BLUE_ICE, COOLING_TYPE);
    }

    /** 取出所有冷却配方，并适配成 {@link BasinRecipe} 的持有者列表。 */
    private static List<RecipeHolder<BasinRecipe>> coolingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        List<RecipeHolder<BasinRecipe>> result = new ArrayList<>();
        for (RecipeHolder<CoolingRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.COOLING_TYPE.get())) {
            result.add(new RecipeHolder<>(holder.id(), holder.value()));
        }
        return result;
    }
}
