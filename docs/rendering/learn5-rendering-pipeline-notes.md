# 学习路线

> 本篇会结合关键源码入口理解流程，但不做完整源码逐行分析

1. 整体渲染管线
   - 理解组合 / 测量 / 布局 / 绘制分别做什么。
   - 理解状态变化可能影响不同阶段。
2. Compose UI 树和 LayoutNode
   - 理解 Composable 不等于 Android View。
   - 结合 `LayoutNode` 看 Compose 如何承接测量、布局、绘制。
3. 测量、布局与绘制原理
   - 结合 `MeasurePolicy`、`MeasureScope`、`Placeable` 理解 measure / place。
   - 结合 `Canvas`、`drawBehind`、`drawWithContent` 理解绘制入口。
4. Modifier 与传统 View 的关系
   - 结合 `Modifier.Node` 理解 Modifier 如何参与不同阶段。
   - 结合 `AndroidComposeView` 理解 Compose 如何挂到 Android View 系统。

# 整体渲染管线

核心结论：

- Compose 从状态变化到屏幕更新，大体会经过组合、测量、布局、绘制。
- 组合决定有哪些 UI，测量决定多大，布局决定放哪，绘制决定长什么样。
- 状态变化不一定影响整条管线，有些变化影响组合，有些影响测量 / 布局，有些主要影响绘制。

## 组合 / 测量 / 布局 / 绘制

四个阶段的边界：

```text
组合：决定这次有哪些 UI。
测量：决定每个 UI 在约束下多大。
布局：决定已经量好的 UI 放在哪里。
绘制：决定在已确定的位置和大小里画什么。
```

例子：

```kotlin
@Composable
fun UserCard(showBadge: Boolean, name: String) {
    Column {
        Text(name)

        if (showBadge) {
            Text("VIP")
        }
    }
}
```

对应关系：

- `if (showBadge) { Text("VIP") }`：影响组合，UI 结构变了。
- `Modifier.size(100.dp)`：影响测量，节点大小变了。
- `Column` 把子项上下排列：属于布局，决定子项放哪。
- `Modifier.background(Color.Red)` / `Canvas { ... }` / `drawBehind { ... }`：参与绘制，在已有区域里画内容。

关键边界：

- 组合不关心尺寸和像素。
- 测量只决定大小，不决定最终坐标。
- 布局基于测量结果决定位置。
- 绘制不会重新决定占位，只在已有区域里画内容。

## 状态变化影响不同阶段

影响组合：

```kotlin
if (showBadge) {
    Text("VIP")
}
```

`showBadge` 改变后，UI 结构变了。



影响测量 / 布局：

```kotlin
Box(
    Modifier.size(if (expanded) 160.dp else 80.dp)
)
```

`expanded` 改变后，节点还在，但大小变了，可能影响父子布局。



影响绘制：

```kotlin
Box(
    Modifier.background(if (selected) Color.Red else Color.Gray)
)
```

`selected` 改变后，结构和大小可能都没变，只是颜色变了。



一句话：

```text
组合决定结构，测量决定大小，布局决定位置，绘制决定像素。
```

后续要继续回答：

```text
Composable 不是 View，那组合之后的 UI 结构到底交给谁去测量、布局、绘制
```
