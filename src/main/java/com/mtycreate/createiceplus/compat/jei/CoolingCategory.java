package com.mtycreate.createiceplus.compat.jei;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 「冷却」在 JEI 里的分类。
 *
 * <p>在 Create 的 {@link BasinCategory} 基础上只改两处视觉：
 * <ul>
 *   <li>底部温度条不再显示「无需加热」，改成冰蓝色的「冷却」</li>
 *   <li>工作盆下方画一个蓝冰方块（原版会在同一个位置画烈焰人燃烧室）</li>
 * </ul>
 *
 * <p>槽位布局、搅拌器动画、阴影和箭头全部沿用 Create 的实现。
 */
public class CoolingCategory extends BasinCategory {
    /** 冰蓝色，比 Create 的加热色更冷。 */
    private static final int COOLING_COLOR = 0x5BB8E8;

    private final AnimatedMixer mixer = new AnimatedMixer();
    private final AnimatedCoolingBlock coolant = new AnimatedCoolingBlock();

    public CoolingCategory(CreateRecipeCategory.Info<BasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        // 和原版给燃烧室留的槽位同一个坐标，改为显示蓝冰
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                .addItemStack(new ItemStack(Items.BLUE_ICE));
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        int rows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;
        if (rows <= 2) {
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (rows - 1) + 32);
        }
        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        AllGuiTextures.JEI_NO_HEAT_BAR.render(graphics, 4, 80);

        // 蓝色「冷却」文字（原版这里显示「无需加热 / 加热 / 超级加热」）
        graphics.drawString(Minecraft.getInstance().font,
                Component.translatable("create_ice_plus.jei.cooling"),
                9, 86, COOLING_COLOR, false);

        int centerX = this.getBackground().getWidth() / 2 + 3;
        this.mixer.draw(graphics, centerX, 34);
        this.coolant.draw(graphics, centerX, 55);
    }
}
