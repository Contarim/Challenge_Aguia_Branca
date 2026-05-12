package com.inovagab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.inovagab.app.presentation.navigation.InovaGABNavGraph
import com.inovagab.app.ui.theme.InovaGABTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InovaGABTheme {
                InovaGABNavGraph()
            }
        }
    }
}
