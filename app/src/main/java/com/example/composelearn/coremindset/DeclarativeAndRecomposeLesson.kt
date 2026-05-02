package com.example.composelearn.coremindset

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 第一节：声明式 UI 与重组。
 * 用两个最小实验说明“改状态而不是手动改 UI”，以及“谁读取状态，谁更相关”。
 */
private const val RecompositionLogTag = "ComposeCoreMindset"

@Composable
internal fun DeclarativeAndRecomposeLesson() {
    Log.d("abcdefg", "DeclarativeAndRecomposeLesson: step1")
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CoreMindsetLessonCard(
            title = "先记住两个结论",
            summary = "先不管底层细节，先把这一节最重要的两句话记住。"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CoreMindsetBulletLine("Compose 更像 `UI = f(State)`。")
                CoreMindsetBulletLine("状态变了，不是你手动刷新界面，而是 Compose 重新执行相关 Composable。")
                CoreMindsetBulletLine("重组不等于整页重建，谁读取了状态，谁更可能跟着更新。")
            }
        }

        DeclarativeCounterCard()
        RecompositionIsolationCard()
    }
}

@Composable
private fun DeclarativeCounterCard() {
    var count by remember { mutableIntStateOf(0) }

    Log.d("abcdefg", "DeclarativeCounterCard: step1")
    CoreMindsetLessonCard(
        title = "实验 1：声明式 UI",
        summary = "按钮修改状态，文本自动跟着变化。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Log.d("abcdefg", "DeclarativeCounterCard: step2")
            Text(
                text = if (count == 0) "你还没有点击按钮" else "你已经点击了 $count 次",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这里没有任何手动 setText。文本内容完全由 count 状态决定。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Log.d("abcdefg", "DeclarativeCounterCard: step3")
                Button(onClick = { count++ }) {
                    Text("点击 +1")
                }
                Button(onClick = { count = 0 }) {
                    Text("重置")
                }
            }
        }
    }
}

@Composable
private fun RecompositionIsolationCard() {
    var count by remember { mutableIntStateOf(0) }
    var highlightOn by remember { mutableStateOf(false) }

    CoreMindsetLessonCard(
        title = "实验 2：谁读取状态，谁更相关",
        summary = "打开 Logcat 过滤 `ComposeCoreMindset`，然后分别点两个按钮，观察哪些 Composable 被重新执行。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { count++ }) {
                    Text("修改 count")
                }
                Button(onClick = { highlightOn = !highlightOn }) {
                    Text("切换高亮")
                }
            }

            CounterPanel(count = count)
            HighlightPanel(highlightOn = highlightOn)

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察方式", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 点击\"修改 count\"，看哪个面板的日志出现。")
                    Text("2. 点击\"切换高亮\"，再看哪个面板的日志出现。")
                    Text("3. 理解重点不是\"有没有重组\"，而是\"为什么这块 UI 跟当前状态有关\"。")
                }
            }
        }
    }
}

@Composable
private fun CounterPanel(count: Int) {
    Log.d(RecompositionLogTag, "CounterPanel recompose, count=$count")

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(999.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("CounterPanel", fontWeight = FontWeight.Bold)
                Text("它读取的状态是 count = $count")
            }
        }
    }
}

@Composable
private fun HighlightPanel(highlightOn: Boolean) {
    Log.d(RecompositionLogTag, "HighlightPanel recompose, highlightOn=$highlightOn")

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (highlightOn) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(checked = highlightOn, onCheckedChange = null)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("HighlightPanel", fontWeight = FontWeight.Bold)
                Text(if (highlightOn) "当前处于高亮状态" else "当前未高亮")
            }
        }
    }
}
