package com.example.composelearn.sideeffects

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

/**
 * 副作用与异步模块入口。
 * 当前先放 LaunchedEffect 的最小样例。
 */
private const val TAG = "SideEffectsLearningScreen"
@Composable
fun SideEffectsLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SideEffectsHeader() }
        item { LaunchedEffectEnterLesson() }
        item { LaunchedEffectKeyLesson() }
        item { DisposableEffectLesson() }
    }
}

@Composable
private fun SideEffectsHeader() {
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
                            Color(0xFFFFE4E6)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "副作用与异步",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先学 LaunchedEffect 和 DisposableEffect：什么时候启动协程，什么时候注册并清理资源。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun LaunchedEffectEnterLesson() {
    var seconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    SideEffectLessonCard(
        title = "LaunchedEffect(Unit)",
        summary = "目标是先理解：进入组合后启动一个协程，离开组合时这个协程会跟着结束。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "页面停留秒数 = $seconds",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("代码", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
LaunchedEffect(Unit) {
    while (true) {
        delay(1000)
        seconds++
    }
}
                        """.trimIndent()
                    )
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
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 进入这块 UI 后，LaunchedEffect 会启动协程。")
                    Text("2. 协程里可以安全地写 delay 这类挂起逻辑。")
                    Text("3. 这就是为什么它常用来做倒计时、首次加载、页面进入后的异步任务。")
                }
            }
        }
    }
}

@Composable
private fun LaunchedEffectKeyLesson() {
    var keyword by remember { mutableStateOf("Compose") }
    var searchResult by remember { mutableStateOf("等待搜索...") }

    LaunchedEffect(keyword) {
        searchResult = "搜索中..."
        delay(700)
        searchResult = "\"$keyword\" 的结果已返回"
    }

    SideEffectLessonCard(
        title = "LaunchedEffect(key)",
        summary = "目标是理解：key 变化后，旧协程会取消，新协程会按新的 key 重新启动。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("输入关键词") }
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前关键词：$keyword", fontWeight = FontWeight.Bold)
                    Text("当前结果：$searchResult")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("代码", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
LaunchedEffect(keyword) {
    searchResult = "搜索中..."
    delay(700)
    searchResult = "\"$keyword\" 的结果已返回"
}
                        """.trimIndent()
                    )
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
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. keyword 变化时，LaunchedEffect 会按新的 key 重启。")
                    Text("2. 旧协程会取消，所以它很适合做基于输入变化的异步任务。")
                    Text("3. 这也是它常见于搜索、防抖、自动刷新场景的原因。")
                }
            }
        }
    }
}

@Composable
private fun DisposableEffectLesson() {
    var isVisible by remember { mutableStateOf(true) }
    var listenerId by remember { mutableIntStateOf(1) }

    SideEffectLessonCard(
        title = "DisposableEffect(key)",
        summary = "目标是理解：进入组合时注册资源，离开组合或 key 变化时做清理。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { isVisible = !isVisible }) {
                    Text(if (isVisible) "隐藏监听器" else "显示监听器")
                }
                Button(onClick = { listenerId++ }) {
                    Text("切换 key")
                }
            }

            Text("当前 listenerId = $listenerId")
            Text("当前状态 = ${if (isVisible) "监听中" else "已移除"}")

            if (isVisible) {
                ListenerPanel(listenerId = listenerId)
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("代码", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
DisposableEffect(listenerId) {
    registerListener(listenerId)

    onDispose {
        unregisterListener(listenerId)
    }
}
                        """.trimIndent()
                    )
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
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 进入组合时会执行注册逻辑。")
                    Text("2. listenerId 变化时，会先清理旧资源，再按新 key 重新注册。")
                    Text("3. 这块 UI 离开组合时，会执行 onDispose 做清理。")
                }
            }
        }
    }
}

@Composable
private fun ListenerPanel(listenerId: Int) {
    var status by remember(listenerId) { mutableStateOf("等待注册...") }

    Log.d(TAG, "ListenerPanel: start")
    DisposableEffect(listenerId) {
        Log.d(TAG, "ListenerPanel: register")
        status = "已注册 listener-$listenerId"

        onDispose {
            Log.d(TAG, "ListenerPanel: onDispose")
            status = "已清理 listener-$listenerId"
        }
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("ListenerPanel", fontWeight = FontWeight.Bold)
            Text(status)
        }
    }
}

@Composable
private fun SideEffectLessonCard(
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
