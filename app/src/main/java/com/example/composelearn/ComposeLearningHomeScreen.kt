package com.example.composelearn

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composelearn.coremindset.ComposeCoreMindsetLearningScreen
import com.example.composelearn.statebasics.ComposeStateBasicsLearningScreen

private enum class LearningSection(val label: String) {
    Basics("基础入门"),
    Practice("先做小实战"),
    CoreMindset("核心心智"),
    StateBasics("状态管理基础")
}

@Composable
fun ComposeLearningHomeScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var currentSection by remember { mutableStateOf(LearningSection.StateBasics) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        LearningTopBar(
            currentSection = currentSection,
            onSectionChange = { currentSection = it }
        )

        when (currentSection) {
            LearningSection.Basics -> ComposeBasicsLearningScreen()
            LearningSection.Practice -> ComposePracticeLearningScreen()
            LearningSection.CoreMindset -> ComposeCoreMindsetLearningScreen()
            LearningSection.StateBasics -> ComposeStateBasicsLearningScreen()
        }
    }
}

@Composable
private fun LearningTopBar(
    currentSection: LearningSection,
    onSectionChange: (LearningSection) -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LearningSection.entries.forEach { section ->
                FilterChip(
                    selected = currentSection == section,
                    onClick = { onSectionChange(section) },
                    label = { Text(section.label) }
                )
            }
        }
    }
}
