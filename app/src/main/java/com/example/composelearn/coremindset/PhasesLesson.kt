package com.example.composelearn.coremindset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 第二节：组合 / 布局 / 绘制。
 * 用三个最小实验区分“有没有 UI”、“多大放哪”和“长什么样”。
 */
@Composable
internal fun PhasesLesson() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CoreMindsetLessonCard(
            title = "先记住三个阶段",
            summary = "这一节先建立最基础的三层心智，不急着钻实现细节。"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CoreMindsetBulletLine("组合：决定有哪些 UI。")
                CoreMindsetBulletLine("布局：决定多大、放哪。")
                CoreMindsetBulletLine("绘制：决定长什么样。")
            }
        }

        CompositionPhaseCard()
        LayoutPhaseCard()
        DrawPhaseCard()
    }
}

@Composable
private fun CompositionPhaseCard() {
    var showBadge by remember { mutableStateOf(true) }

    CoreMindsetLessonCard(
        title = "实验 1：组合",
        summary = "组合关注的是\"这块 UI 存不存在\"。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("显示推荐徽标", fontWeight = FontWeight.Bold)
                    Text("切换后，Badge 这块 UI 会出现或消失。")
                }
                Switch(checked = showBadge, onCheckedChange = { showBadge = it })
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("这是固定文本")
                        Text("这是固定文本")
                        Text("这是固定文本")
                    }

                    if (showBadge) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "推荐",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutPhaseCard() {
    var expanded by remember { mutableStateOf(false) }

    CoreMindsetLessonCard(
        title = "实验 2：布局",
        summary = "布局关注的是\"多大、放哪\"。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("展开卡片", fontWeight = FontWeight.Bold)
                    Text("切换后，高度和 padding 会变化。")
                }
                Switch(checked = expanded, onCheckedChange = { expanded = it })
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (expanded) 168.dp else 104.dp)
                        .padding(if (expanded) 20.dp else 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("布局阶段示例", fontWeight = FontWeight.Bold)
                    Text("当前高度：${if (expanded) "168dp" else "104dp"}")
                    Text("当前 padding：${if (expanded) "20dp" else "12dp"}")
                }
            }
        }
    }
}

@Composable
private fun DrawPhaseCard() {
    var highlight by remember { mutableStateOf(false) }

    CoreMindsetLessonCard(
        title = "实验 3：绘制",
        summary = "绘制关注的是\"长什么样\"。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("高亮背景", fontWeight = FontWeight.Bold)
                    Text("切换后，主要变化是颜色和背景。")
                }
                Switch(checked = highlight, onCheckedChange = { highlight = it })
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (highlight) Color(0xFFFDE68A) else MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = if (highlight) Color(0xFFF59E0B) else Color(0xFF2563EB),
                                shape = RoundedCornerShape(999.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("绘制阶段示例", fontWeight = FontWeight.Bold)
                        Text("这里主要变化的是颜色和背景。")
                    }
                }
            }
        }
    }
}
