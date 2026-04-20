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
   - 重组作用域
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
   - `snapshotFlow`
   - `SideEffect`
   - `produceState`
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

## 重组作用域

核心结论：

- Compose 里有"重组作用域"这个概念
- 状态变化时，不一定整个 Composable 都重组
- Compose 会尽量只重组读取该状态的那块作用域。状态在哪个作用域里被读取，哪个作用域就更可能重组
- `SideEffect` 是否执行，也取决于它所在作用域是否真的重组



它是什么：

- 这里的"作用域"不是 Kotlin 变量作用域
- 这里说的是 Compose 用来决定"哪一块需要重新执行"的边界



为什么需要它：

- 如果没有这个机制，一个状态变化可能让整页都重新执行
- Compose 想做的是：只更新真正受影响的部分



直觉例子：

```kotlin
@Composable
fun Screen() {
    var count by remember { mutableIntStateOf(0) }

    Column {
        Text("标题")
        Button(onClick = { count++ }) {
            Text("count = $count")
        }
    }
}
```

- `count` 只在按钮里的 `Text` 被读取
- `count` 变化时，Compose 更倾向于只重组和这块读取相关的范围

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

# 副作用与异步

## LaunchedEffect

核心结论：

- `LaunchedEffect` 是 Compose 提供的副作用 API
- 它适合启动和当前 UI 生命周期绑定的协程
- `LaunchedEffect` 内部的协程会在进入组合后自动启动，离开组合后自动取消



副作用：

- 副作用指的不是单纯描述 UI 的代码
- 启动协程、发请求、倒计时、订阅这类额外动作，都属于副作用



什么时候使用：

- 页面进入后自动加载一次数据
- 页面出现后开始倒计时
- 某个 key 变化后自动发起异步任务



`LaunchedEffect`使用例子：

```kotlin
LaunchedEffect(Unit) {
    while (true) {
        delay(1000)
        seconds++
    }
}
```

- `LaunchedEffect(Unit)` 常用于页面进入后的首次加载、倒计时、自动刷新

```kotlin
LaunchedEffect(keyword) {
    searchResult = "搜索中..."
    delay(700)
    searchResult = "\"$keyword\" 的结果已返回"
}
```

- `LaunchedEffect(keyword)` 常用于搜索、防抖、根据条件变化自动刷新
- `keyword` 变化时，旧协程会取消，再按新的 `keyword` 启动新协程



`LaunchedEffect` 和组合的关系 例子：

```kotlin
@Composable
fun Outer(showDemo: Boolean, showEffect: Boolean) {
    if (showDemo) {
        Demo(showEffect)
    }
}

@Composable
fun Demo(showEffect: Boolean) {
    if (showEffect) {
        LaunchedEffect(Unit) {
        }
    }
}
```

- 情况 1：`showDemo` 一直是 `true`，`showEffect` 从 `false -> true`，这次组合结果里第一次出现这个 `LaunchedEffect`，它内部的协程会启动
- 情况 2：`showDemo` 一直是 `true`，`showEffect` 从 `true -> false`，这次组合结果里这个 `LaunchedEffect` 消失，旧协程会取消
- 情况 3：一开始 `showDemo = true`，`showEffect = true`，协程已经启动；后来 `showDemo = false`，`Demo()` 整块离开组合，里面的 `LaunchedEffect` 也会一起消失，旧协程会取消



`LaunchedEffect` 和组合的关系 结论：

- `LaunchedEffect` 是否启动和取消，取决于它对应的调用点还在不在当前组合结构里
- `LaunchedEffect` 进入组合：它内部的协程启动
- `LaunchedEffect` 离开组合：它内部的协程取消
- `LaunchedEffect` 的 key 变化：取消旧协程，按新的 key 重启

## DisposableEffect

核心结论：

- `DisposableEffect` 适合注册资源，并在离开组合或 key 变化时清理资源
- 它不负责启动协程，重点是 `onDispose`



什么时候使用：

- 注册监听器
- 绑定回调
- 添加观察者
- 接入需要手动释放的对象



最小例子：

```kotlin
DisposableEffect(listenerId) {
    registerListener(listenerId)

    onDispose {
        unregisterListener(listenerId)
    }
}
```

- 进入组合：执行注册逻辑
- key 变化：先清理旧资源，再按新 key 重新注册
- 离开组合：执行 `onDispose`



典型场景：

```kotlin
// 注册监听器
DisposableEffect(locationManager) {
    val listener = LocationListener { location ->
    }

    locationManager.register(listener)

    onDispose {
        locationManager.unregister(listener)
    }
}
```

```kotlin
// 绑定回调
DisposableEffect(player) {
    val callback = object : PlayerCallback {
        override fun onPlay() {
        }
    }

    player.setCallback(callback)

    onDispose {
        player.clearCallback()
    }
}
```

```kotlin
// 添加观察者
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
    }

    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}
```

```kotlin
// 接入需要手动释放的对象
DisposableEffect(cameraController) {
    cameraController.startPreview()

    onDispose {
        cameraController.stopPreview()
        cameraController.release()
    }
}
```

## snapshotFlow

核心结论：

- `snapshotFlow` 可以把 Compose 状态变化转成 Flow
- 它主要用于观察 Compose state，不是普通变量



什么时候使用：

- 输入框内容变化后做搜索或防抖
- 滚动位置变化后做上报或联动（把滚动状态当成一个持续变化的数据源，拿来做统计，或者驱动别的界面状态变化）
- 选择状态变化后继续走 Flow 处理链



最小例子：

```kotlin
LaunchedEffect(Unit) {
    snapshotFlow { keyword }
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .collectLatest { value ->
            latestLog = "\"$value\" 触发了一次收集"
        }
}
```

- `snapshotFlow { keyword }`：观察 `keyword` 这个 Compose 状态
- `filter { it.isNotBlank() }`：过滤空字符串
- `distinctUntilChanged()`：相同值不重复往下传
- `collectLatest { value -> ... }`：如果前一个值的处理还没结束，新值来了就取消前一个，优先处理最新值



和 Compose 状态的关系：

- `snapshotFlow` 观察的是 block 里读取到的 Compose 状态
- 如果只是普通 `String` 或普通变量，它不会像 Compose state 那样被持续观察



作用：

- 把 Compose 状态接入 Flow 的处理链
- 让状态变化可以继续配合 `filter`、`distinctUntilChanged`、`collectLatest` 这类操作符使用

## SideEffect

> 参考：Stack Overflow: [Android Compose side effects: Side-Effect behavior](https://stackoverflow.com/questions/78071347/android-compose-side-effects-side-effect-behavior)

核心结论：

- `SideEffect` 适合在每次成功重组后，把最新状态同步给外部对象
- 它不是协程 API，也不是资源清理 API
- 它是否执行，取决于它所在的重组作用域是否真的发生了重组



什么时候使用：

- 同步 analytics / 埋点 SDK 的当前状态
- 把最新 Compose 状态同步给旧 View 或外部 holder
- 做轻量的外部状态桥接



最小例子：

```kotlin
SideEffect {
    analytics.setCurrentScreen(screenName)
}
```

- 每次成功重组后，把最新 `screenName` 同步给外部对象



`Counter2` 例子：

```kotlin
@Composable
fun Counter2() {
    var counter by remember { mutableStateOf(0) }

    SideEffect {
        Log.d("Test tag", "Counter2: $counter")
    }

    Column {
        Button(onClick = { counter++ }) {
            Text("Increase count is: $counter")
        }
    }
}
```

点击按钮后，`SideEffect` 可能不会继续执行。原因：

- `counter` 的读取发生在 `Button` 里的 `Text` 那层
- `Button` 的内容 lambda 会形成更内层作用域
- 点击按钮后，更内层作用域重组时，外层放着 `SideEffect` 的作用域可能被跳过
- `SideEffect` 不在那层真正重组的作用域里，所以可能不会执行



`Counter1` 例子：

```kotlin
@Composable
fun Counter1() {
    var counter by remember { mutableStateOf(0) }

    SideEffect {
        Log.d("Test tag", "Counter1: $counter")
    }

    Column {
        Button(onClick = { counter++ }) {
            Text("Increase count")
        }
        Text("Counter value is: $counter")
    }
}
```

`Counter1`：点击按钮后，`SideEffect` 会继续执行。原因：

- `counter` 被外面的 `Text("Counter value is: $counter")` 读取
- **那么不是 `Column` 也形成了作用域吗？`Column` 是 `inline` 的，这里的内容会直接使用外围作用域。因为`SideEffect`所在的那个重组作用域真的发生了重组，因此`SideEffect`内部代码会执行**。

## produceState

核心结论：

- `produceState` 可以把一段异步生产过程直接包装成 Compose 可读取的 `State`
- 它适合"我要一个给 UI 直接读取的结果"这种场景



什么时候使用：

- 根据某个 key 异步加载一个结果给 UI
- 想把“异步过程 -> UI 状态”收在一起
- 想直接得到一个可读的 `State`



最小例子：

```kotlin
val result by produceState(
    initialValue = "等待加载...",
    key1 = keyword
) {
    value = "加载中..."
    delay(700)
    value = "\"$keyword\" 的异步结果"
}
```

- `initialValue`：初始值
- `key1 = keyword`：`keyword` 变化后，内部生产逻辑会按新的 key 重新开始
- `value = ...`：在 block 里直接更新要暴露给 UI 的状态



和 `LaunchedEffect` 的区别：

- `LaunchedEffect`：重点是执行一段协程逻辑
- `produceState`：重点是得到一个给 UI 直接读取的状态结果

```kotlin
var result by remember { mutableStateOf("等待加载...") }

LaunchedEffect(keyword) {
    result = "加载中..."
    delay(700)
    result = "\"$keyword\" 的结果"
}
```

```kotlin
val result by produceState(
    initialValue = "等待加载...",
    key1 = keyword
) {
    value = "加载中..."
    delay(700)
    value = "\"$keyword\" 的结果"
}
```

- 想执行协程逻辑，用 `LaunchedEffect`
- 想把异步逻辑直接包装成 UI 状态，用 `produceState`

## 小结

| API | 核心作用 | 什么时候用 |
| --- | --- | --- |
| `LaunchedEffect` | 启动和当前 UI 生命周期绑定的协程 | 页面进入后加载、倒计时、key 变化后的异步任务 |
| `DisposableEffect` | 注册资源，并在离开组合或 key 变化时清理 | 监听器、回调、观察者、需要手动释放的对象 |
| `snapshotFlow` | 把 Compose 状态变化转成 Flow | 搜索、防抖、滚动状态监听、状态流处理 |
| `SideEffect` | 每次成功重组后，把最新状态同步给外部对象 | analytics、旧 View 桥接、外部 holder 同步 |
| `produceState` | 把异步生产过程直接包装成 `State` | 根据 key 异步加载结果给 UI、收口异步状态 |


