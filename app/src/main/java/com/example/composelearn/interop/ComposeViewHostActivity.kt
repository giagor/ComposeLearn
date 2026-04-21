package com.example.composelearn.interop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import com.example.composelearn.R
import com.example.composelearn.ui.theme.ComposeLearnTheme

private const val COMPOSE_VIEW_TAG = "ComposeViewHostActivity"

class ComposeViewHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_view_host)

        val composeView = findViewById<ComposeView>(R.id.composeViewHost)
        var count by mutableIntStateOf(0)
        var label by mutableStateOf("from ComposeViewHostActivity")

        composeView.setContent {
            Log.d(COMPOSE_VIEW_TAG, "[ComposeViewActivity] setContent content start")
            ComposeLearnTheme {
                ComposeViewHostCard(
                    count = count,
                    label = label,
                    onAddCount = { count++ },
                    onToggleLabel = {
                        label = if (label == "from ComposeViewHostActivity") {
                            "updated from legacy Activity"
                        } else {
                            "from ComposeViewHostActivity"
                        }
                    }
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ComposeViewHostCard(
    count: Int,
    label: String,
    onAddCount: () -> Unit,
    onToggleLabel: () -> Unit
) {
    Log.d(COMPOSE_VIEW_TAG, "[ComposeViewActivity] ComposeViewHostCard recomposed, count=$count, label=$label")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("这块内容来自 ComposeView", style = MaterialTheme.typography.titleMedium)
            Text("count = $count")
            Text("label = $label")
            Button(onClick = onAddCount) {
                Text("count + 1")
            }
            Button(onClick = onToggleLabel) {
                Text("切换文案")
            }
        }
    }
}
