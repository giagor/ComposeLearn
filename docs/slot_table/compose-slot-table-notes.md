# 学习路线

1. Slot Table 解决什么问题：记录组合结构，帮助 Runtime 在重组时复用、插入、删除对应内容。
2. group / slot / key：它们分别对应结构单位、数据位置、组合身份。
3. 重组时如何对齐：Compose 会把新一轮组合执行结果和旧 Slot Table 对上。
4. Slot Table 和 `remember` 的关系：`remember` 的值依附在组合位置对应的 slot 上。
5. `key(...)` 的作用：在列表、条件分支、顺序变化时提供更稳定的身份。
6. Compose 源码：重点看 `SlotTable`、`SlotReader`、`SlotWriter`、`Composer`，用源码验证模型。
7. Gap Buffer：理解 Slot Table 为什么适合频繁插入、删除组合结构。
