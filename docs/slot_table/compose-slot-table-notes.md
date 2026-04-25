# 学习路线

1. Slot Table 解决的问题：记录组合结构，帮助 Runtime 在重组时复用、插入、删除对应内容。
2. group / slot / key：理解结构单位、数据位置、组合身份。
3. 重组时如何对齐：理解新一轮组合执行结果如何和旧 Slot Table 对上。
4. Slot Table 和 remember 的关系：理解 `remember` 的值如何依附在组合位置对应的 slot 上。
5. key 的作用：理解列表、条件分支、顺序变化时如何提供更稳定的身份。
6. Compose 源码：重点看 `SlotTable`、`SlotReader`、`SlotWriter`、`Composer`，用源码验证模型。
7. Gap Buffer：理解 Slot Table 为什么适合频繁插入、删除组合结构。

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
