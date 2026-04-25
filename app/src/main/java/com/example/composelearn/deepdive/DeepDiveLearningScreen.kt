package com.example.composelearn.deepdive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun DeepDiveLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DeepDiveHeader() }
        item { CustomLayoutLesson() }
    }
}

@Composable
private fun DeepDiveHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEFF6FF),
                            Color(0xFFECFDF5)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "底层原理与高阶能力",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先从自定义 Layout 开始：手写 measure / place，比先啃 Runtime 数据结构更容易建立直觉。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CustomLayoutLesson() {
    val cards = remember {
        mutableStateListOf(
            LayoutCardData("Measure", "先量每个子元素多大。", 72),
            LayoutCardData("Place", "再决定每个子元素放哪。", 120),
            LayoutCardData("Constraints", "父布局会先把约束传下来。", 96),
            LayoutCardData("Custom", "Layout 的核心是你自己写摆放规则。", 144)
        )
    }
    var nextIndex by remember { mutableIntStateOf(cards.size + 1) }

    DeepDiveCard(
        title = "自定义 Layout",
        summary = "目标是理解：自定义 Layout 最核心的两步就是 measure 子元素，再 place 到你想要的位置。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(
                onClick = {
                    val newHeight = if (nextIndex % 2 == 0) 88 else 132
                    cards.add(
                        LayoutCardData(
                            title = "Item $nextIndex",
                            description = "新增一个不同高度的卡片，观察自定义布局怎么重新排布。",
                            minHeight = newHeight
                        )
                    )
                    nextIndex++
                }
            ) {
                Text("新增一个卡片")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("下面这块不是 `LazyVerticalGrid`，而是手写 `Layout { ... }`。", fontWeight = FontWeight.Bold)
                    Text("规则很简单：把子元素按左右两列交替放置。")
                    Text("这样你能直接看到 measure / place 是怎么把 UI 摆出来的。")
                }
            }

            AlternatingTwoColumnLayout(
                modifier = Modifier.fillMaxWidth(),
                horizontalGap = 12.dp,
                verticalGap = 12.dp
            ) {
                cards.forEach { card ->
                    LayoutCardItem(card)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 父布局先决定每列能给子元素多大宽度。")
                    Text("2. 每个子元素先被 measure，拿到自己的 placeable。")
                    Text("3. 最后再根据左右列规则，把 placeable 放到具体坐标。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
Layout(content = content) { measurables, constraints ->
    val placeables = measurables.map { measurable ->
        measurable.measure(constraints)
    }

    layout(width, height) {
        placeables.forEach { placeable ->
            placeable.placeRelative(x, y)
        }
    }
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

private data class LayoutCardData(
    val title: String,
    val description: String,
    val minHeight: Int
)

@Composable
private fun LayoutCardItem(card: LayoutCardData) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = card.minHeight.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(card.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(card.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AlternatingTwoColumnLayout(
    modifier: Modifier = Modifier,
    horizontalGap: androidx.compose.ui.unit.Dp,
    verticalGap: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val horizontalGapPx = horizontalGap.roundToPx()
        val verticalGapPx = verticalGap.roundToPx()
        val columnWidth = ((constraints.maxWidth - horizontalGapPx) / 2).coerceAtLeast(0)
        val childConstraints = Constraints(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )

        val placeables = measurables.map { measurable ->
            measurable.measure(childConstraints)
        }

        val positions = mutableListOf<Pair<Int, Int>>()
        var leftColumnY = 0
        var rightColumnY = 0

        placeables.forEachIndexed { index, placeable ->
            if (index % 2 == 0) {
                positions += 0 to leftColumnY
                leftColumnY += placeable.height + verticalGapPx
            } else {
                positions += (columnWidth + horizontalGapPx) to rightColumnY
                rightColumnY += placeable.height + verticalGapPx
            }
        }

        val layoutHeight = maxOf(leftColumnY, rightColumnY).let { total ->
            if (placeables.isEmpty()) 0 else total - verticalGapPx
        }.coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width = constraints.maxWidth, height = layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = positions[index]
                placeable.placeRelative(x = x, y = y)
            }
        }
    }
}

@Composable
private fun DeepDiveCard(
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
