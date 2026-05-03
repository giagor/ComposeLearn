分几篇笔记合适，笔记文件名怎么起。尽量每篇笔记内部的内容是关联的

建议分 **4 篇**，刚好按内部关联度切开：

3. `docs/compose_ui/learn5-compose-rendering-pipeline-notes.md`

主题：组合 / 测量 / 布局 / 绘制原理

内容放：

- 组合阶段、测量阶段、布局阶段、绘制阶段分别做什么
- `LayoutNode`
- `MeasurePolicy`
- `Modifier` 在测量、布局、绘制里的参与方式
- 自定义 `Layout`、`drawBehind`、`Canvas`、`graphicsLayer` 和这些阶段的关系
- 状态变化为什么可能只影响某个阶段

这一篇聚焦 Compose 自己的 UI 渲染管线。

4. `docs/compose_ui/learn6-compose-vs-view-pipeline-notes.md`

主题：Compose 与传统 View 测量 / 布局 / 绘制的关系

内容放：

- View 的 `measure / layout / draw`
- Compose 的测量 / 布局 / 绘制
- 两者相同点和不同点
- `AndroidComposeView` 的作用
- Compose 如何挂到 Android View 系统里
- `AndroidView` / `ComposeView` 在这条链路里的位置

这一篇专门做对比，避免前一篇讲 Compose 原理时被传统 View 体系打断。

整体顺序：

```text
learn3 @Composable 与组合原理
-> learn4 Snapshot 系统
-> learn5 Compose 渲染管线
-> learn6 Compose 与 View 管线对比
```

如果你想更紧凑，也可以合并成 3 篇，把第 4 篇并进第 3 篇末尾。但我更建议 4 篇，因为 **Compose 自己怎么渲染** 和 **它跟传统 View 怎么接上** 是两个问题，分开会清楚很多。