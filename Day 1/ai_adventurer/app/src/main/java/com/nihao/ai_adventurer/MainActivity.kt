package com.nihao.ai_adventurer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nihao.ai_adventurer.ui.ChatScreen
import com.nihao.ai_adventurer.ui.theme.Ai_adventurerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ai_adventurerTheme {
                ChatScreen()
            }
        }
    }
}