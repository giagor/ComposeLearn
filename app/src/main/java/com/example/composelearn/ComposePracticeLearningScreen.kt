package com.example.composelearn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * 先做小实战：登录页、设置页、列表 + 详情、加载 / 空 / 错误态
 * */
private enum class PracticeCase(val label: String) {
    Login("登录页"),
    Settings("设置页"),
    ListDetail("列表 + 详情"),
    UiState("加载 / 空 / 错误态")
}

@Composable
fun ComposePracticeLearningScreen() {
    var currentCase by remember { mutableStateOf(PracticeCase.Login) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PracticeHeader()
        }

        item {
            PracticeCaseSelector(
                currentCase = currentCase,
                onCaseChange = { currentCase = it }
            )
        }

        item {
            when (currentCase) {
                PracticeCase.Login -> LoginPracticeCard()
                PracticeCase.Settings -> SettingsPracticeCard()
                PracticeCase.ListDetail -> ListDetailPracticeCard()
                PracticeCase.UiState -> UiStatePracticeCard()
            }
        }
    }
}

@Composable
private fun PracticeHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFDE68A),
                            Color(0xFFBFDBFE)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Compose 小实战样例",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一页把真实页面里最常见的 4 类场景拼出来，让你开始从“会用组件”过渡到“会组织页面”。",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "建议学习顺序：先看页面结构，再看状态怎么驱动输入、筛选、选中和界面切换。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PracticeCaseSelector(
    currentCase: PracticeCase,
    onCaseChange: (PracticeCase) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "练习场景",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PracticeCase.entries.forEach { practiceCase ->
                    FilterChip(
                        selected = currentCase == practiceCase,
                        onClick = { onCaseChange(practiceCase) },
                        label = { Text(practiceCase.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginPracticeCard() {
    var email by remember { mutableStateOf("compose@demo.dev") }
    var password by remember { mutableStateOf("123456") }
    val canLogin = email.isNotBlank() && password.length >= 6

    PracticeCard(
        title = "登录页",
        summary = "目标是把输入组件、按钮状态、表单结构组合成一个完整页面。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "欢迎回来",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "先把邮箱和密码输入进状态，再让按钮跟着状态变化。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("邮箱") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {},
                enabled = canLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("登录")
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "观察点：按钮是否可点击，不是你手动 setEnabled，而是由 email 和 password 状态直接决定。",
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsPracticeCard() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoPlayVideo by remember { mutableStateOf(false) }
    var useMobileData by remember { mutableStateOf(false) }
    var theme by remember { mutableStateOf("系统") }
    val themeOptions = listOf("浅色", "深色", "系统")

    PracticeCard(
        title = "设置页",
        summary = "目标是把多个设置项排成一页，同时体会状态和 UI 的一一对应。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingRow(
                title = "推送通知",
                subtitle = "接收课程更新和提醒",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            SettingRow(
                title = "自动播放视频",
                subtitle = "进入详情页时自动开始播放",
                checked = autoPlayVideo,
                onCheckedChange = { autoPlayVideo = it }
            )
            SettingRow(
                title = "允许移动网络加载",
                subtitle = "在无 Wi-Fi 时继续请求图片和视频",
                checked = useMobileData,
                onCheckedChange = { useMobileData = it }
            )

            HorizontalDivider()

            Text(
                text = "主题模式",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                themeOptions.forEach { option ->
                    FilterChip(
                        selected = theme == option,
                        onClick = { theme = option },
                        label = { Text(option) }
                    )
                }
            }
            Text(
                text = "当前主题：$theme",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ListDetailPracticeCard() {
    val lessons = remember {
        listOf(
            DemoLesson("Compose 入门", "理解声明式 UI 和基础布局", "先把 Row / Column / Box 用熟，再看重组。"),
            DemoLesson("Modifier", "掌握尺寸、间距、背景和顺序", "Modifier 是 Compose 最核心的接口之一。"),
            DemoLesson("状态", "让输入和展示共享同一份状态", "先吃透 remember 和 mutableStateOf。"),
            DemoLesson("副作用", "理解什么时候该让 Compose 接触外界", "后面再接入 LaunchedEffect 和 snapshotFlow。")
        )
    }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedLesson = lessons[selectedIndex]

    PracticeCard(
        title = "列表 + 详情",
        summary = "目标是把“左边选中一项，右边或下方显示详情”的页面心智搭起来。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            lessons.forEachIndexed { index, lesson ->
                LessonListItem(
                    lesson = lesson,
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index }
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedLesson.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedLesson.subtitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = selectedLesson.detail)
                }
            }
        }
    }
}

@Composable
private fun LessonListItem(
    lesson: DemoLesson,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lesson.title.first().toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = lesson.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UiStatePracticeCard() {
    var state by remember { mutableStateOf("加载中") }
    val states = listOf("加载中", "空页面", "错误", "成功")

    PracticeCard(
        title = "加载 / 空 / 错误态",
        summary = "目标是把页面状态独立出来，而不是只盯着“有数据时怎么显示”。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                states.forEach { option ->
                    FilterChip(
                        selected = state == option,
                        onClick = { state = option },
                        label = { Text(option) }
                    )
                }
            }

            when (state) {
                "加载中" -> StatePanel(
                    title = "正在加载课程列表",
                    subtitle = "这时候页面不应该空白，而是明确告诉用户系统正在工作。"
                ) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                "空页面" -> StatePanel(
                    title = "还没有收藏内容",
                    subtitle = "空态不是报错，而是当前没有数据。"
                ) {
                    Text("去逛逛课程")
                }

                "错误" -> StatePanel(
                    title = "加载失败",
                    subtitle = "错误态需要告诉用户出了什么问题，并给一个重试动作。"
                ) {
                    Button(onClick = {}) {
                        Text("重试")
                    }
                }

                else -> StatePanel(
                    title = "课程列表已加载",
                    subtitle = "成功态才是正常内容区。"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Compose 布局", "Compose 状态", "Compose 副作用").forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatePanel(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun PracticeCard(
    title: String,
    summary: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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

private data class DemoLesson(
    val title: String,
    val subtitle: String,
    val detail: String
)
