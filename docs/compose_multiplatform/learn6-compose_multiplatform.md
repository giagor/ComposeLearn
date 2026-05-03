# Compose Multiplatform 学习笔记

# 相关文档

- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Compose Multiplatform compatibility and versioning](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Tencent ovCompose sample](https://github.com/Tencent-TDS/ovCompose-sample)
- [Tencent ovCompose multiplatform core](https://github.com/Tencent-TDS/ovCompose-multiplatform-core)
- [Tencent KuiklyUI](https://github.com/Tencent-TDS/KuiklyUI)

# Jetpack Compose 是否支持跨平台

核心结论：

- Android 开发里常说的 **Jetpack Compose**，主要指 Android 官方的声明式 UI 框架。
- 真正用于跨平台 UI 的是 **Compose Multiplatform**。
- Compose Multiplatform 由 JetBrains 推动，基于 Kotlin Multiplatform 和 Compose 技术栈。

可以简单区分成：

```text
Jetpack Compose
  主要面向 Android

Compose Multiplatform
  面向 Android / iOS / Desktop / Web 等多平台
```

Compose Multiplatform 当前官方主要支持：

- Android
- iOS
- Desktop：Windows、macOS、Linux
- Web：Wasm / JS 浏览器环境

所以更准确地说：

```text
Compose 这套声明式 UI 技术可以跨平台。
Android 语境里的 Jetpack Compose 本身主要面向 Android。
要做跨平台 UI，通常使用 Compose Multiplatform。
```

# Compose Multiplatform 的跨平台原理

核心结论：

**Compose Multiplatform 不是把 Android App 打包运行到其他平台，也不是 WebView 套壳，而是让 Kotlin / Compose 代码编译到不同平台，再接入不同平台的渲染和系统能力。**

整体链路可以粗略理解成：

```text
@Composable UI 代码
    ↓
Compose Runtime
    ↓
重组 / 状态跟踪
    ↓
Compose UI 树 / Layout / Modifier / 事件系统
    ↓
不同平台的渲染后端
    ↓
Android / iOS / Desktop / Web 屏幕
```

## Kotlin Multiplatform 负责代码运行到多平台

Compose Multiplatform 依赖 Kotlin Multiplatform。

同一份 Kotlin 代码可以编译到不同目标：

- Android：JVM / Android
- iOS：Kotlin/Native
- Desktop：JVM
- Web：Kotlin/Wasm 或 Kotlin/JS

例如：

```kotlin
@Composable
fun App() {
    Text("Hello Compose Multiplatform")

    Button(onClick = {}) {
        Text("Click")
    }
}
```

这类 `@Composable` UI 代码可以在多个平台上复用。

## Compose Runtime 负责统一声明式 UI 模型

Compose 的状态系统、重组机制、`@Composable` 函数模型是通用的。

例如：

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

当 `count` 改变时，Compose Runtime 会知道哪些 UI 读取了这个状态，然后触发相关部分重新组合。

这套机制不是 Android 独有的，因此可以被 Compose Multiplatform 复用到其他平台。

## 不同平台有不同渲染后端

Compose Runtime 负责描述 UI 应该是什么样，真正画到屏幕上需要平台渲染后端。

大致可以理解为：

```text
Compose Runtime 管状态和重组
Compose UI 管布局、Modifier、输入、语义等
平台后端负责窗口、图形、文本、事件、生命周期等接入
```

不同平台的实现方式不同：

- Android：接入 Android 系统能力。
- Desktop：通常基于 JVM 和 Skia 渲染窗口内容。
- iOS：通过 Kotlin/Native 运行逻辑，并接入 iOS 容器和图形能力。
- Web：通过 Wasm / JS 在浏览器中运行和渲染。

## Skia 是重要基础

在很多非 Android 平台上，Compose Multiplatform 会依赖 Skia 作为 2D 图形渲染引擎。

Skia 负责处理：

- 文字绘制
- 图形绘制
- 图片绘制
- 路径绘制
- Canvas 能力

Skia 也是 Chrome、Android、Flutter 等生态里常见的图形库。

## 通常不是原生控件逐个映射

Compose Multiplatform 通常不是这种模式：

```text
Button -> iOS UIButton
TextField -> iOS UITextField
```

更接近这种模式：

```text
Button 的 Compose 描述
    ↓
Compose 自己计算布局、状态和交互
    ↓
通过 Skia 或平台图形层画出来
```

这样做的优点：

- UI 代码复用度高。
- 多平台 UI 一致性强。
- Compose 的声明式模型可以跨平台保留。

需要关注的地方：

- 平台原生体验差异。
- 输入法、焦点、文本选择。
- 无障碍能力。
- 系统控件行为。
- 平台生命周期和导航集成。

# Compose Multiplatform 是否支持鸿蒙

核心结论：

**官方 Compose Multiplatform 目前没有把 HarmonyOS / OpenHarmony 作为正式支持平台。**

官方支持平台主要还是：

```text
Android
iOS
Desktop: Windows / macOS / Linux
Web: Wasm / JS
```

因此，如果想像下面这样直接加一个鸿蒙 target：

```kotlin
kotlin {
    harmonyMain()
}
```

目前官方并不支持。

## Android 兼容层场景

如果是支持 Android APK 兼容层的 HarmonyOS 设备，Android 版 Compose App 可能可以运行。

但这种方式本质上仍然是：

```text
Android App 跑在兼容层里
```

它不等于 Compose Multiplatform 原生支持鸿蒙。

## HarmonyOS NEXT / 纯血鸿蒙

对于不再依赖 Android 兼容层的 HarmonyOS NEXT / 纯血鸿蒙，Compose Multiplatform 不能直接运行。

要真正支持，需要解决：

- Kotlin 编译到鸿蒙目标。
- Compose Runtime 和 UI 层适配鸿蒙。
- 窗口、输入、生命周期接入。
- 文本、字体、资源、打包适配。
- 图形渲染后端适配。

# 如果要支持鸿蒙，可能的技术路线

## 路线一：Android 兼容层运行

这是成本最低的方式：

```kotlin
kotlin {
    androidTarget()
}
```

优点：

- 改造成本低。
- 可以继续使用 Android Jetpack Compose。
- 对已有 Android 项目比较友好。

缺点：

- 依赖 Android 兼容层。
- 不适用于 HarmonyOS NEXT / 纯血鸿蒙。
- 不是真正的鸿蒙原生支持。

## 路线二：共享业务逻辑，鸿蒙 UI 单独写

这是目前更稳的工程路线：

```text
shared/commonMain
  网络、数据库、状态管理、领域逻辑

androidMain
  Compose UI

iosMain
  Compose UI 或 SwiftUI

harmony
  ArkTS / ArkUI UI
```

也就是：

```text
Kotlin Multiplatform 共享非 UI 逻辑
鸿蒙侧用 ArkTS / ArkUI 写原生 UI
```

优点：

- 工程风险低。
- 更符合鸿蒙官方开发方式。
- UI 层可以更好地贴合鸿蒙系统能力。

缺点：

- UI 代码不能完全复用。
- 多端 UI 需要维护不同实现。

## 路线三：给 Compose Multiplatform 做鸿蒙后端

这是最接近“真正支持鸿蒙”的路线，但工程量最大。

需要补齐的链路：

```text
Kotlin 编译目标
    ↓
Compose Runtime
    ↓
Compose UI / Layout / Modifier / Text / Input
    ↓
鸿蒙窗口、事件、生命周期适配
    ↓
Skia 或鸿蒙图形 API 渲染
    ↓
HarmonyOS 原生应用包
```

主要难点：

- Kotlin/Native 是否稳定支持 OpenHarmony / HarmonyOS 目标。
- Skia 或其他图形后端如何接入鸿蒙。
- Ability / Stage 模型如何接入 Compose 生命周期。
- 点击、手势、键盘、输入法、焦点如何适配。
- 中文文本排版、字体 fallback、emoji、光标、选区如何处理。
- Compose 资源如何映射到鸿蒙工程和打包产物。

一句话：

```text
短期商用更适合 KMP 共享逻辑 + ArkUI 写鸿蒙 UI。
长期框架级支持需要 Kotlin/Native 鸿蒙 target + Compose 鸿蒙渲染后端。
```

# 社区和开源探索

虽然官方没有支持鸿蒙，但社区和企业已经有一些探索。

## ovCompose

腾讯 TDS 开源的 ovCompose 是比较贴近“让 Compose Multiplatform 支持鸿蒙”的项目：

- [ovCompose-sample](https://github.com/Tencent-TDS/ovCompose-sample)
- [ovCompose-multiplatform-core](https://github.com/Tencent-TDS/ovCompose-multiplatform-core)

它的大致方向是：

```text
Kotlin / Compose 代码
    ↓
定制 Kotlin / Compose 工具链
    ↓
ohosArm64 target
    ↓
编译出 .so
    ↓
通过 NAPI / ArkUI 容器接入鸿蒙 App
    ↓
依赖 Skia / skikobridge / compose.har 等组件渲染
```

这说明鸿蒙上的 Compose 适配并不是完全不可行，但它属于社区 / 企业级探索，不是官方 Compose Multiplatform 的正式 target。

适合关注的问题：

- 工具链是否稳定。
- IDE 和调试体验如何。
- 是否支持常用 Compose 组件。
- 文本、输入法、焦点、无障碍是否足够完善。
- 是否适合正式业务长期维护。

## Kuikly / Kuikly Compose DSL

[KuiklyUI](https://github.com/Tencent-TDS/KuiklyUI) 是另一个值得关注的方向。

它不是“原版 Compose Multiplatform 直接跑鸿蒙”，而是基于 Kotlin Multiplatform 的跨端框架，支持 Android、iOS、HarmonyOS、Web、小程序等，并提供 Compose 风格 DSL。

可以理解成：

```text
Kotlin + Compose 风格 DSL
    ↓
Kuikly 跨端框架
    ↓
各平台原生渲染
    ↓
鸿蒙侧接入 ArkUI / 原生能力
```

它更偏业务落地型跨端框架，而不是 JetBrains 官方 Compose Multiplatform 的鸿蒙后端。

## 官方 issue

JetBrains Compose Multiplatform 仓库里有人提过 HarmonyOS support 的需求：

- [JetBrains/compose-multiplatform#4155](https://github.com/JetBrains/compose-multiplatform/issues/4155)

但这不代表官方已经支持鸿蒙。从目前官方兼容性文档看，HarmonyOS / OpenHarmony 还不在正式支持平台中。

# 总结

可以按目标选择路线：

```text
学习 Compose 跨平台原理：
  重点理解 Kotlin Multiplatform + Compose Runtime + 平台渲染后端。

做 Android / iOS / Desktop / Web：
  优先研究官方 Compose Multiplatform。

做鸿蒙原生商用：
  更稳的是 KMP 共享业务逻辑，鸿蒙 UI 用 ArkTS / ArkUI 单独写。

探索 Compose 跑鸿蒙：
  可以关注腾讯 ovCompose。

想要 Kotlin 跨鸿蒙业务落地：
  可以关注 Kuikly。
```

最终结论：

**Compose Multiplatform 的跨平台本质是共享声明式 UI 模型和状态运行机制，再由不同平台后端完成渲染与系统接入。鸿蒙目前不是官方支持平台，但已经有 ovCompose、Kuikly 等社区和企业级探索。**
