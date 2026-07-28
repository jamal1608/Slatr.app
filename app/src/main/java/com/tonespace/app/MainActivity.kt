package com.tonespace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tonespace.app.ui.navigation.ToneShareNavGraph
import com.tonespace.app.ui.theme.ToneSpaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToneSpaceTheme {
                ToneShareNavGraph()
            }
        }
    }
}