> 内容：@Composable 与组合原理

# 学习路线

1. `@Composable` 的作用与编译器改写
   - 理解 `@Composable` 标记的函数和普通 Kotlin 函数有什么不同。
   - 理解 Compose Compiler 如何改写 Composable 函数，让 Runtime 能参与函数调用过程。

2. Composer 作用及来源
   - 理解 Composer 是组合过程里的运行时协作者。
   - 理解最顶层 Composer 由 Runtime 创建，后续由编译器改写后的调用链继续向下传递。
   - 重点看它如何记录结构、对齐旧组合、保存和读取运行时数据。

3. 组合阶段做什么
   - 理解组合阶段负责决定"有哪些 UI"。
   - 重点看 Composable 执行、group 记录、结构对齐、插入、删除、跳过。

4. 和 Slot Table / group / remember / key 的关系
   - 理解 Slot Table 是组合阶段留下的结构和数据记录。
   - 理解 group 记录结构，slot 保存 `remember` 数据，key 帮助组合身份对齐。

# @Composable 的作用与编译器改写

核心结论：

- `@Composable` 让一个 Kotlin 函数从普通执行逻辑，变成可以被 Compose Runtime 追踪的 UI 声明逻辑。
- Composable 函数不是执行后返回一个 View，而是在当前组合上下文里声明一段 UI 结构。
- `@Composable` 也是给 Compose Compiler 的信号，编译器会改写函数签名和调用点。
- 改写后的 Composable 函数会携带 `Composer` 等运行时信息。
- Runtime 借助 `Composer` 记录组合结构、恢复 `remember` 状态、判断是否需要重组或跳过。

## 和普通函数的区别

普通函数：

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

特点：

- 调用一次，执行一次。
- 输入参数进去，返回结果出来。
- Kotlin 运行时不会关心这个函数调用形成了什么 UI 结构。
- 调用结束后，普通局部变量就结束了。

Composable 函数：

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }

    Text("count = $count")
    Button(onClick = { count++ }) {
        Text("+1")
    }
}
```

特点：

- 它会在 Composition 里执行。
- 执行过程中，Compose Runtime 会记录它声明了哪些 UI。
- 它可以调用 `remember`，让局部状态跨重组保留。
- 状态变化后，它可能被重新执行，也就是重组。
- 它的调用位置会变成 Runtime 可以识别的组合结构。

不要把：

```kotlin
Text("Hello")
```

理解成：

```text
new TextView(...)
```

更应该理解成：

```text
在 Compose 的组合结构里声明一个 Text 节点
```

Compose 不是每个 `Text`、`Button` 都对应一个 Android View。Compose 会维护自己的 UI 结构，后续再进入测量、布局、绘制流程。

一句话：

```text
普通函数：执行逻辑
@Composable 函数：声明 UI 结构，并让 Runtime 能记录、重组、恢复状态
```

## 编译器大概做了什么

我们写的代码：

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello $name")
}
```

概念上可以粗略理解成：

```kotlin
fun Greeting(
    name: String,
    composer: Composer,
    changed: Int
) {
    composer.startGroup(...)
    Text("Hello $name", composer, ...)
    composer.endGroup()
}
```

这不是源码逐字等价，只是帮助理解。

重点：

- 你写的时候像普通函数。

- 编译后，它不再只是普通 Kotlin 调用。

- 编译器会把 `Composer` 这类运行时对象传进去。

- `Composer` 才能知道现在执行到哪个 Composable、参数有没有变化、这段结构从哪里开始、到哪里结束。

- Runtime 也才能记录 group、读取 slot、决定是否跳过或重组。

  

普通函数调用，Runtime 看不见结构：

```kotlin
fun Screen() {
    Header()
    Content()
}
```

Kotlin 只会正常调用 `Header()` 和 `Content()`，不会自动留下 "这是 UI 结构" 的记录。

Composable 函数调用，编译器会让 `Composer` 参与：

```kotlin
@Composable
fun Screen() {
    Header()
    Content()
}
```

编译器会让这些调用变成 `Composer` 可参与的调用：

```text
start Screen group
  start Header group
  end Header group
  start Content group
  end Content group
end Screen group
```

这也是 Slot Table 后续能记录 group / slot 的前提。

核心链路：

```text
@Composable 是标记
-> Compose Compiler 看到标记后改写函数
-> 改写后的函数会携带 Composer
-> Composer 在运行时记录组合结构
-> Slot Table 保存这些结构和状态
```

# Composer 作用及来源

核心结论：

- Composer 是 Compose Runtime 在组合过程里的核心协作者。
- 它不是 UI 组件，也不是 View。
- 它由 Compose Runtime 创建，并由 Compose Compiler 改写后的 Composable 调用链继续向下传递。
- 它负责执行组合、记录结构、恢复 `remember`、判断跳过和产出变化。

可以先粗略理解成：

```text
Composer = 组合过程里的记录员和调度员
```

## 作用

1. 记录组合结构

```kotlin
@Composable
fun Screen() {
    Column {
        Text("Hello")
        Button(onClick = {}) {
            Text("OK")
        }
    }
}
```

组合时会记录类似结构：

```text
Screen group
  Column group
    Text group
    Button group
      Text group
```

这些 group 最后会落到 Slot Table 里。

2. 对齐旧组合和新组合

状态变化后，Composable 会重新执行。

Composer 需要判断：

```text
这次执行到的 Text，是不是上次那个 Text
这次执行到的 Button，是不是上次那个 Button
某个 if 分支是不是消失了
某个列表 item 是不是换位置了
```

能对齐，就复用旧 group / slot。对不齐，就插入、删除或移动。

3. 配合 `remember` 读写 slot

```kotlin
var count by remember { mutableIntStateOf(0) }
```

执行到 `remember` 时：

```text
如果当前 slot 里有旧值：复用
如果当前 slot 里没有旧值：执行 block 创建新值，再写入 slot
```

所以 `remember` 不是存在函数局部变量里，而是通过 Composer 和 Slot Table 找回来的。

4. 判断能不能跳过某段组合

如果某个 Composable 的参数没变，并且它是稳定可跳过的，Composer 可能会跳过这段函数体执行。

也就是：

```text
不是每次父级重组，所有子 Composable 都必须重新执行
```

5. 记录变化，交给后续阶段应用

组合阶段不是直接把所有 UI 立即画出来。

Composer 会把这次组合产生的变化记录下来，例如：

```text
插入节点
删除节点
更新参数
移动 group
```

后面再应用到 Compose 的 UI 树里，然后进入测量、布局、绘制。

Composer 和 Slot Table 的关系：

```text
Composer 是操作 Slot Table 的主要角色之一
Slot Table 是结构和状态的存储表
```

组合过程可以粗略理解成：

```text
Composable 执行
-> Composer 参与执行过程
-> 记录 group / 读取 slot / 对齐结构
-> 更新 Slot Table
-> 产出 UI 变化
```

## 参数来源

我们原来写的代码：

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello $name")
}
```

用 Android Studio 反编译后，可能看到类似代码：

```java
public static final void Greeting(
    @NotNull String name,
    @Nullable Composer $composer,
    int $changed
) {
    Intrinsics.checkNotNullParameter(name, "name");

    $composer = $composer.startRestartGroup(429914052);
    int $dirty = $changed | ($composer.changed(name) ? 4 : 2);

    if ($composer.shouldExecute(...)) {
        TextKt.Text("Hello " + name, ..., $composer, ...);
    } else {
        $composer.skipToGroupEnd();
    }

    ScopeUpdateScope scope = $composer.endRestartGroup();
    if (scope != null) {
        scope.updateScope((composer, force) -> {
            Greeting(
                name,
                composer,
                RecomposeScopeImplKt.updateChangedFlags($changed | 1)
            );
        });
    }
}
```

上面是简化后的反编译示意，省略了 trace、sourceInformation 和 `Text` 的大量默认参数。重点看：

- 函数签名里多了 `Composer $composer` 和 `int $changed`。
- 开始时调用 `startRestartGroup(...)`。
- 通过 `$composer.changed(name)` 判断参数是否变化。
- `Text(...)` 调用时继续把 `$composer` 传下去。
- 可以调用 `skipToGroupEnd()` 跳过当前 group。
- 结束时调用 `endRestartGroup()`，并通过 `updateScope(...)` 记录后续重组时怎么重新调用 `Greeting`。

这里的 `$composer` 不是手动传的，而是 Compose Compiler 改写调用链后自动传的。



如果外层这样调用：

```kotlin
@Composable
fun Screen() {
    Greeting("Compose")
}
```

概念上编译后类似：

```kotlin
fun Screen(composer: Composer, changed: Int) {
    Greeting("Compose", composer, ...)
}
```

也就是：

```text
Screen 拿到 Composer
-> Screen 调 Greeting 时，把同一个 Composer 传给 Greeting
-> Greeting 调 Text 时，再把 Composer 传给 Text
```

所以 `Greeting` 里的 `$composer` 通常来自它的外层 Composable 调用点。

继续往外追，`Screen` 的 `composer` 参数又从哪里来？

最开始的入口通常是：

```kotlin
setContent {
    Screen()
}
```

`setContent { ... }` 会创建 Composition，背后会有 Recomposer、Composition、Applier、SlotTable 等运行时对象。

当 Compose Runtime 开始执行这棵 Composable 内容时，它会创建或持有一个 `ComposerImpl`，然后调用传进去的 composable lambda。

概念链路：

```text
Activity.setContent { ... }
-> 创建 ComposeView / AndroidComposeView
-> 创建 Composition
-> Recomposer 驱动组合
-> Runtime 创建 / 使用 ComposerImpl
-> 执行 Composable lambda
-> Composer 一路传进 Screen / Greeting / Text
```

总结：

```text
Greeting 的 Composer 来自外层 Composable 调用点
外层 Composable 的 Composer 继续来自更外层调用点
最顶层的 Composer 由 Compose Runtime 创建并传入
后续每一层 Composable 调用里的 Composer，由 Compose Compiler 改写后的调用链继续往下传
```

这也是普通函数不能随便调用 Composable 的原因：

```kotlin
fun normal() {
    Greeting("Compose") // 不行
}
```

普通函数没有处在 Composable 调用链里，手上没有这个隐式的 `Composer` 上下文。
