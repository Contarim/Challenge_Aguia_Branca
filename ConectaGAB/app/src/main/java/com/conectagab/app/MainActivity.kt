package com.conectagab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.conectagab.app.presentation.navigation.ConectaGABNavGraph
import com.conectagab.app.ui.theme.ConectaGABTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConectaGABTheme {
                ConectaGABNavGraph()
            }
        }
    }
}
