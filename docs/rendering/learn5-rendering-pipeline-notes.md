# 学习路线

> 本篇会结合关键源码入口理解流程，但不做完整源码逐行分析

1. 整体渲染管线
   - 理解组合 / 测量 / 布局 / 绘制分别做什么。
   - 理解状态变化可能影响不同阶段。
2. Compose UI 树和 LayoutNode
   - 理解 Composable 不等于 Android View。结合Compose后的Android View树长什么样
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

# Compose UI 树和 LayoutNode

核心结论：

- Composable 不是 Android View。
- Compose 会维护自己的 UI 树，真正参与测量、布局、绘制的核心节点是 `LayoutNode`。
- `LayoutNode` 是 Compose 源码里的真实类：`androidx.compose.ui.node.LayoutNode`。
- Compose 最终通过 `ComposeView` / `AndroidComposeView` 挂到 Android View 系统里。

## LayoutNode

`LayoutNode` 可以理解成：

```text
Compose UI 树里的布局 / 绘制节点
```

它主要承接：

- 子节点关系。
- Modifier 链。
- measure。
- layout。
- draw。
- semantics 等信息。

例如：

```kotlin
Column {
    Text("Ada")
    Text("VIP")
}
```

可以粗略理解成：

```text
LayoutNode(Column)
  LayoutNode(Text "Ada")
  LayoutNode(Text "VIP")
```

这只是心智模型，不是说每个 Composable 都一定对应一个 `LayoutNode`。

例如：

```kotlin
@Composable
fun UserName(name: String) {
    Text(name)
}
```

`UserName` 通常只是函数封装，不一定产生自己的 UI 节点。真正可能产生 UI 节点的是里面的 `Text(name)`。

关键点：

```text
Composable 调用很多
LayoutNode 只对应真正参与布局 / 绘制的 UI 节点
```

不要理解成：

```text
一个 Composable = 一个 LayoutNode
```

一句话：

```text
LayoutNode 是 Compose 自己的 UI 节点；Composable 用来声明 UI，LayoutNode 承接后续测量、布局、绘制。
```

## Android View 树

当前工程打印出来的 View 树：

```text
com.android.internal.policy.DecorView id=-1
  android.widget.LinearLayout id=-1
    android.view.ViewStub id=16908782
    android.widget.FrameLayout id=16908290
      androidx.compose.ui.platform.ComposeView id=-1
        androidx.compose.ui.platform.AndroidComposeView id=-1
          android.view.View id=-1
```

这棵 View 树说明：

- Activity 里不是每个 Composable 都变成了 Android View。

- 传统 View 树里主要看到的是 `ComposeView` 和它内部的 `AndroidComposeView`。

- `Column`、`Text`、`Button` 这些 Compose 内容主要存在于 Compose 内部的 `LayoutNode` 树里。

  

`ComposeView`：

- 暴露给 Android View 体系的 Compose 容器 View。
- 由 `Activity.setContent { ... }` 内部创建，并通过 `setContentView` 放进 `android.R.id.content`。
- 保存传入的 `@Composable content`。
- 管理 Composition 的创建和销毁策略。
- 对外表现为一个 Android `ViewGroup`。

设置时机：

```kotlin
// MainActivity.kt
setContent {
    ComposeLearnTheme {
        ...
    }
}

// ComponentActivity.setContent
public fun ComponentActivity.setContent(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
  	...
    ComposeView(this).apply {
        // Set content and parent **before** setContentView
        // to have ComposeView create the composition on attach
        setParentCompositionContext(parent)
        setContent(content)
        // Set the view tree owners before setting the content view so that the inflation process
        // and attach listeners will see them already present
        setOwners()
        setContentView(this, DefaultActivityContentLayoutParams)
    }
}
```



`AndroidComposeView`：

- `ComposeView` 内部创建的 Compose Owner View。
- 承载 Compose 的 `LayoutNode` 树。
- 把 Android 的 measure / layout / draw / input / accessibility 等事件接到 Compose。

设置时机：

```kotlin
setContent {
    ComposeLearnTheme {
        Scaffold { ... }
    }
}
```

```text
1. Activity.onCreate 调用 setContent { ... }
2. activity-compose 创建 ComposeView
3. setContentView(ComposeView)，ComposeView 被放进 Activity 的 content FrameLayout
4. ComposeView attach 到 window 后，或者首次 measure 时创建 Composition
5. 创建 Composition 时，内部创建 AndroidComposeView
6. AndroidComposeView 持有 Compose 的 root LayoutNode
7. 后续 Compose 内容通过 LayoutNode 树测量、布局、绘制
```



可以粗略理解成：

```text
ComposeView：外层容器，给 Android View 树看的
AndroidComposeView：内部承载者，给 Compose UI 系统干活的
```

最后一层 `AndroidComposeView -> android.view.View` 通常是 Compose 内部为了某些 Android 平台能力挂的辅助 View，不代表你的 Composable 子节点变成了传统 View。
