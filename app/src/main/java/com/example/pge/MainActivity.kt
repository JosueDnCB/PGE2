package com.example.pge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pge.navigation.NavRoutes
import com.example.pge.ui.theme.PGETheme
import com.example.pge.views.AnalisisDashboardScreen
import com.example.pge.views.CargaConsumosScreen
import com.example.pge.views.DashboardScreen
import com.example.pge.views.Dependencia
import com.example.pge.views.DependenciasScreen
import com.example.pge.views.DrawerScreen
import com.example.pge.views.PgeHomeScreen
import com.example.pge.views.PresupuestoScreen
import com.example.pge.views.TransparencyModuleCard
import com.example.pge.views.UsuariosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PGETheme {
                val navController = rememberNavController()
                // Aquí es donde obtendrías tu estado real, por ejemplo:
                // val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                // Cambia esto para probar
                val isLoggedIn = false //

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold( bottomBar = {
                        DrawerScreen(
                          navController,
                          isLoggedIn = isLoggedIn
                        ) }) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = NavRoutes.Principal.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(NavRoutes.Dashboard.route) { DashboardScreen(navController) }
                            composable(NavRoutes.Principal.route) {PgeHomeScreen(navController,isLoggedIn) }
                            composable(NavRoutes.Transparencia.route) { TransparencyModuleCard(navController, isLoggedIn) }
                            composable(NavRoutes.Analisis.route) { AnalisisDashboardScreen(navController) }
                            composable(NavRoutes.Dependencias.route) {
                                DependenciasScreen(
                                    navController,
                                    listOf<Dependencia>(
                                        Dependencia("Secretaría de Finanzas", "Administrativa", 8),
                                        Dependencia("Secretaría de Educación", "Educativa", 12),
                                        Dependencia("Secretaría de Salud", "Salud", 6),
                                        Dependencia("Secretaría de Infraestructura", "Obras", 10)
                                    )
                                )
                            }
                            composable(NavRoutes.CargaConsumos.route) { CargaConsumosScreen(navController) }
                            composable(NavRoutes.Presupuestos.route) {PresupuestoScreen(navController)}
                            composable(NavRoutes.Usuarios.route) {UsuariosScreen() }
                        }
                    }
                }
            }
        }
    }
}

