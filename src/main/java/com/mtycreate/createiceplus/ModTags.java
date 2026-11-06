package com.mtycreate.createiceplus;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/** 本模组用到的标签。 */
public class ModTags {
    /**
     * 能作为「冷却源」的方块：放在工作盆正下方一格时，工作盆就会执行冷却配方。
     *
     * <p>默认只包含蓝冰，可以在数据包或本模组的标签文件里追加（比如浮冰、冰、雪块）。
     */
    public static final TagKey<Block> COOLANTS =
            BlockTags.create(ResourceLocation.fromNamespaceAndPath(CreateIcePlus.MOD_ID, "coolants"));

    private ModTags() {
    }
}
