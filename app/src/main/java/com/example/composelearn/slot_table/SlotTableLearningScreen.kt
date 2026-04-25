package com.example.composelearn.slot_table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SlotTableLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SlotTableHeader() }
        item { SlotTableLesson() }
        item { ListKeyComparisonLesson() }
        item { KeyDoesNotCacheForgottenStateLesson() }
    }
}

@Composable
private fun SlotTableHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF0FDF4),
                            Color(0xFFEFF6FF)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Slot Table",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "聚焦 Compose Runtime 如何记录组合结构、恢复 remember 状态，以及用 key 稳定组合身份。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SlotTableLesson() {
    var showBadge by remember { mutableStateOf(true) }
    var showFooter by remember { mutableStateOf(true) }
    var outerCount by remember { mutableIntStateOf(0) }

    SlotTableCard(
        title = "Slot Table 解决的问题",
        summary = "目标是建立直觉：Compose Runtime 需要一份内部结构记录，来知道当前组合里有哪些节点、哪些分支还在、remember 状态该接回哪里。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { showBadge = !showBadge }) {
                Text(if (showBadge) "隐藏 Badge" else "显示 Badge")
            }

            Button(onClick = { showFooter = !showFooter }) {
                Text(if (showFooter) "隐藏 Footer" else "显示 Footer")
            }

            Button(onClick = { outerCount++ }) {
                Text("outerCount + 1")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("条件分支结构变化", fontWeight = FontWeight.Bold)
                    Text("下面这块 UI 会根据条件显示或隐藏一部分节点，重点是观察哪些组合内容被插入、删除、复用。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Header", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    if (showBadge) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Badge",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text("Outer count = $outerCount")

                    RememberedCounterChip()

                    if (showFooter) {
                        Text("Footer", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

        }
    }
}

@Composable
private fun ListKeyComparisonLesson() {
    var noKeyItems by remember {
        mutableStateOf(
            listOf(
                SlotKeyDemoItem(id = 1, label = "A"),
                SlotKeyDemoItem(id = 2, label = "B"),
                SlotKeyDemoItem(id = 3, label = "C")
            )
        )
    }
    var keyedItems by remember {
        mutableStateOf(
            listOf(
                SlotKeyDemoItem(id = 1, label = "A"),
                SlotKeyDemoItem(id = 2, label = "B"),
                SlotKeyDemoItem(id = 3, label = "C")
            )
        )
    }

    SlotTableCard(
        title = "列表 key 对比",
        summary = "目标是观察列表顺序变化时，remember 状态是跟着位置走，还是跟着 item 身份走。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NoKeyDemoList(
                title = "不用 key",
                description = "状态按位置保存，交换后 count 容易跟错 item。",
                items = noKeyItems,
                onSwap = { noKeyItems = noKeyItems.withFirstTwoSwapped() }
            )
            KeyedDemoList(
                title = "使用 key(item.id)",
                description = "状态按 item.id 对齐，交换后 count 应该跟着 A / B 走。",
                items = keyedItems,
                onSwap = { keyedItems = keyedItems.withFirstTwoSwapped() }
            )
        }
    }
}

private fun List<SlotKeyDemoItem>.withFirstTwoSwapped(): List<SlotKeyDemoItem> {
    return listOf(this[1], this[0]) + drop(2)
}

@Composable
private fun KeyDoesNotCacheForgottenStateLesson() {
    val userA = SlotKeyDemoItem(id = 1, label = "User A")
    val userB = SlotKeyDemoItem(id = 2, label = "User B")
    var selectedUser by remember { mutableStateOf(userA) }

    SlotTableCard(
        title = "key 不会缓存已离开的状态",
        summary = "目标是观察 A -> B -> A 时，A 离开过 Composition，所以 A 内部的 remember 状态会重新创建。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    selectedUser = if (selectedUser.id == userA.id) userB else userA
                }
            ) {
                Text("切换 User A / User B")
            }

            key(selectedUser.id) {
                RememberedListItem(item = selectedUser)
            }

            Text(
                text = "操作：把 User A 点到非 0，切到 User B，再切回 User A，A 的 count 会回到 0。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class SlotKeyDemoItem(
    val id: Int,
    val label: String
)

@Composable
private fun NoKeyDemoList(
    title: String,
    description: String,
    items: List<SlotKeyDemoItem>,
    onSwap: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onSwap) {
            Text("交换 A / B")
        }
        items.forEach { item ->
            RememberedListItem(item = item)
        }
    }
}

@Composable
private fun KeyedDemoList(
    title: String,
    description: String,
    items: List<SlotKeyDemoItem>,
    onSwap: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onSwap) {
            Text("交换 A / B")
        }
        items.forEach { item ->
            key(item.id) {
                RememberedListItem(item = item)
            }
        }
    }
}

@Composable
private fun RememberedListItem(item: SlotKeyDemoItem) {
    var count by remember { mutableIntStateOf(0) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("${item.label}: remember count = $count", fontWeight = FontWeight.Bold)
            Button(onClick = { count++ }) {
                Text("${item.label} +1")
            }
        }
    }
}

@Composable
private fun RememberedCounterChip() {
    var localCount by remember { mutableIntStateOf(0) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
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
}

@Composable
private fun SlotTableCard(
    title: String,
    summary: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider()
            content()
        }
    }
}
