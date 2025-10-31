package com.example.pge


import AnalisisDashboardScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pge.navigation.NavRoutes
import com.example.pge.ui.theme.PGETheme
import com.example.pge.views.CargaConsumosScreen
import com.example.pge.views.DashboardScreen
import com.example.pge.views.Dependencia
import com.example.pge.views.DependenciasScreen
import com.example.pge.views.DrawerScreen
import com.example.pge.views.PresupuestosScreen
import com.example.pge.views.UsuariosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PGETheme {
                val navController = rememberNavController()
                var showDrawer by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = NavRoutes.Dashboard.route
                        ) {
                            composable(NavRoutes.Dashboard.route) {
                                DashboardScreen(navController) { showDrawer = true }
                            }
                            composable(NavRoutes.Analisis.route) { AnalisisDashboardScreen(navController) { showDrawer = true } }
                            composable(NavRoutes.Dependencias.route) {
                                DependenciasScreen(
                                    navController,
                                    listOf<Dependencia>(
                                        Dependencia("Secretaría de Finanzas", "Administrativa", 8, ""),
                                        Dependencia("Secretaría de Educación", "Educativa", 12, ""),
                                        Dependencia("Secretaría de Salud", "Salud", 6, ""),
                                        Dependencia("Secretaría de Infraestructura", "Obras", 10, ""
                                        )
                                    ), { showDrawer = true }
                                )
                            }
                            composable(NavRoutes.CargaConsumos.route) { CargaConsumosScreen() }
                            composable(NavRoutes.Presupuestos.route) { PresupuestosScreen() }
                            composable(NavRoutes.Usuarios.route) { UsuariosScreen() }
                        }

                        DrawerScreen(
                            navController = navController,
                            showDrawer = showDrawer,
                            onClose = { showDrawer = false }
                        )
                    }
                }
            }
        }
    }
}

