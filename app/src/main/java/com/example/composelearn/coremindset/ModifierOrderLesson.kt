package com.example.composelearn.coremindset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 第三节：Modifier 执行顺序 / 包裹模型。
 * 用最小对比实验说明：Modifier 是链式包裹的，顺序变化会改变结果。
 */
@Composable
internal fun ModifierOrderLesson() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CoreMindsetLessonCard(
            title = "先记住两句话",
            summary = "这一节只抓最关键的心智，不急着覆盖很多 Modifier。"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CoreMindsetBulletLine("Modifier 是链式执行的。")
                CoreMindsetBulletLine("顺序不同，包裹关系不同，结果也可能不同。")
            }
        }

        BackgroundPaddingCard()
        ClipBackgroundCard()
    }
}

@Composable
private fun BackgroundPaddingCard() {
    CoreMindsetLessonCard(
        title = "背景和内边距",
        summary = "这一组最适合建立第一个直觉：同样是 background 和 padding，顺序一变，背景覆盖范围就可能不同。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ModifierComparePanel(
                    title = "background -> padding",
                    modifierText = """
Modifier
    .background(Color(0xFFBFDBFE))
    .padding(16.dp)
                    """.trimIndent()
                ) {
                    Text(
                        text = "A",
                        modifier = Modifier
                            .background(Color(0xFFBFDBFE))
                            .padding(16.dp)
                    )
                }

                ModifierComparePanel(
                    title = "padding -> background",
                    modifierText = """
Modifier
    .padding(16.dp)
    .background(Color(0xFFBFDBFE))
                    """.trimIndent()
                ) {
                    Text(
                        text = "B",
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFFBFDBFE))
                    )
                }
            }

            Text("观察点：两边都用了同样两个 Modifier，但背景覆盖范围不一样。")
        }
    }
}

@Composable
private fun ClipBackgroundCard() {
    CoreMindsetLessonCard(
        title = "裁剪和背景",
        summary = "这一组继续说明：顺序不同，不只是大小不同，视觉效果也会不同。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ModifierComparePanel(
                    title = "clip -> background",
                    modifierText = """
Modifier
    .clip(RoundedCornerShape(16.dp))
    .background(Color(0xFFFDE68A))
                    """.trimIndent()
                ) {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = "Clip",
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFDE68A))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }

                ModifierComparePanel(
                    title = "background -> border",
                    modifierText = """
Modifier
    .background(Color(0xFFFDE68A))
    .border(2.dp, Color(0xFFF59E0B))
                    """.trimIndent()
                ) {
                    Text(
                        text = "Border",
                        modifier = Modifier
                            .background(Color(0xFFFDE68A))
                            .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(0.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            Text("观察点：不要死记结果，先看顺序，再想是谁包住谁。")
        }
    }
}

@Composable
private fun ModifierComparePanel(
    title: String,
    modifierText: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.width(170.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = modifierText,
                    style = MaterialTheme.typography.bodySmall
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
