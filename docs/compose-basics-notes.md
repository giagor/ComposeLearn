# 相关文档

- [Build better apps faster with Jetpack Compose](https://developer.android.com/compose)
- [android/compose-samples](https://github.com/android/compose-samples)
- [Compose 中的组件](https://developer.android.com/develop/ui/compose/components?hl=zh-cn)
- [Compose 中的布局](https://developer.android.com/develop/ui/compose/layouts?hl=zh-cn)

配套代码：

- [ComposeLearn](https://github.com/giagor/ComposeLearn)

# 整体介绍

**Compose UI 是对 Android UI 开发方式和 UI 运行机制的一次重构。**

1. **从命令式 UI 到声明式 UI** 
   传统 View 开发里，我们不仅要创建界面，还要在数据变化后手动更新界面，比如 `textView.setText("Hello")`。 
   Compose 更像是 `UI = f(State)`：界面由状态决定。状态变化后，Compose 会自动触发相关 UI 的重新执行，并尽量只更新受影响的部分。

2. **从继承式组件到组合式构建** 
   传统 View 体系强依赖继承，例如 `Button -> TextView -> View`，组件往往会带上很多并不总是需要的能力。 
   Compose 更强调“组合”：界面是由一个个小的可复用函数拼出来的。这样代码更灵活、更容易复用，也更容易按需组织 UI 能力。

3. **从 View 树驱动到 Compose 运行时驱动** 
   Compose 的界面节点不再是传统的 View 对象树，而是由 Compose Runtime 和 UI 系统管理。 
   它仍然有自己的**组合、布局、绘制**流程，但这套机制比传统 View 树更轻量，也更适合声明式更新。再配合编译器和运行时优化，Compose 可以更高效地完成界面更新与渲染。

# 学习路线

1. **基础入门**
   - 常用组件
   - Row / Column / Box
   - 列表、滚动、Material3 基础
   - Modifier 常见用法
3. **核心心智**
   - 声明式 UI 与重组
   - 渲染三阶段：组合 / 布局 / 绘制
   - Modifier 执行顺序与包裹模型
4. **状态管理基础**
   - `mutableStateOf`
   - `remember`
   - `rememberSaveable`
   - 状态提升
   - 单向数据流
5. **副作用与异步**
   - `LaunchedEffect`
   - `DisposableEffect`
   - `SideEffect`
   - `produceState`
   - `snapshotFlow`
6. **性能与进阶状态**
   - `derivedStateOf`
   - Stability
   - 为什么会发生不必要重组
   - 列表性能、延迟加载、避免无效计算
7. **互操作与工程化**
   - Compose 和 View 混编
   - `ComposeView`
   - `AndroidView`
   - 导航、主题、分层架构、ViewModel 配合
8. **底层原理与高阶能力**
   - Slot Table
   - Gap Buffer
   - 自定义 Layout
   - 自定义绘制
   - `graphicsLayer` 和渲染优化

# 基础入门

## 组件和布局

- Compose 是声明式 UI：`UI = f(State)`
- `@Composable` 函数用来声明界面，不是手动操作界面
- 页面通常由很多小 Composable 组合而成



本阶段接触过的组件：

- `Text`：显示文本
- `Button` / `OutlinedButton`：触发操作
- `Card`：承载内容
- `TextField` / `OutlinedTextField`：输入内容
- `Checkbox` / `Switch` / `Slider`：表达和修改状态
- `LinearProgressIndicator`：展示进度
- `AssistChip` / `FilterChip`：展示轻量操作或筛选项



最重要的 4 个布局：

- `Column`：纵向排列
- `Row`：横向排列
- `Box`：叠层布局
- `Spacer`：制造间距

传统布局和 Compose 布局的映射关系：

- `LinearLayout` -> `Row` / `Column`
- `FrameLayout` -> `Box`
- `ScrollView` -> `Column + verticalScroll(...)`
- `HorizontalScrollView` -> `Row + horizontalScroll(...)`
- `RecyclerView` -> `LazyColumn` / `LazyRow`
- `RelativeLayout` -> 没有完全等价的默认容器，通常用 `Row` / `Column` / `Box` 组合替代
- `ConstraintLayout` -> Compose 里也有官方库 `constraintlayout-compose`，但不是默认首选，通常在复杂约束场景下按需使用

对应代码：

- [BasicLayoutsSection](https://github.com/giagor/ComposeLearn/blob/main/app/src/main/java/com/example/composelearn/BasicsLearningScreen.kt#L303)



滚动相关：

- `Column` 默认不滚动
- `Row` 默认不滚动
- 需要滚动时，显式加 `verticalScroll(...)` 或 `horizontalScroll(...)`

常见选择：

- `Column`：内容少，不滚动
- `Column + verticalScroll`：内容少，但要整体滚动
- `LazyColumn`：长列表、数据列表

对应代码：

- [ScrollSection](https://github.com/giagor/ComposeLearn/blob/main/app/src/main/java/com/example/composelearn/BasicsLearningScreen.kt#L392)
- [LazyColumnSection](https://github.com/giagor/ComposeLearn/blob/main/app/src/main/java/com/example/composelearn/BasicsLearningScreen.kt#L495)



容器相关：

- `Surface`：更基础、更通用的容器
- `Card`：更场景化、更语义化的容器


简单记：

- 想包一层背景、圆角、承载面：`Surface`
- 想表达“这是一张卡片”：`Card`

## Modifier

`Modifier` 用来控制：

- 尺寸：`fillMaxWidth()`、`height()`、`size()`
- 间距：`padding()`
- 外观：`background()`、`clip()`、`border()`
- 布局行为：`weight()`、`align()`
- 滚动：`verticalScroll()`、`horizontalScroll()`

最重要的一点：

- `Modifier` 是按顺序执行的
- 顺序不同，效果可能不同

## 状态

本阶段接触到的核心写法：

```kotlin
var name by remember { mutableStateOf("Compose") }
```

理解这句就够了：

- `mutableStateOf(...)` 创建状态
- `remember { ... }` 让状态在重组时保留
- 状态变化会驱动 UI 更新

常见模式：

```kotlin
TextField(
    value = name,
    onValueChange = { name = it }
)
```

也就是：

- 组件接收当前值
- 用户修改时通过回调抛出新值
- 你更新状态
- UI 自动刷新

# 核心心智

## 声明式 UI 与重组

核心结论：

- Compose 更像 `UI = f(State)`
- 状态变了，Compose 会重新执行相关 Composable
- 重组不等于整页重建，谁读取状态，谁就更相关



声明式 UI：

```kotlin
var count by remember { mutableIntStateOf(0) }

Text(
    text = if (count == 0) "还没有点击" else "已经点击了 $count 次"
)

Button(onClick = { count++ }) {
    Text("点击 +1")
}
```

 - 改的是状态，不是手动改控件
 - UI 会自动反映当前状态



重组：

```kotlin
@Composable
fun Screen() {
    var count by remember { mutableIntStateOf(0) }

    Column {
        CounterText(count)
        StaticTitle()
    }
}

@Composable
fun CounterText(count: Int) {
    Text("count = $count")
}

@Composable
fun StaticTitle() {
    Text("我是固定标题")
}
```

- `count` 变化后，`CounterText(count)` 更相关
- `StaticTitle()` 不读取 `count`，通常就没那么相关
- 谁读取状态，谁就更可能跟着重组

## 组合 / 布局 / 绘制

组合：决定有哪些 UI

布局：决定多大、放哪

绘制：决定长什么样

组合：

```kotlin
if (showText) {
    Text("我是可选内容")
}
```

`showText` 改变后，这块 UI 会出现或消失，这是组合层面的变化。



布局：

```kotlin
Box(
    modifier = Modifier.size(if (expanded) 120.dp else 60.dp)
)
```

UI 还在，但大小变了，这是布局层面的变化。



绘制：

```kotlin
Box(
    modifier = Modifier.background(
        if (highlight) Color.Yellow else Color.Gray
    )
)
```

UI 还在，大小和位置也没变，主要变化是颜色或背景，这是绘制层面的变化。

## 重组和三阶段的关系

> 三阶段：组合 / 布局 / 绘制

这一节要说明的是：重组和三阶段不是一回事。

- 重组回答的是：哪些 Composable 要重新执行

- 三阶段回答的是：重新执行后，UI 哪一层真的发生了变化

- 状态变了，先发生重组，重组会重新执行相关 Composable，接着 Compose 再决定这次变化主要影响组合、布局还是绘制

  

重组与组合：

```kotlin
if (showText) {
    Text("Hello")
}
```

`showText` 变了 -> 相关 Composable 重组 -> 这块 UI 直接出现 / 消失 -> 更接近影响组合。



重组与布局：

```kotlin
Box(
    modifier = Modifier.size(if (expanded) 120.dp else 60.dp)
)
```

`expanded` 变了 -> 相关 Composable 重组 -> UI 还在，但尺寸变了 -> 更接近影响布局。



重组与绘制：

```kotlin
Box(
    modifier = Modifier.background(
        if (highlight) Color.Yellow else Color.Gray
    )
)
```

`highlight` 变了 -> 相关 Composable 重组 -> UI 还在，大小位置没变，主要是颜色变了 -> 更接近影响绘制。

## Modifier 执行顺序 / 包裹模型

核心结论：

- Modifier 是链式执行的
- 顺序不同，包裹关系不同，结果也可能不同



未加 Modifier 的原始效果：

```kotlin
Text(
    text = "A"
)

Text(
    text = "B"
)
```

![未添加 Modifier](images/modifier_order_origin.png)



加了 Modifier 后的效果对比：

```kotlin
Text(
    text = "A",
    modifier = Modifier
        .background(Color(0xFFBFDBFE))
        .padding(16.dp)
)

Text(
    text = "B",
    modifier = Modifier
        .padding(16.dp)
        .background(Color(0xFFBFDBFE))
)
```

![添加 Modifier 后的效果](images/modifier_order_compare.png)

# 状态管理基础

## mutableStateOf / remember

核心写法：

```kotlin
var name by remember { mutableStateOf("Compose") }
```

各自负责：

- `mutableStateOf(...)` 创建可观察状态
- `remember { ... }` 让状态在重组时保留
- 状态变化会驱动 UI 更新



加了 `remember`：

```kotlin
@Composable
fun Counter(trigger: Int) {
    var count by remember { mutableIntStateOf(0) }

    Text("count = $count, trigger = $trigger")
}
```

+ `trigger` 变化时，`Counter(trigger)` 也会重组；但因为 `count` 被 `remember` 保住了，所以这份状态对象不会重新创建。

+ 加了 `remember`：下次重组时，继续使用同一个状态对象



不加 `remember`：

```kotlin
@Composable
fun Counter(trigger: Int) {
    var count by mutableIntStateOf(0)

    Text("count = $count, trigger = $trigger")
}
```

+ `trigger` 变化时，`Counter(trigger)` 会重组；这时 `var count by mutableIntStateOf(0)` 会重新执行，状态对象可能重新创建，`count` 就可能回到初始值。

- 不加 `remember`：如果 Composable 因为重组重新执行到这行代码，就会重新创建一份新的 `mutableIntStateOf(0)`

## rememberSaveable

核心写法：

```kotlin
var count by remember { mutableIntStateOf(0) }
var count by rememberSaveable { mutableIntStateOf(0) }
```

各自负责：

- `remember` 让状态在重组时保留
- `rememberSaveable` 在此基础上，进一步处理配置变化后的恢复，例如旋转屏幕后，`rememberSaveable` 的数据通常还能恢复。它更适合输入框内容、当前 tab、简单筛选条件这类需要恢复的 UI 状态。

## 状态提升

核心结论：

- 状态不要总写在组件内部
- 很多时候应该把状态提到外层统一管理



对比：

```kotlin
// 组件内部管理状态
@Composable
fun StatefulCounter() {
    var count by remember { mutableIntStateOf(0) }

    CounterCard(
        label = "内部状态",
        count = count,
        onIncrement = { count++ },
        onReset = { count = 0 }
    )
}
```

```kotlin
// 外部管理状态
@Composable
fun StatelessCounter(
    count: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    CounterCard(
        label = "外部状态",
        count = count,
        onIncrement = onIncrement,
        onReset = onReset
    )
}
```

- 组件内部自己管状态时，组件更完整，但复用和测试会更受限
- 外层统一管理状态时，子组件只负责展示和回调，职责更清晰
- 这就是状态提升：把状态提到更高一层

## 单向数据流

核心结论：

- 状态从父组件传给子组件
- 事件由子组件通过回调抛给父组件
- 单向数据流通常建立在状态提升的基础上



最小例子：

```kotlin
@Composable
fun SearchPage() {
    var query by remember { mutableStateOf("") }
    var submitCount by remember { mutableIntStateOf(0) }

    SearchBox(
        query = query,
        onQueryChange = { query = it },
        onSubmit = { submitCount++ }
    )
}
```

```kotlin
@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    TextField(value = query, onValueChange = onQueryChange)
    Button(onClick = onSubmit) { Text("提交") }
}
```

数据流方向：

- 状态下行：`query` 从父组件传给 `SearchBox`
- 事件上行：`onQueryChange` / `onSubmit` 从 `SearchBox` 回到父组件

几个好处：

- 状态来源更清晰
- 组件职责更单一
- 排查问题和后续扩展更容易
