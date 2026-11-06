# 机械动力：冰+（Create: Ice Plus）

NeoForge 1.21.1 / Create 6.0.10 的机械动力附属模组。

## 构建与产物约定

- 构建：`gradlew build --offline`（`JAVA_HOME` 需指向 JDK 21）
- 产物：`build/libs/create_ice_plus-<版本>.jar`
- **`build/libs/` 里只覆盖同版本号的文件**；其他版本号的 jar 一律保留，作为历史稳定版存档，
  不要在做新版本时把它们清掉。
- **每次构建新产物前，先询问是否要修改 `mod_version`**，不要擅自升版本号。
  同版本修复 = 覆盖同名文件；内容有实质变化 = 由用户决定是否升号。
- **产物只留在 `build/libs/`，不要自动复制到测试实例或游戏目录**，
  由用户自己决定何时、拷到哪里测试。
- **需要用户做决定时，用选择题形式提问**（列出选项并给出推荐），不要开放式提问。
- **更新日志、说明文档等文本内容直接在对话里给出 Markdown**，不要落地成文件。

## 依赖说明

Create 及其内嵌依赖（Flywheel / Ponder / Registrate）以本地 jar 的形式放在 `libs/` 下，
在 `build.gradle` 里用 `compileOnly` 引入。原因是 `maven.neoforged.net` 在部分网络环境下
被 DNS 污染、`maven.createmod.net` 的 Create 包也常下不来。

`libs/` 里的 jar **不随仓库分发**（已在 `.gitignore` 中排除，约 41 MB，且都是第三方产物）。
clone 之后需要自己放入以下文件才能编译：

| 文件 | 说明 |
|---|---|
| `libs/create-1.21.1-6.0.10.jar` | Create 本体（含 jar-in-jar 的 Flywheel/Ponder/Registrate） |
| `libs/flywheel-neoforge-1.21.1-1.0.6.jar` | 从 Create jar 内解出 |
| `libs/ponder-neoforge-1.0.82+mc1.21.1.jar` | 从 Create jar 内解出 |
| `libs/Registrate-MC1.21-1.3.0+67.jar` | 从 Create jar 内解出 |
| `libs/jei-neoforge-api.jar` | JEI 编译用 API；仅编译需要，**不要**放进游戏 |

- 运行时用的 Create 由 `run/mods/` 里的 jar 提供（两者必须版本一致）
- `libs/_old_6.0.0/` 存放 6.0.0 时代的旧 jar，仅作备份
- 想换回 Maven 依赖：见 `build.gradle` 依赖块里的注释

## 许可证与致谢

本项目以 **MIT License** 发布，见 [LICENSE](LICENSE)。

部分模型与贴图资源基于 [Create](https://modrinth.com/mod/create) 修改（Create 同样以 MIT
许可证发布），包括：

- `assets/create_ice_plus/models/block/ice_cogwheel.json`、`ice_large_cogwheel.json`
  —— 复制自 Create 的 `cogwheel` / `large_cogwheel` 模型几何
- `assets/create_ice_plus/textures/block/ice_*.png` —— 由 Create 的齿轮贴图重绘（亮度映射为冰色）
- `assets/create_ice_plus/textures/item/ice_cannon.png` —— 由 Create 的土豆发射器贴图重绘
- `textures/item/ice_crystal_mechanism.png` —— 由 Create 的精密构件贴图重绘
- 未完成的冰晶核心构件等序列装配逻辑参照 Create 的精密构件配方

底层的模版文件来自 [NeoForged MDK](https://github.com/NeoForged/MDK)（MIT），见
`TEMPLATE_LICENSE.txt`。


## 已实现内容

- **冰块发射器**：照 Create 土豆发射器复刻（弹药制、装填/击退/伤害数值、后坐力动画、
  悬停数值、耐久、力量/冲击附魔加成），炮身有可自转的齿轮（闲置慢转、开火后快转）
- **冰齿轮 / 冰大齿轮**：继承 Create 的 `CogWheelBlock` + `BracketedKineticBlockEntity`，
  真正接入应力网络；外观由自己的 `BlockEntityRenderer` 渲染（贴图改为冰色）

## 环境相关

- `mod_version` 在 `gradle.properties`
- 使用前需设置 `JAVA_HOME=C:\Users\lishi\.jdks\ms-21.0.11`，否则 Gradle 会尝试用 JDK 8 启动而失败
