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
Text("Header")

if (showBadge) {
    Text("Badge")
}

Text("Outer count = $outerCount")

RememberedCounterChip()

if (showFooter) {
    Text("Footer")
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
