package com.example.composelearn.interop

import android.util.Log
import android.widget.TextView
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "InteropLearningScreen"

@Composable
fun InteropLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { InteropHeader() }
        item { AndroidViewLesson() }
    }
}

@Composable
private fun InteropHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color(0xFFECFCCB)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "互操作与工程化",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先学 AndroidView：在 Compose 里包一个传统 View，并理解 factory / update 各管什么。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AndroidViewLesson() {
    var count by remember { mutableIntStateOf(0) }
    var themeLabel by remember { mutableStateOf("Compose Theme") }

    InteropCard(
        title = "AndroidView",
        summary = "目标是理解：Compose 里嵌一个传统 View 时，factory 负责创建，update 负责把最新状态同步进去。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { count++ }) {
                Text("count + 1")
            }

            Button(
                onClick = {
                    themeLabel = if (themeLabel == "Compose Theme") {
                        "Legacy TextView Theme"
                    } else {
                        "Compose Theme"
                    }
                }
            ) {
                Text("切换文案")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前 count = $count", fontWeight = FontWeight.Bold)
                    Text("当前 themeLabel = $themeLabel")
                    Text("看 Logcat 过滤 tag：$TAG")
                    Text("重点看 [AndroidView] factory / update")
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    Log.d(TAG, "[AndroidView] factory create TextView")
                    TextView(context).apply {
                        textSize = 18f
                        setPadding(40, 30, 40, 30)
                    }
                },
                update = { textView ->
                    Log.d(TAG, "[AndroidView] update count=$count, themeLabel=$themeLabel")
                    textView.text = "Legacy View -> count = $count, label = $themeLabel"
                }
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `factory` 负责创建 View 实例，通常不是每次状态变化都重新创建。")
                    Text("2. `update` 会在状态变化后执行，用来把最新 Compose 状态同步给旧 View。")
                    Text("3. 这就是 AndroidView 的核心：保留原有 View，但让 Compose 继续驱动它的显示。")
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
AndroidView(
    factory = { context -> TextView(context) },
    update = { textView ->
        textView.text = "count = $count"
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
private fun InteropCard(
    title: String,
    summary: String,
    content: @Composable () -> Unit
) {
    Log.d(TAG, "[Interop] InteropCard start, title=$title")
    SideEffect {
        Log.d(TAG, "[Interop] InteropCard recomposed, title=$title")
    }

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
