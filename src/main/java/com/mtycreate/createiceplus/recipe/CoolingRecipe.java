package com.mtycreate.createiceplus.recipe;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

/**
 * 「冷却」配方。
 *
 * <p>本质是一个 {@link BasinRecipe}（所以能直接走工作盆的整套处理流程：抽取材料、
 * 产出物品、播放特效），区别只在于它的注册类型是 {@code create_ice_plus:cooling}，
 * 而且<b>不会</b>被 Create 自己的工作盆主动检索——只有
 * {@link com.mtycreate.createiceplus.mixin.BasinOperatingBlockEntityMixin}
 * 检测到工作盆下方是冷却源时，才会把它加进候选列表。
 *
 * <p>配方 JSON 里的 {@code heat_requirement} 保持默认的 {@code none}，
 * 否则会被 Create 的加热条件拦住。
 */
public class CoolingRecipe extends BasinRecipe {
    public CoolingRecipe(ProcessingRecipeParams params) {
        super(ModRecipes.COOLING_TYPE_INFO, params);
    }
}
