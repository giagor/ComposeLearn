package com.example.composelearn.statebasics

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 状态管理基础模块入口。
 * 样例：remember + mutableIntStateOf；rememberSaveable
 */
private const val TAG = "StateBasicsLearningScreen"
@Composable
fun StateBasicsLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { StateBasicsHeader() }
        item { RememberIntStateLesson() }
        item { NoRememberIntStateLesson() }
        item { RememberSaveableLesson() }
    }
}

@Composable
private fun StateBasicsHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD1FAE5),
                            Color(0xFFBFDBFE)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "状态管理基础",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先学 remember、mutableIntStateOf、rememberSaveable。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun RememberIntStateLesson() {
    var count by remember { mutableIntStateOf(0) }

    StateLessonCard(
        title = "remember + mutableIntStateOf",
        summary = "目标是先理解：mutableIntStateOf 创建状态，remember 让状态在重组时保留下来。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "当前 count = $count",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { count++ }) {
                    Text("点击 +1")
                }
                Button(onClick = { count = 0 }) {
                    Text("重置")
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
var count by remember { mutableIntStateOf(0) }
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
                    Text("1. 点按钮时，count 变化。")
                    Text("2. 读取 count 的文本会自动更新。")
                    Text("3. 状态写在 remember 里，所以重组后不会直接丢掉。")
                }
            }
        }
    }
}

@Composable
private fun NoRememberIntStateLesson() {
    Log.d(TAG, "NoRememberIntStateLesson: ")
    var trigger by remember { mutableIntStateOf(0) }

    StateLessonCard(
        title = "不加 remember",
        summary = "目标是对比：状态对象如果不放进 remember，每次重组都可能重新初始化。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            NoRememberCounter(trigger = trigger)

            Button(onClick = { trigger++ }) {
                Text("主动触发一次重组")
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
var count by mutableIntStateOf(0)
                        """.trimIndent()
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 先点\"点击 +1\"，count 会变化。")
                    Text("2. 再点\"主动触发一次重组\"，如果状态没有被 remember 保住，它就可能回到初始值。")
                    Text("3. 原因不是 mutableIntStateOf 失效，而是状态对象本身没有被 remember 保住。")
                }
            }
        }
    }
}

@Composable
private fun NoRememberCounter(trigger: Int) {
    Log.d(TAG, "NoRememberCounter: ")
    var count by mutableIntStateOf(0)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "当前 count = $count",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "当前 trigger = $trigger",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
private fun RememberSaveableLesson() {
    Log.d(TAG, "RememberSaveableLesson: ")
    var rememberCount by remember { mutableIntStateOf(0) }
    var saveableCount by rememberSaveable { mutableIntStateOf(0) }
    var rememberText by remember { androidx.compose.runtime.mutableStateOf("remember") }
    var saveableText by rememberSaveable { androidx.compose.runtime.mutableStateOf("rememberSaveable") }

    StateLessonCard(
        title = "rememberSaveable",
        summary = "目标是理解：remember 解决重组，rememberSaveable 进一步解决配置变化后的恢复。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "先改这两组状态，再旋转屏幕观察差异。",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "remember：count = $rememberCount",
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { rememberCount++ }) {
                            Text("remember +1")
                        }
                        Button(onClick = { rememberCount = 0 }) {
                            Text("重置")
                        }
                    }
                    TextField(
                        value = rememberText,
                        onValueChange = { rememberText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("remember 文本") }
                    )

                    HorizontalDivider()

                    Text(
                        text = "rememberSaveable：count = $saveableCount",
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { saveableCount++ }) {
                            Text("saveable +1")
                        }
                        Button(onClick = { saveableCount = 0 }) {
                            Text("重置")
                        }
                    }
                    TextField(
                        value = saveableText,
                        onValueChange = { saveableText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("rememberSaveable 文本") }
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
                    Text("1. 普通重组下，remember 和 rememberSaveable 都能保住状态。")
                    Text("2. 旋转屏幕这类配置变化后，remember 通常会丢，rememberSaveable 更适合恢复。")
                    Text("3. rememberSaveable 更适合输入框内容、当前 tab、简单筛选条件。")
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
                    Text("代码", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
var count by rememberSaveable { mutableIntStateOf(0) }
var text by rememberSaveable { mutableStateOf("") }
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun StateLessonCard(
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
