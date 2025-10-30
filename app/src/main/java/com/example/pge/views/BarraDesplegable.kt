package com.example.pge.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pge.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(
    navController: NavController,
    showDrawer: Boolean,
    onClose: () -> Unit
) {
    if (showDrawer) {
        // Fondo semitransparente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClose() } // cerrar al tocar fuera
        )

        // Drawer flotante centrado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
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
                    Text(
                        "Menú Principal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()

                    MenuItem("Dashboard") {
                        navController.navigate(NavRoutes.Dashboard.route)
                        onClose()
                    }
                    MenuItem("Análisis") {
                        navController.navigate(NavRoutes.Analisis.route)
                        onClose()
                    }
                    MenuItem("Dependencias") {
                        navController.navigate(NavRoutes.Dependencias.route)
                        onClose()
                    }
                    MenuItem("Carga de consumos") {
                        navController.navigate(NavRoutes.CargaConsumos.route)
                        onClose()
                    }
                    MenuItem("Presupuestos") {
                        navController.navigate(NavRoutes.Presupuestos.route)
                        onClose()
                    }
                    MenuItem("Usuarios") {
                        navController.navigate(NavRoutes.Usuarios.route)
                        onClose()
                    }

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

