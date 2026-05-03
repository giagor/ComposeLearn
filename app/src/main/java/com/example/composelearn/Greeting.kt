package com.example.composelearn

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Greeting1(name: String) {
    Text("Hello $name")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting2() {
    var count by remember { mutableIntStateOf(0) }
    Column {
        Text(
            text = if (count == 0) "你还没有点击按钮" else "你已经点击了 $count 次",
        )
        Button(onClick = { count++ }) {
            Text("点击 +1")
        }
    }
}
