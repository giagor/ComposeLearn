# 学习路线

1. Slot Table 解决的问题：记录组合结构，帮助 Runtime 在重组时复用、插入、删除对应内容。
2. group / slot / key：理解结构单位、数据位置、组合身份。
3. 重组时如何对齐：理解新一轮组合执行结果如何和旧 Slot Table 对上。
4. Slot Table 和 remember 的关系：理解 `remember` 的值如何依附在组合位置对应的 slot 上。
5. Compose 源码：重点看 `SlotTable`、`SlotReader`、`SlotWriter`、`Composer`，理解 group / slot / remember 在源码里如何落地。
6. Gap Buffer：理解 Slot Table 如何用 Gap Buffer 思路支持 group / slot 的插入、删除和移动。

# Slot Table 解决的问题

核心结论：

- Slot Table 是 Compose Runtime 的内部结构记录，用来在多次组合之间对齐结构、复用状态、处理结构变化。

为什么需要：

- Compose 的更新不是重新执行完就全部丢掉重建，而是把声明式函数的重新执行变成增量更新。
- Runtime 需要知道"这次执行结果"和"上次组合结果"之间的关系，才能判断哪些内容复用、插入、删除。

它记录什么：

- Slot Table 不是屏幕截图、最终绘制结果或传统 View 树，而是记录组合过程中有哪些 group、哪些 slot、哪些状态位置。

  

工程例子：

```kotlin
@Composable
private fun SlotTableLesson() {
  var showBadge by remember { mutableStateOf(true) }
  var showFooter by remember { mutableStateOf(true) }
  var outerCount by remember { mutableIntStateOf(0) }

  Column(){
    Text("Header")

    if (showBadge) {
        Text("Badge")
    }

    Text("Outer count = $outerCount")

    RememberedCounterChip()

    if (showFooter) {
        Text("Footer")
    }
  }
}

@Composable
private fun RememberedCounterChip() {
    var localCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("remember count = $localCount", fontWeight = FontWeight.Bold)
        Button(onClick = { localCount++ }) {
            Text("remember +1")
        }
    }
}
```

最小理解：

- `showBadge` 从 `true` 变成 `false` 时，`Badge` 这段组合被删除，但 `RememberedCounterChip()` 仍然是原来的组合内容，它里面的 `remember` 状态应该接回原来的位置。



服务的能力：

- 重组定位。
- 跳过不需要重组的 group。
- 条件分支的插入和删除。
- `remember` 状态保存与恢复。
- 后续理解 `key`、列表复用、移动 group 的基础。

# group / slot / key

## group

核心结论：

- group 是 Slot Table 里记录一段组合结构的单位。

最小例子：

```kotlin
Column {
    Text("Header")

    if (showBadge) {
        Text("Badge")
    }

    RememberedCounterChip()
}
```

可以粗略理解成：

```text
Column group
  Text("Header") group
  Badge 对应的条件组合区域
    Text("Badge") group
  RememberedCounterChip group
```

group 解决的问题：

- 这一段组合从哪里开始，到哪里结束。
- 这一段组合里有多少内容。
- 这一段组合下次能否复用、跳过、删除或移动。

## slot

核心结论：

- slot 是组合结构里存放运行时数据的位置，典型例子是 `remember` 的结果。

最小例子：

```kotlin
@Composable
private fun RememberedCounterChip() {
    var localCount by remember { mutableIntStateOf(0) }

    Text("remember count = $localCount")
}
```

可以粗略理解成：

```text
RememberedCounterChip group
  slot: localCount state
  Text group
```

为什么需要：

- Composable 重组时函数会重新执行，普通局部变量会重新创建。
- `remember` 的值需要放在 Runtime 能找回的位置。
- 下次执行到同一个组合位置时，Runtime 从对应 slot 取回旧值。

## key

核心结论：

- key 是组合身份，用来帮助 Runtime 在结构变化时识别"这段组合是谁"。

### key 标识组合身份

结构演示：

```text
group: item.id = 1（A，位置0）
  slot: remember count
  group: item row
  
group: item.id = 2（B，位置1）
  slot: remember count
  group: item row
```

当 A / B 交换位置时：

```text
不用 key：
位置 0 group 的 slot 还留在位置 0

使用 key：
A 换了位置，A 对应的 remember slot 仍然会对回 A
```



例子：

```kotlin
private data class SlotKeyDemoItem(
    val id: Int,
    val label: String
)

var items by remember {
    mutableStateOf(
        listOf(
            SlotKeyDemoItem(id = 1, label = "A"),
            SlotKeyDemoItem(id = 2, label = "B"),
            SlotKeyDemoItem(id = 3, label = "C")
        )
    )
}
```

列表不用 key：

```kotlin
items.forEach { item ->
    RememberedListItem(item = item)
}
```

列表使用 key：

```kotlin
items.forEach { item ->
    key(item.id) {
        RememberedListItem(item = item)
    }
}
```

其中 `RememberedListItem` 内部有自己的 `remember` 状态：

```kotlin
@Composable
private fun RememberedListItem(item: SlotKeyDemoItem) {
    var count by remember { mutableIntStateOf(0) }

    Text("${item.label}: remember count = $count")
}
```

效果差异：

```text
初始：
位置 0: A, id = 1, count = 3
位置 1: B, id = 2, count = 0
位置 2: C, id = 3, count = 0
```

不用 key，交换 A / B 后：

```text
位置 0: B, id = 2, count = 3
位置 1: A, id = 1, count = 0
位置 2: C, id = 3, count = 0
```

使用 `key(item.id)`，交换 A / B 后：

```text
位置 0: B, id = 2, count = 0
位置 1: A, id = 1, count = 3
位置 2: C, id = 3, count = 0
```

原因：

- 不用 key 时，Compose 默认更容易按组合位置对齐，`remember` 状态会留在原位置。
- 使用 `key(item.id)` 后，Runtime 可以按 item 身份对齐，`remember` 状态会跟着 item 移动。
- 列表更容易出 key 问题，因为列表项经常插入、删除、排序、移动，组合位置和业务对象身份容易分离。

补充：

- `key(...)` 不是列表专属，普通 UI 也可以使用。

### key 不是缓存

核心结论：

- `key(...)` 不能缓存已经离开 Composition 的 `remember` 状态。

最小例子：

```kotlin
val userA = SlotKeyDemoItem(id = 1, label = "A")
val userB = SlotKeyDemoItem(id = 2, label = "B")

var selectedUser by remember {
    mutableStateOf(userA)
}

key(selectedUser.id) {
    RememberedListItem(item = selectedUser)
}

@Composable
private fun RememberedListItem(item: SlotKeyDemoItem) {
    var count by remember { mutableIntStateOf(0) }

    Text("${item.label}: remember count = $count")
}
```

操作过程：

```text
A, id = 1, count = 3
切换到 B, id = 2
再切回 A, id = 1
```

结果：

```text
A, id = 1, count = 0
```

原因：

- 从 A 切到 B 时，A 对应的组合离开 Composition，A 内部的 `remember` 状态会被遗忘。
- 再切回 A 时，Runtime 是重新创建 A 对应的组合，不是从缓存里恢复旧状态。
- key 能帮助仍在 Composition 里的组合按身份移动或对齐，但不能保存已经离开的组合状态。

补充：

- 如果需要 A -> B -> A 后保留 A 的状态，要把状态提升到外层或 ViewModel，而不是依赖 `remember`。

## 总结

- group：结构单位，记录一段组合内容的范围。
- slot：数据位置，保存 `remember` 等运行时数据。
- key：组合身份，帮助 Runtime 在结构变化时对齐同一段组合。
- Slot Table 用 group 记录结构，用 slot 保存数据，用 key 辅助识别身份。

# 重组时如何对齐

核心结论：

- 重组时，Runtime 会把新一轮组合执行过程和旧 Slot Table 对齐；对齐成功就复用 group / slot，对齐失败就插入、删除或重新创建。



默认对齐：

- 固定结构通常靠调用位置和组合结构对齐，不需要手动 `key`。

```kotlin
Column {
    Text("Header")
    RememberedCounterChip()
    Text("Footer")
}
```

上一次组合记录：

```text
位置 0: Text("Header") group
位置 1: RememberedCounterChip group
  slot: localCount state
位置 2: Text("Footer") group
```

下一次重组时，代码结构没有变：

```text
位置 0 仍然执行 Text("Header")
位置 1 仍然执行 RememberedCounterChip
位置 2 仍然执行 Text("Footer")
```

Runtime 可以按相同的调用位置和结构对上：

```text
这次位置 0 -> 上次位置 0 的 Header group
这次位置 1 -> 上次位置 1 的 RememberedCounterChip group
这次位置 2 -> 上次位置 2 的 Footer group
```



条件分支：

```kotlin
Column {
    Text("Header")

    if (showBadge) {
        Text("Badge")
    }

    Text("Outer count = $outerCount")
    RememberedCounterChip()
}
```

`showBadge = true` 时，上一次组合记录大概是：

```text
位置 0: Text("Header") group
位置 1: Text("Badge") group
位置 2: Text("Outer count") group
位置 3: RememberedCounterChip group
  slot: localCount state
```

`showBadge = false` 时，这一次执行结果变成：

```text
位置 0: Text("Header") group
位置 1: Text("Outer count") group
位置 2: RememberedCounterChip group
```

Runtime 需要识别：

```text
Text("Badge") 对应的 group 被删除
Text("Outer count") 不是新创建的内容，而是原来的 Outer count 继续存在
RememberedCounterChip 也不是新创建的内容，它的 localCount slot 应该继续接回原来的状态
```

remember 状态：

- `remember` 的值在 slot 里。
- 能对回原来的 group / slot，状态就保留。
- 对不回原来的 group / slot，就会创建新的状态。



key 参与对齐：

- 默认对齐更依赖调用位置。
- 列表顺序变化时，位置和业务身份容易分离。
- `key(item.id)` 可以让 Runtime 按 item 身份对齐。

对齐失败：

- `remember` 状态可能重置。
- 局部状态可能跟错 item。
- 输入框、展开状态、动画状态可能出现异常。

一句话：

- Slot Table 让 Runtime 判断"谁还是原来的谁，谁是新来的，谁已经消失，谁只是换了位置"。

# Slot Table 和 remember 的关系

核心结论：

- `remember` 的值不是存在 Composable 的普通局部变量里，而是存在当前组合位置对应的 slot 里。
- 重组时，只要 Runtime 能把这次执行对回原来的 group / slot，`remember` 状态就能保留。

## remember 不是普通局部变量

普通局部变量：

```kotlin
@Composable
fun Counter() {
    var count = 0

    Text("count = $count")
}
```

重组时，函数重新执行，`count` 会重新变成 `0`。

`remember`：

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }

    Text("count = $count")
}
```

第一次执行时，Runtime 创建状态并存入 slot；下次执行到同一个组合位置时，Runtime 从 slot 取回旧状态。

## remember 依附在 slot 上

```kotlin
Column {
    Text("Header")
    RememberedCounterChip()
}
```

可以粗略理解成：

```text
Column group
  Text("Header") group
  RememberedCounterChip group
    slot: localCount state
```

关键点：

- `remember` 状态不是挂在函数名上。
- `remember` 状态不是挂在变量名上。
- `remember` 状态挂在当前组合位置对应的 slot 上。

## remember 什么时候会丢

`remember` 状态依赖两个条件：

- 这段组合还在 Composition 里。
- 这次执行能对回原来的 group / slot。

最小例子：

```kotlin
if (showCounter) {
    RememberedCounterChip()
}
```

操作过程：

```text
showCounter = true
count = 3

showCounter = false
RememberedCounterChip 离开 Composition

showCounter = true
RememberedCounterChip 重新进入 Composition
```

结果：

```text
count = 0
```

原因：

- 离开 Composition 后，对应 group / slot 被移除。
- 再次出现时，是新的 group / slot。

## remember(key) 和 key(...) 的区别

`remember(key) { ... }` 控制这个 `remember` 值什么时候重新计算：

```kotlin
val userState = remember(user.id) {
    createUserState(user.id)
}
```

当 `user.id` 没变：

```text
继续使用 slot 里的旧 userState
```

当 `user.id` 变了：

```text
旧 remember 值不用了
重新执行 remember block
生成新的 userState
把新值放回当前 slot
```



`key(...) { ... }` 控制一段组合结构用什么身份参与对齐：

```kotlin
key(user.id) {
    UserPanel(user)
}
```

当 `user.id` 用作 `key(...)`：

```text
Runtime 用 user.id 标识这一段 UserPanel 组合
结构变化时，按这个身份参与对齐
```

对比：

```text
remember(user.id)：slot 里的值什么时候重算
key(user.id)：group / 组合结构怎么识别身份
```

## 总结

- `remember` 的值存在 slot 里。
- slot 依附在组合结构上。
- 对回原来的 group / slot，`remember` 状态就保留。
- 离开 Composition 或对不回原来的 slot，`remember` 状态就会重新创建。

# Compose 源码

## SlotTable 的整体结构

核心结论：

- 源码里的 `SlotTable` 不是普通对象树，而是主要由 `groups` 数组和 `slots` 数组组成。

```kotlin
internal class SlotTable : CompositionData, Iterable<CompositionGroup> {
    var groups = IntArray(0)
        private set

    var slots = Array<Any?>(0) { null }
        private set
}
```

作用：

```text
groups：压平记录组合结构
slots：存组合相关数据
```

`groups` 负责什么：

- `groups` 是 `IntArray`，不是一棵对象树，groups 数组不是直接存对象。
- group 信息会被压平成 int 字段，每个 group 用连续的几个 int 字段表示。
- group 里会记录 key、node count、group size、parent、data anchor 等结构信息。

`slots` 负责什么：

- `slots` 是 `Array<Any?>`。
- slots 存 group 关联的数据，例如 `remember` 的值、ObjectKey、Node、Aux 等。



group 描述 slot 的方式：group 会通过 data anchor 关联到 slots 里的数据范围，如下：

```text
groups:
  group 0: Column
  group 1: RememberedCounterChip, dataAnchor = 0

slots:
  slot 0: localCount state
  
备注：
```

+ localCount 是 RememberedCounterChip 里的 `var localCount by remember { mutableIntStateOf(0) }`。

+ `RememberedCounterChip` 这个 group 通过 `dataAnchor = 0` 找到 slots 里的 `localCount state`。

  

为什么 `SlotTable` 不是普通树？如果是普通树，可能长这样：

```text
Group(
  key = Column,
  children = [
    Group(key = Text),
    Group(
      key = RememberedCounterChip,
      slots = [localCount state],
      children = [
        Group(key = Text),
        Group(key = Button)
      ]
    )
  ]
)
```

但源码没有这么做，而是用扁平数组。更顺的理解是：

- Compose Runtime 组合和重组时，经常需要按顺序扫描 group。数组在内存中连续存放，更适合这种线性扫描。
- 从数据结构角度看，相比普通对象树，扁平数组更紧凑，可以减少大量 Group 对象创建和对象引用跳转。
- 子 group 在数组中连续排列，一段组合就可以表示成连续区间，Runtime 更容易计算范围、跳过整段内容。
- 至于在中间插入、删除 group / slot，普通数组本身并不擅长，Slot Table 还需要结合 Gap Buffer 思路来降低修改成本。

## group 在源码里怎么表示

核心结论：

- 源码里一个 group 不是一个 `Group` 对象，而是 `groups: IntArray` 里连续的 5 个 `Int` 字段。

示例代码：

```kotlin
@Composable
private fun GroupSourceExample() {
    Column {
        Text("Header")
        RememberedCounterChip()
    }
}

@Composable
private fun RememberedCounterChip() {
    var localCount by remember { mutableIntStateOf(0) }

    Column {
        Text("remember count = $localCount")
        Button(onClick = { localCount++ }) {
            Text("remember +1")
        }
    }
}
```

可以粗略理解成：

```text
Column group
  Text("Header") group
  RememberedCounterChip group
    slot: localCount state
    Column group
      Text("remember count") group
      Button group
        Text("remember +1") group
```

源码里的 group 布局：

```kotlin
// Group layout
//  0             | 1             | 2             | 3             | 4             |
//  Key           | Group info    | Parent anchor | Size          | Data anchor   |
private const val Key_Offset = 0
private const val GroupInfo_Offset = 1
private const val ParentAnchor_Offset = 2
private const val Size_Offset = 3
private const val DataAnchor_Offset = 4
private const val Group_Fields_Size = 5
```

也就是：

```text
一个 group = 5 个连续 Int
```

放到数组里：

```text
groups:
  [group0 的 5 个字段][group1 的 5 个字段][group2 的 5 个字段]...
```

真实存储更接近：

```text
groups:
  0: group0.key
  1: group0.groupInfo
  2: group0.parentAnchor
  3: group0.size
  4: group0.dataAnchor
  5: group1.key
  6: group1.groupInfo
  7: group1.parentAnchor
  8: group1.size
  9: group1.dataAnchor
```

### Key

- `Key` 是 group 的基础身份信息。
- 它来自 Composer 开始一个 group 时传入的 int key。
- 我们写的 `key(item.id) { ... }` 这种对象 key，通常会作为 ObjectKey 相关数据存到 slots 里，不完全等同于这里的 int key。

### Group info

- `Group info` 是一个压缩字段，把多种信息压在一个 Int 里。可以粗略理解成 `flags + node count`。
- flags 里会记录是否是 node group、是否有 object key、是否有 aux data 等信息。
- node count 用来记录这个 group 对应的节点数量。

### Parent anchor

- `Parent anchor` 记录当前 group 的父 group。

用上面的例子理解：

```text
Column group
  Text("Header") group
  RememberedCounterChip group
```

`Text("Header") group` 和 `RememberedCounterChip group` 的 parent 都指向外层 `Column group`。

这里叫 anchor，而不是简单叫 parent index，是因为 Slot Table 里有 gap，group 的物理位置可能变化；anchor 更适合在插入、删除时维护定位。

### Size

- `Size` 表示这个 group 占了多少个 group，包括自己和子 group。

例子：

```text
0: Column group, size = 4
1: Text("Header") group, size = 1
2: RememberedCounterChip group, size = 2
3: Text("remember count") group, size = 1
```

含义：

- `Column group` 的范围是 `0 until 4`。
- `RememberedCounterChip group` 的范围是 `2 until 4`。
- Runtime 可以通过 `index + size` 知道这段 group 到哪里结束。如果要跳过一个 group：`下一个 group index = 当前 group index + size`，这就是 子 group 连续排列 的价值。这里的 `index` 是 group index，换算到`IntArray` 下标时要乘以 `Group_Fields_Size`

```text
当前 group index = 2
size = 2
下一个 group index = 2 + 2 = 4

下一个 IntArray 起始下标 = 4 * Group_Fields_Size
```

### Data anchor

- `Data anchor` 表示这个 group 关联的数据在 `slots` 数组里的位置。

例子：

```text
groups:
  group 2: RememberedCounterChip, dataAnchor = 0

slots:
  slot 0: localCount state
```

含义：

- `RememberedCounterChip group` 通过 `dataAnchor = 0` 找到 slots 里的 `localCount state`。
- group 负责描述结构，slot 负责保存数据，data anchor 负责把结构和数据连起来。
- 这里叫 anchor，而不是 index，是因为 `slots` 数组里也有 gap；gap 移动时，slot 数据的物理位置可能变化，anchor 更适合表示 group 数据位置。

### 字段访问方式

源码访问 group 字段时，会用：

```text
address * Group_Fields_Size + Offset
```

例子：

```kotlin
private inline fun IntArray.key(address: Int) =
    this[address * Group_Fields_Size]

private fun IntArray.groupSize(address: Int) =
    this[address * Group_Fields_Size + Size_Offset]

private inline fun IntArray.dataAnchor(address: Int) =
    this[address * Group_Fields_Size + DataAnchor_Offset]
```

总结：

- group 是结构单位。
- 源码里 group 被压平成 `groups: IntArray` 里的 5 个字段。
- `Key` 标识 group，`Parent anchor` 维护父子关系，`Size` 表示范围，`Data anchor` 连接 slots 数据。

## slot 在源码里怎么表示

核心结论：

- 源码里的 slot 不是一个单独的类，而是 `slots: Array<Any?>` 里的元素。
- `slots` 存组合相关的运行时数据，最典型的是 `remember` 的结果。

源码入口：

```kotlin
internal class SlotTable : CompositionData, Iterable<CompositionGroup> {
    var slots = Array<Any?>(0) { null }
        private set
}
```

为什么是 `Array<Any?>`：

- slot 里可能放不同类型的数据，所以不是 `Array<State<*>>`。
- slots 里可能存 `remember` 的值，也可能存 ObjectKey、Node、Aux 等 group 关联数据。

### slots 存什么

学习时最常关注：

```text
remember 的结果
```

例如：

```kotlin
var localCount by remember { mutableIntStateOf(0) }
```

可以粗略理解成：

```text
slots:
  slot 0: localCount state
```

但源码层面，slots 不只存 `remember`：

```text
slots:
  ObjectKey?
  Node?
  Aux?
  remember value?
```

### group 怎么找到自己的 slots

group 存在 `groups: IntArray` 里，slot 数据存在 `slots: Array<Any?>` 里。

两者靠 `dataAnchor` 关联：

```text
groups:
  group 2: RememberedCounterChip, dataAnchor = 0

slots:
  slot 0: localCount state
```

含义：

- group 通过自己的 `DataAnchor_Offset` 找到 `dataAnchor`。
- `dataAnchor` 再换算成 slots 里的位置。
- 这样 group 结构和 slot 数据就被连接起来。

源码访问：

```kotlin
private inline fun IntArray.dataAnchor(address: Int) =
    this[address * Group_Fields_Size + DataAnchor_Offset]
```

### dataAnchor 和 slotAnchor

`dataAnchor` 指向 group 数据区的开始。

但 group 数据区里不一定只有普通 slot，还可能先放固定数据：

```text
ObjectKey?
Node?
Aux?
```

所以普通 slot 的开始位置可能不是 `dataAnchor` 本身，而是 `slotAnchor`。

源码：

```kotlin
private fun IntArray.slotAnchor(address: Int) =
    (address * Group_Fields_Size).let { slot ->
        this[slot + DataAnchor_Offset] +
            countOneBits(this[slot + GroupInfo_Offset] shr Slots_Shift)
    }
```

不用死记这段，理解成：

```text
slotAnchor = dataAnchor + 固定 group data 的数量
```

图示：

```text
group data:
  [Node?][ObjectKey?][Aux?][普通 slot...]
   ↑                    ↑
 dataAnchor          slotAnchor
```

结论：

- `dataAnchor`：group 关联数据区的开始。
- `slotAnchor`：普通 slot 的开始。

### ObjectKey / Node / Aux 也在 slots 里

源码里能看到类似访问：

```kotlin
private fun IntArray.node(index: Int) =
    if (isNode(index)) {
        slots[nodeIndex(index)]
    } else Composer.Empty

private fun IntArray.aux(index: Int) =
    if (hasAux(index)) {
        slots[auxIndex(index)]
    } else Composer.Empty

private fun IntArray.objectKey(index: Int) =
    if (hasObjectKey(index)) {
        slots[objectKeyIndex(index)]
    } else null
```

含义：

- Node / Aux / ObjectKey 这些对象数据也存在 `slots` 数组里。
- 有没有这些数据，由 `Group info` 里的 flags 决定。
- 我们写的 `key(item.id) { ... }` 这种对象身份信息，就和 ObjectKey 相关。

### remember 和 slot 的关系

示例：

```kotlin
@Composable
private fun RememberedCounterChip() {
    var localCount by remember { mutableIntStateOf(0) }

    Text("remember count = $localCount")
}
```

第一次组合：

```text
执行到 remember
当前 slot 没有旧值
创建 mutableIntStateOf(0)
写入 slots
```

重组时：

```text
Runtime 对回 RememberedCounterChip group
根据 slotAnchor 找到对应 slot
从 slots 里取回 localCount state
```

如果这段组合离开 Composition：

```text
RememberedCounterChip group 被删除
关联的 slot 数据也被移除
再次出现时创建新的 group / slot
remember 重新初始化
```

### 总结

- `groups` 描述结构。
- `dataAnchor` 指向 group 关联数据区的开始。
- `slotAnchor` 指向普通 slot 的开始。
- `slots` 保存运行时对象数据。
- `remember` 的结果是最典型的 slot 数据。

## SlotReader / SlotWriter

核心结论：

- `SlotTable` 是底层数据结构，`SlotReader` / `SlotWriter` 是读写入口。
- `SlotReader` 负责读取 group / slot，`SlotWriter` 负责修改 group / slot。

源码入口：

```kotlin
inline fun <T> read(block: (reader: SlotReader) -> T): T =
    openReader().let { reader ->
        try {
            block(reader)
        } finally {
            reader.close()
        }
    }

inline fun <T> write(block: (writer: SlotWriter) -> T): T =
    openWriter().let { writer ->
        var normalClose = false
        try {
            block(writer).also { normalClose = true }
        } finally {
            writer.close(normalClose)
        }
    }
```

读写规则：

```text
可以有多个 Reader
同一时间只能有一个 Writer
有 Writer 时不能开 Reader
有 Reader 时不能开 Writer
```

"读旧结构、写新结构" 是重组逻辑上的说法，和底层读写互斥不矛盾；底层 `SlotTable` 不允许 Reader 和 Writer 无约束地同时操作同一份数组，Runtime 会用受控流程完成读取和修改。

### SlotReader 读什么

`SlotReader` 可以读取 group 的结构信息：

```kotlin
fun groupKey(index: Int) = groups.key(index)
fun groupSize(index: Int) = groups.groupSize(index)
fun parent(index: Int) = groups.parentAnchor(index)
fun groupObjectKey(index: Int) = groups.objectKey(index)
```

它也可以读取 slot 数据：

```kotlin
fun next(): Any? {
    if (emptyCount > 0 || currentSlot >= currentSlotEnd) {
        hadNext = false
        return Composer.Empty
    }
    hadNext = true
    return slots[currentSlot++]
}
```

最小理解：

- 读 group：读 key、size、parent、object key、node count 等结构信息。
- 读 slot：从当前 slot 游标读取下一个旧值。

### SlotReader 如何进入 group

源码：

```kotlin
fun startGroup() {
    val currentGroup = currentGroup
    ...
    this.parent = currentGroup
    // currentEnd: The end of the parent group.
    currentEnd = currentGroup + groups.groupSize(currentGroup)
    this.currentGroup = currentGroup + 1
    // currentSlot: The current slot of parent.
    this.currentSlot = groups.slotAnchor(currentGroup)
    // currentSlotEnd: The current end slot of parent.
    this.currentSlotEnd =
        if (currentGroup >= groupsSize - 1) slotsSize
        else groups.dataAnchor(currentGroup + 1)
}
```

例子：

```text
0: Column group, size = 4
1: Text("Header") group, size = 1
2: RememberedCounterChip group, size = 2
3: Text("count") group, size = 1
```

当 Reader 在 `this.currentGroup = 0` 调用 `startGroup()`：

```text
this.currentGroup = 0

val currentGroup = 0
parent = 0
currentEnd = 0 + 4 = 4
this.currentGroup = 1
currentSlot = Column group 的 slotAnchor
currentSlotEnd = Column group 的 slot 结束位置
```

含义：

- `startGroup()` 会进入原来的 `currentGroup`，并把它变成新的 `parent`。
- `currentEnd` 记录这个 parent group 的结束位置。
- `this.currentGroup` 是 group 结构游标，会移到 parent 的第一个子 group。
- `currentSlot` 是 slot 游标，指向 parent group 自己的 slots 范围。
- 所以进入 `Column group` 后，`this.currentGroup` 指向 `Text("Header") group`，但 `currentSlot` 指向的是 `Column group` 的 slotAnchor。

### SlotReader 如何跳过 group

源码：

```kotlin
fun skipGroup(): Int {
    val count = if (groups.isNode(currentGroup)) 1 else groups.nodeCount(currentGroup)
    currentGroup += groups.groupSize(currentGroup)
    return count
}
```

`currentGroup += groupSize`：

- 跳过当前 group 以及它的所有子 group。
- 这里移动的是 group 游标。

`return count`：

- 返回这段 group 对父级贡献了多少个直接 UI node。
- 这里处理的是 UI node 数量，不是 group 数量。

### isNode / nodeCount

group 不一定等于真实 UI node。

结构 group：

- 只负责记录组合结构，不直接对应一个 UI node。
- 例如 `key(user.id) { ... }` 这一层更像结构 group。

node group：

- 这个 group 自己对应一个真实 UI node。
- 对父级来说，一个 node group 贡献 1 个直接 node。

所以：

```text
isNode(group) = 当前 group 自己是不是 node group
nodeCount(group) = 当前结构 group 内部向父级贡献了多少个 node
```

例子：

```text
Column node group
  Text node group
  Text node group
```

跳过 `Column node group` 时：

```text
groupSize 可能很多
skipGroup 返回 1
```

因为从 `Column` 的父级看，`Column` 整体只是 1 个直接 node。

结构 group 例子：

```text
key group
  Text node group
```

跳过 `key group` 时：

```text
key group 自己不是 node
skipGroup 返回它内部贡献的 nodeCount
```

区分：

```text
groupSize：这段组合占多少个 group
nodeCount：这段组合对父级贡献多少个 UI node
```

### SlotReader.next() 什么时候调用

`next()` 的本质：

```text
从当前 slot 游标读取下一个旧值
```

典型场景是 `remember`：

```kotlin
var count by remember { mutableIntStateOf(0) }
```

执行到 `remember` 这一行时，Runtime 需要从当前 slot 里取旧的 state 对象，这时会涉及 `SlotReader.next()` 这类 slot 读取逻辑。

但使用 `count` 的值时：

```kotlin
Text("count = $count")
```

读取的是 `MutableState.value`，不是再从 SlotTable 里读一次 slot。

也就是：

```text
remember 这一行：读取 slot 里保存的 state 对象
Text("$count")：读取 state 对象里的 value
```

补充：

- `remember` 是 `next()` 的典型使用场景之一，不是唯一场景。
- 只要 Runtime 需要按顺序读取当前 group 里的 slot 数据，就可能用到这类读取逻辑。

### SlotWriter 做什么

`SlotWriter` 负责修改 `SlotTable`：

```text
写入 group
写入 slot
插入 group / slot
删除 group / slot
更新 group size
更新 node count
```

源码里 Writer 会维护 gap 相关字段：

```kotlin
private var groupGapStart: Int = table.groupsSize
private var groupGapLen: Int = groups.size / Group_Fields_Size - table.groupsSize

private var slotsGapStart: Int = table.slotsSize
private var slotsGapLen: Int = slots.size - table.slotsSize
```

含义：

- Writer 不只是简单 append。
- Writer 需要管理 `groups` 数组和 `slots` 数组里的 gap。
- Gap Buffer 是后面理解插入、删除 group / slot 的关键。

### 总结

- `SlotReader` 负责读表：读 group 结构，也读 slot 旧值。
- `SlotWriter` 负责改表：写入、插入、删除 group / slot。
- `skipGroup()` 同时处理 group 游标和 UI node 数量。
- 例如 `var count by remember { mutableIntStateOf(0) }`：执行到 `remember` 时，会读取 slot 里保存的 state 对象；后续使用 `count` 时，读的是这个 state 对象的 `value`，不是再次调用 `next()` 读 slot。
