# 学习路线

1. 整体渲染管线
   - 理解组合 / 测量 / 布局 / 绘制分别做什么。
   - 理解状态变化可能影响不同阶段。
   - 本篇会结合关键源码入口理解流程，但不做完整源码逐行分析。

2. Compose UI 树和 LayoutNode
   - 理解 Composable 不等于 Android View。
   - 结合 `LayoutNode` 看 Compose 如何承接测量、布局、绘制。

3. 测量、布局与绘制原理
   - 结合 `MeasurePolicy`、`MeasureScope`、`Placeable` 理解 measure / place。
   - 结合 `Canvas`、`drawBehind`、`drawWithContent` 理解绘制入口。

4. Modifier 与传统 View 的关系
   - 结合 `Modifier.Node` 理解 Modifier 如何参与不同阶段。
   - 结合 `AndroidComposeView` 理解 Compose 如何挂到 Android View 系统。
