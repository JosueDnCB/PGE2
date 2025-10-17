package com.example.pge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.pge.ui.theme.PGETheme
import com.example.pge.views.DashboardScreenPreview
import com.example.pge.views.DependenciasTablePreview
import com.example.pge.views.DrawerScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PGETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //DashboardScreenPreview()
                    //DrawerScreen()
                    DependenciasTablePreview()
                }
            }
        }
    }
}

