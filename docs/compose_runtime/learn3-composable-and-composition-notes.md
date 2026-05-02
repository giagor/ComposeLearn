> 内容：@Composable 与组合原理

# 学习路线

1. `@Composable` 的作用
   - 理解 `@Composable` 标记的函数和普通 Kotlin 函数有什么不同。
   - 理解它为什么能参与组合、重组和状态恢复。

2. Compose Compiler 大概做了什么
   - 理解 Composable 函数会被编译器改写。
   - 重点看编译器如何让 Runtime 能参与函数调用过程。

3. Composer 是什么
   - 理解 Composer 是组合过程里的运行时协作者。
   - 重点看它如何记录结构、对齐旧组合、保存和读取运行时数据。

4. Composable 调用为什么能被 Runtime 追踪
   - 理解普通函数调用为什么 Runtime 看不见。
   - 理解 Composable 调用为什么能被 Composer 记录成组合结构。

5. 组合阶段做什么
   - 理解组合阶段负责决定"有哪些 UI"。
   - 重点看 Composable 执行、group 记录、结构对齐、插入、删除、跳过。

6. 和 Slot Table / group / remember / key 的关系
   - 理解 Slot Table 是组合阶段留下的结构和数据记录。
   - 理解 group 记录结构，slot 保存 `remember` 数据，key 帮助组合身份对齐。
