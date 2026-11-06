package com.mtycreate.createiceplus.client;

import com.mtycreate.createiceplus.CreateIcePlus;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

/**
 * 本模组自己的 partial model（Flywheel 的 {@link PartialModel}）。
 *
 * <p>Flywheel 的 {@code PartialModelEventHandler} 会自动把这里注册过的模型
 * 加进模型管理器，所以只要保证这些静态字段在模型烘焙之前完成初始化即可
 * （见 {@link #init()}）。
 */
public final class ModPartialModels {
    /** 冰齿轮的完整模型（含中心轴）。 */
    public static final PartialModel ICE_COGWHEEL =
            PartialModel.of(CreateIcePlus.asResource("block/ice_cogwheel"));

    /** 冰大齿轮的完整模型。 */
    public static final PartialModel ICE_LARGE_COGWHEEL =
            PartialModel.of(CreateIcePlus.asResource("block/ice_large_cogwheel"));

    private ModPartialModels() {
    }

    /** 触发静态初始化。必须在模型注册事件之前调用。 */
    public static void init() {
        // 方法体刻意留空
    }
}
