package com.example.composelearn.coremindset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 核心心智模块入口。
 * 负责组织三个小节：声明式 UI 与重组、组合 / 布局 / 绘制、Modifier 执行顺序 / 包裹模型。
 */
private enum class CoreMindsetLesson(val label: String) {
    DeclarativeAndRecompose("声明式 UI 与重组"),
    Phases("组合 / 布局 / 绘制")
}

@Composable
fun ComposeCoreMindsetLearningScreen() {
    var currentLesson by remember { mutableStateOf(CoreMindsetLesson.DeclarativeAndRecompose) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { CoreMindsetHeader() }
        item {
            CoreMindsetSelector(
                currentLesson = currentLesson,
                onLessonChange = { currentLesson = it }
            )
        }
        item {
            when (currentLesson) {
                CoreMindsetLesson.DeclarativeAndRecompose -> DeclarativeAndRecomposeLesson()
                CoreMindsetLesson.Phases -> ComposePhasesLesson()
            }
        }
    }
}

@Composable
private fun CoreMindsetHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFC7D2FE),
                            Color(0xFFFBCFE8)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "核心心智",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先不追求 API 数量，而是先建立 Compose 为什么能更新 UI、为什么只更新相关部分的心智。",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "按顺序学习：声明式 UI 与重组 -> 组合 / 布局 / 绘制。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoreMindsetSelector(
    currentLesson: CoreMindsetLesson,
    onLessonChange: (CoreMindsetLesson) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CoreMindsetLesson.entries.forEach { lesson ->
                FilterChip(
                    selected = currentLesson == lesson,
                    onClick = { onLessonChange(lesson) },
                    label = { Text(lesson.label) }
                )
            }
        }
    }
}

@Composable
internal fun CoreMindsetLessonCard(
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

@Composable
internal fun CoreMindsetBulletLine(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•")
        Text(text)
    }
}
