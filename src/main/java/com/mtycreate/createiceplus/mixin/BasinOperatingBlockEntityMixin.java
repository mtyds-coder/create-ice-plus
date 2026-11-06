package com.mtycreate.createiceplus.mixin;

import com.mtycreate.createiceplus.CreateIcePlus;
import com.mtycreate.createiceplus.ModTags;
import com.mtycreate.createiceplus.recipe.CoolingRecipe;
import com.mtycreate.createiceplus.recipe.ModRecipes;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 让工作盆支持「冷却」：当工作盆正下方一格是冷却源（默认蓝冰）时，
 * 把 {@code create_ice_plus:cooling} 类型的配方加入候选列表。
 *
 * <p>为什么必须用 Mixin：Create 的 {@code HeatCondition} 只有
 * {@code NONE / HEATED / SUPERHEATED}，没有「冷却」这个状态，
 * 且 {@code com.simibubi.create.api} 下没有工作盆热源的注册接口。
 *
 * <p>接入点选在 {@code getMatchingRecipes()} 的返回值上——冷却配方本身是
 * {@link BasinRecipe} 的子类，进入列表后，抽取材料、产出、特效全部由 Create 完成。
 *
 * <p>坐标关系（由 {@code BasinBlock.canSurvive} 与 {@code getBasin()} 决定）：
 * 操作方块在 y，工作盆在 y-2（中间必空一格），冷却源在工作盆下方 y-3。
 *
 * <p>诊断日志：首次检测到冷却源、以及首次匹配到冷却配方时各输出一条 INFO，
 * 便于在 {@code logs/latest.log} 里确认这条链路是否打通。
 */
@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin {
    @Unique
    private static boolean iceplus$loggedNoBasin;
    @Unique
    private static boolean iceplus$loggedNoCoolant;
    @Unique
    private static boolean iceplus$loggedCoolant;

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"))
    private void iceplus$addCoolingRecipes(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        BasinOperatingBlockEntity self = (BasinOperatingBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos basinPos = self.getBlockPos().below(2);
        if (!(level.getBlockEntity(basinPos) instanceof BasinBlockEntity basin)) {
            if (!iceplus$loggedNoBasin) {
                iceplus$loggedNoBasin = true;
                CreateIcePlus.LOGGER.info(
                        "[ice+] cooling: no basin found at {} (expected 2 blocks below the operating block)",
                        basinPos);
            }
            return;
        }

        BlockPos coolantPos = basinPos.below();
        boolean hasCoolant = level.getBlockState(coolantPos).is(ModTags.COOLANTS);
        if (!hasCoolant) {
            if (!iceplus$loggedNoCoolant) {
                iceplus$loggedNoCoolant = true;
                CreateIcePlus.LOGGER.info(
                        "[ice+] cooling: basin at {}, but block below ({}) is not a coolant",
                        basinPos, level.getBlockState(coolantPos).getBlock());
            }
            return;
        }

        if (!iceplus$loggedCoolant) {
            iceplus$loggedCoolant = true;
            CreateIcePlus.LOGGER.info("[ice+] cooling: coolant detected below basin at {}", basinPos);
        }

        List<Recipe<?>> recipes = cir.getReturnValue();
        for (RecipeHolder<CoolingRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.COOLING_TYPE.get())) {
            CoolingRecipe recipe = holder.value();
            if (recipes.contains(recipe)) {
                continue;
            }
            // 复用 Create 的匹配（检查材料是否够、输出能不能放下、加热条件）
            if (BasinRecipe.match(basin, recipe)) {
                recipes.add(recipe);
                CreateIcePlus.LOGGER.debug("[ice+] cooling: matched recipe {}", holder.id());
            }
        }
    }
}
