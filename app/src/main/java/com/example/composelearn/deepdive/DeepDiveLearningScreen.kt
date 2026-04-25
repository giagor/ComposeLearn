package com.example.composelearn.deepdive

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun DeepDiveLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DeepDiveHeader() }
        item { CustomLayoutLesson() }
        item { CustomDrawingLesson() }
        item { DrawingComparisonLesson() }
        item { GraphicsLayerLesson() }
        item { RenderingOptimizationLesson() }
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
                text = "自定义能力",
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

@Composable
private fun CustomDrawingLesson() {
    var showGuide by remember { mutableStateOf(true) }

    DeepDiveCard(
        title = "自定义绘制",
        summary = "目标是理解：自定义绘制主要发生在 draw 阶段，常见入口是 drawBehind、drawWithContent、Canvas。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { showGuide = !showGuide }) {
                Text(if (showGuide) "隐藏辅助线" else "显示辅助线")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("重点看：这里没有重新量尺寸，只是在已有区域里画额外内容。", fontWeight = FontWeight.Bold)
                    Text("切换按钮时，变化的是绘制结果，不是布局规则。")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (showGuide) {
                        drawLine(
                            color = Color(0xFF0F766E),
                            start = center.copy(x = 0f),
                            end = center.copy(x = size.width),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFF0F766E),
                            start = center.copy(y = 0f),
                            end = center.copy(y = size.height),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    drawCircle(
                        color = Color(0xFF2563EB),
                        radius = size.minDimension * 0.22f,
                        center = center
                    )

                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension * 0.3f,
                        center = center,
                        style = Stroke(width = 6.dp.toPx())
                    )
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
                    Text("1. `Canvas` 给你一块已经分配好的绘制区域。")
                    Text("2. `size`、`center` 这些值描述的是当前绘制区域，不是重新参与测量。")
                    Text("3. 自定义绘制的重点是“在这块区域里画什么”，不是“重新决定 UI 摆哪”。")
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
Canvas(modifier = Modifier.size(160.dp)) {
    drawCircle(
        color = Color.Blue,
        radius = size.minDimension * 0.25f,
        center = center
    )
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawingComparisonLesson() {
    DeepDiveCard(
        title = "对比：Canvas / drawBehind / drawWithContent",
        summary = "目标是理解：三者都能画，但职责不一样。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("下面三个例子会画出相似的视觉元素，但使用入口不同。", fontWeight = FontWeight.Bold)
                    Text("重点看：是“自己画整块”，还是“在现有内容前后补一层”。")
                }
            }

            DrawCanvasExample()
            DrawBehindExample()
            DrawWithContentExample()

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("先记住", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `Canvas`：整块区域主要靠你自己画。")
                    Text("2. `drawBehind`：在现有内容后面补绘制。")
                    Text("3. `drawWithContent`：你可以决定原内容先画还是后画。")
                }
            }
        }
    }
}

@Composable
private fun DrawCanvasExample() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Canvas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(16.dp))
            ) {
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = size.minDimension * 0.26f,
                    center = center
                )
            }
            Text("这块区域主要靠你自己画出来。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DrawBehindExample() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("drawBehind", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(16.dp))
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF0F766E),
                            radius = size.minDimension * 0.28f,
                            center = center
                        )
                    },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Text", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text("先有内容，再在内容后面补一层绘制。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DrawWithContentExample() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("drawWithContent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(16.dp))
                    .drawWithContent {
                        drawContent()
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.16f),
                            radius = size.minDimension * 0.32f,
                            center = center
                        )
                    },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Text", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
            }
            Text("你可以控制原内容前后顺序，这里是先画内容再盖一层。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GraphicsLayerLesson() {
    var enabled by remember { mutableStateOf(false) }

    DeepDiveCard(
        title = "graphicsLayer",
        summary = "目标是理解：graphicsLayer 更像对整块内容做图层级变换，不是重新布局。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { enabled = !enabled }) {
                Text(if (enabled) "关闭 graphicsLayer 效果" else "开启 graphicsLayer 效果")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("重点看：卡片占位没变，但视觉会旋转、缩放、变透明。", fontWeight = FontWeight.Bold)
                    Text("这就是 graphicsLayer 更偏绘制层处理，而不是重新 measure / place。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("正常卡片", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    GraphicsLayerCard(modifier = Modifier)
                    Text("这里没有额外图层变换。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("graphicsLayer 后的卡片", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    GraphicsLayerCard(
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (enabled) 10f else 0f
                            scaleX = if (enabled) 1.08f else 1f
                            scaleY = if (enabled) 1.08f else 1f
                            alpha = if (enabled) 0.82f else 1f
                            shadowElevation = if (enabled) 20.dp.toPx() else 0f
                            clip = enabled
                            shape = RoundedCornerShape(20.dp)
                            transformOrigin = TransformOrigin.Center
                        }
                    )
                    Text("这里变的是图层效果，不是布局尺寸。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `rotationZ`、`scaleX`、`scaleY`、`alpha` 都是图层级效果。")
                    Text("2. 这块内容看起来变大了，但布局占位不一定跟着重算。")
                    Text("3. `clip` 和 `shape` 常常一起用，表示按图层形状裁剪内容。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
Box(
    modifier = Modifier
        .size(120.dp)
        .graphicsLayer {
            rotationZ = 12f
            scaleX = 1.1f
            scaleY = 1.1f
            alpha = 0.9f
        }
)
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun GraphicsLayerCard(modifier: Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFDBEAFE)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("graphicsLayer sample", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("这张卡片的布局规则没变，变化的是整块内容的绘制效果。")
        }
    }
}

@Composable
private fun RenderingOptimizationLesson() {
    var enabled by remember { mutableStateOf(false) }

    DeepDiveCard(
        title = "渲染优化",
        summary = "目标是建立第一层直觉：先分清这次变化到底是布局问题，还是绘制层问题。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { enabled = !enabled }) {
                Text(if (enabled) "恢复初始状态" else "触发变化")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("下面两个卡片都会“看起来变大”。", fontWeight = FontWeight.Bold)
                    Text("左边通过改布局尺寸，右边通过 graphicsLayer 做视觉缩放。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("改布局尺寸", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(if (enabled) 180.dp else 140.dp)
                            .background(Color(0xFFDBEAFE), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Text("通过 size 变化让占位真的变大")
                    }
                    Text("这类变化更可能让父布局重新考虑怎么摆。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("改绘制层效果", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .graphicsLayer {
                                scaleX = if (enabled) 1.25f else 1f
                                scaleY = if (enabled) 1.25f else 1f
                                alpha = if (enabled) 0.9f else 1f
                            }
                            .background(Color(0xFFCCFBF1), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Text("通过 graphicsLayer 让它看起来变大")
                    }
                    Text("这类变化更偏视觉层，不一定让布局占位跟着变。", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text("先记住", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 想改占位，就改布局尺寸。")
                    Text("2. 想做轻量视觉强调，优先考虑绘制层变化。")
                    Text("3. 渲染优化第一层不是死记规则，而是先分清：这次变化到底是布局问题，还是视觉问题。")
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
