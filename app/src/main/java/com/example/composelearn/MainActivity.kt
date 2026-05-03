package com.example.composelearn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composelearn.ui.theme.ComposeLearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLearnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LearningHomeScreen(contentPadding = innerPadding)
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            window.decorView.dump()
        }, 3000)
    }
}

fun View.dump(prefix: String = "") {
    Log.d("ViewTree", "$prefix${this.javaClass.name} id=$id")
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).dump("$prefix  ")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LearningHomeScreenPreview() {
    ComposeLearnTheme {
        LearningHomeScreen()
    }
}
