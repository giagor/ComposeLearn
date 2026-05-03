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
