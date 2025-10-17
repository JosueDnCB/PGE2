package com.example.pge.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pge.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.6f),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Menú Principal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Divider()

            MenuItem("Dashboard") { onClose() }
            MenuItem("Análisis") { onClose() }
            MenuItem("Dependencias") { onClose() }
            MenuItem("Carga de consumos") { onClose() }
            MenuItem("Presupuestos") { onClose() }
            MenuItem("Usuarios") { onClose() }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

