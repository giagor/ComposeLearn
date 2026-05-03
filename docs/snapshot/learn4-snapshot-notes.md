# 学习路线

1. Snapshot 系统解决什么问题
   - 理解 `mutableStateOf` 为什么不是普通变量。
   - 理解 Compose 怎么知道状态被谁读过，以及状态写入后为什么能通知相关组合失效。

2. Snapshot 的状态模型
   - 理解 StateObject / StateRecord 的基本关系。
   - 理解一个 state 对象为什么可能有多份记录，以及 Snapshot 为什么需要版本。

3. 读写追踪与 apply
   - 理解 Composable 读取 `state.value` 时如何建立依赖。
   - 理解修改 `state.value` 后如何进入 Snapshot 机制。
   - 理解 apply 后如何通知观察者，让相关组合失效。

4. Snapshot、Recomposer、Composer 的协作边界
   - 理解 Snapshot 负责发现 state 变化。
   - 理解 Recomposer 负责安排重组。
   - 理解 Composer 负责重新执行和对齐组合结构。
   - 区分 `remember`、`mutableStateOf`、Slot Table 和 Snapshot 的职责。
   - `remember` 不是状态追踪系统，它只是保存对象。
   - `mutableStateOf` 才是可观察状态。
   - 普通变量变化不会自动触发重组。

# Snapshot 系统解决什么问题

核心结论：

- Snapshot 系统解决的是 Compose state 的读写追踪和一致性问题。
- `mutableStateOf` 创建的是 Compose 可观察状态，不是普通变量。
- Snapshot 让 Compose 知道状态被谁读过，也让状态写入后能通知相关组合失效。

普通变量：

```kotlin
var count = 0
```

特点：

- 改了值，Compose 不知道，也不会自动触发重组。
- 没有读追踪和写通知。

Compose state：

```kotlin
var count by mutableStateOf(0)
```

特点：

- 读取时，Compose 可以知道"当前组合读取了这个 state"。
- 写入时，Compose 可以知道"这个 state 变了"，并把依赖它的组合标记为失效。
- Recomposer 后续会安排相关部分重组。

## 读写追踪

Snapshot 要解决的第一个问题：

```text
状态被谁读过
```

例子：

```kotlin
@Composable
fun CounterText(count: State<Int>) {
    Text("count = ${count.value}")
}
```

组合执行到：

```kotlin
count.value
```

Snapshot 系统可以配合当前组合上下文记录：

```text
这个组合读取了 count 这个 state
```

之后如果：

```kotlin
count.value = 1
```

Snapshot 就有机会知道：

```text
count 变了
之前读过 count 的组合可能需要重组
```

## Snapshot 是版本化读写环境

Snapshot 要解决的第二个问题：

```text
状态什么时候算真正写入成功
```

Compose 的状态读写不是简单裸改字段：

```text
在某个 Snapshot 里读
在某个 Snapshot 里写
写入 apply 后，再把变化通知出去
```

可以先粗略理解成：

```text
Snapshot 是 Compose state 的版本化读写环境
```

它的作用：

- 让状态读写可以被追踪。
- 让写入可以合并并通知观察者。
- 让并发或嵌套状态修改有一致性规则。
- 让 Recomposer 知道哪些组合需要重新执行。

## remember 和 mutableStateOf 的分工

```kotlin
var count by remember { mutableStateOf(0) }
```

分工：

```text
remember：把 mutableStateOf(0) 这个 state 对象在重组间保存下来
mutableStateOf：创建可观察状态，让读写能进入 Snapshot 系统
```

所以不是 `remember` 让状态可观察，而是 `mutableStateOf` 本身可观察。

闭环：

```text
Composable 读取 state.value
-> Snapshot / Compose 记录这个读取关系
-> state.value 被修改
-> Snapshot apply 后通知变化
-> 相关组合失效
-> Recomposer 安排重组
```

一句话：

```text
Snapshot 系统让 Compose state 从"普通内存值"变成"可追踪、可通知、可按版本管理的状态"。
```
