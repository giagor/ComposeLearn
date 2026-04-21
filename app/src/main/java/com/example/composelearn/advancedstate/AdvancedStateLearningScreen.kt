package com.example.composelearn.advancedstate

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 性能与进阶状态模块入口。
 * 当前先放 derivedStateOf 的最小样例。
 */
private const val TAG = "AdvancedStateLearningScreen"
@Composable
fun AdvancedStateLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { AdvancedStateHeader() }
        item { DerivedStateValidationLesson() }
        item { DerivedStateThresholdLesson() }
        item { DerivedStateComparisonLesson() }
        item { UnnecessaryRecompositionLesson() }
    }
}

@Composable
private fun AdvancedStateHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE0F2FE),
                            Color(0xFFDCFCE7)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "性能与进阶状态",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先学 derivedStateOf：什么时候需要派生状态，什么时候不需要。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun DerivedStateValidationLesson() {
    var keyword by remember { mutableStateOf("") }
    val canSearch by remember {
        derivedStateOf { keyword.trim().length >= 3 }
    }

    AdvancedStateCard(
        title = "derivedStateOf",
        summary = "目标是理解：当一个状态完全由其他状态推导出来时，可以把它收成派生状态。"
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
                    Text("当前 keyword = \"$keyword\"", fontWeight = FontWeight.Bold)
                    Text("canSearch = $canSearch")
                    Text(if (canSearch) "已经满足搜索条件" else "至少输入 3 个字符")
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
val canSearch by remember {
derivedStateOf { keyword.trim().length >= 3 }
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
                    Text("1. canSearch 不是独立状态，它完全由 keyword 推导出来。")
                    Text("2. 这类“由已有状态计算出来的状态”就适合放进 derivedStateOf。")
                    Text("3. 它的重点不是神奇计算，而是把派生关系写清楚。")
                }
            }
        }
    }
}

@Composable
private fun DerivedStateThresholdLesson() {
    var score by remember { mutableFloatStateOf(0f) }
    val isPassed by remember {
        derivedStateOf { score >= 60f }
    }

    AdvancedStateCard(
        title = "什么时候值得用",
        summary = "目标是理解：只有当你真的在表达“派生状态”时，才值得用 derivedStateOf。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "当前分数 = ${score.toInt()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = score,
                onValueChange = { score = it },
                valueRange = 0f..100f
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isPassed) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("isPassed = $isPassed", fontWeight = FontWeight.Bold)
                    Text(if (isPassed) "达到及格线" else "还没达到及格线")
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
                    Text("1. isPassed 完全由 score 推导出来。")
                    Text("2. 如果只是临时写一行简单判断，不一定非要上 derivedStateOf。")
                    Text("3. 当派生关系需要被反复读取、单独表达时，用它更清楚。")
                }
            }
        }
    }
}

@Composable
private fun DerivedStateComparisonLesson() {
    var score by remember { mutableFloatStateOf(60f) }
    val isPassed by remember {
        derivedStateOf { score >= 60f }
    }

    AdvancedStateCard(
        title = "对比：用与不用",
        summary = "目标是理解：真正的收益不在于少算一次判断，而在于把高频原始状态收敛成低频派生结果。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "当前分数 = ${score.toInt()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = score,
                onValueChange = { score = it },
                valueRange = 60f..100f
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("拖动 60 到 100 之间的滑块。", fontWeight = FontWeight.Bold)
                    Text("这时 score 会频繁变化，但 isPassed 会一直是 true。")
                    Text("看 Logcat 过滤 tag：$TAG")
                }
            }

            DirectThresholdPanel(score = score)
            DerivedThresholdPanel(isPassed = isPassed)

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 不用 derivedStateOf 时，下游直接依赖 score，score 每次变化都会更容易触发下游继续参与重组。")
                    Text("2. 用了 derivedStateOf 后，下游只依赖 isPassed；当结果一直是 true 时，那部分更容易被跳过。")
                    Text("3. 这就是它可能带来的性能收益：减少下游对高频原始状态的无意义响应。")
                }
            }
        }
    }
}

@Composable
private fun UnnecessaryRecompositionLesson() {
    var keyword by remember { mutableStateOf("") }
    val canSubmit by remember {
        derivedStateOf { keyword.trim().length >= 3 }
    }

    AdvancedStateCard(
        title = "为什么会发生不必要重组",
        summary = "目标是理解：很多\"看起来没必要\"的重组，根源不是 Compose 太笨，而是我们把高频变化直接传给了下游。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("输入 3 个字符以上") }
            )

            Button(onClick = { keyword = "abc" }) {
                Text("一键填充 abc")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前 keyword = \"$keyword\"", fontWeight = FontWeight.Bold)
                    Text("canSubmit = $canSubmit")
                    Text("看 Logcat 过滤 tag：$TAG")
                }
            }

            RawKeywordPanel(keyword = keyword)
            SubmitButtonHintPanel(canSubmit = canSubmit)

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. RawKeywordPanel 直接依赖 keyword，所以每次输入都会更容易继续参与重组。")
                    Text("2. SubmitButtonHintPanel 只依赖 canSubmit；当输入从 \"abc\" 继续改成 \"abcd\" 时，这个结果还是 true，下游就更容易被跳过。")
                    Text("3. 很多不必要重组，本质上都是把高频原始状态直接传给了只需要低频结果的子组件。")
                }
            }
        }
    }
}

@Composable
private fun DirectThresholdPanel(score: Float) {
    val isPassed = score >= 60f

    Log.d(TAG, "DirectThresholdPanel1 start")
    SideEffect {
        Log.d(TAG, "DirectThresholdPanel1 recomposed, score=$score, isPassed=$isPassed")
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("不使用 derivedStateOf", fontWeight = FontWeight.Bold)
            Text("score 直接传给子组件")
            Text("isPassed = $isPassed")
        }
    }
}

@Composable
private fun RawKeywordPanel(keyword: String) {
    Log.d(TAG, "Unnecessary RawKeywordPanel start")
    SideEffect {
        Log.d(TAG, "Unnecessary RawKeywordPanel recomposed, keyword=$keyword")
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("直接传原始状态", fontWeight = FontWeight.Bold)
            Text("子组件直接读取 keyword")
            Text("keyword.length = ${keyword.length}")
        }
    }
}

@Composable
private fun SubmitButtonHintPanel(canSubmit: Boolean) {
    Log.d(TAG, "Unnecessary SubmitButtonHintPanel start")
    SideEffect {
        Log.d(TAG, "Unnecessary SubmitButtonHintPanel recomposed, canSubmit=$canSubmit")
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("只传真正需要的结果", fontWeight = FontWeight.Bold)
            Text("子组件只读取 canSubmit")
            Text(if (canSubmit) "按钮可以提交" else "按钮暂时不能提交")
        }
    }
}

@Composable
private fun DerivedThresholdPanel(isPassed: Boolean) {
    Log.d(TAG, "DerivedThresholdPanel2 start")
    SideEffect {
        Log.d(TAG, "DerivedThresholdPanel2 recomposed, isPassed=$isPassed")
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("使用 derivedStateOf", fontWeight = FontWeight.Bold)
            Text("子组件只依赖派生结果")
            Text("isPassed = $isPassed")
        }
    }
}

@Composable
private fun AdvancedStateCard(
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
