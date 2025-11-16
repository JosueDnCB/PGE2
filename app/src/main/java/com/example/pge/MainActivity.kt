package com.example.pge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import kotlin.collections.listOf
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El sistema dibujará detrás de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PGETheme {
                val navController = rememberNavController()
                // Aquí es donde obtendrías tu estado real, por ejemplo:
                // val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                // Estado para saber si el usuario inició sesión
                var isLoggedIn by remember { mutableStateOf(false) }


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
                            composable(NavRoutes.Dashboard.route) {
                                DashboardScreen(
                                    navController = navController,
                                    isLoggedIn = isLoggedIn,
                                    onLoginSuccess = {
                                        // Esta lambda se ejecutará cuando el login sea exitoso
                                        isLoggedIn = true
                                        navController.navigate(NavRoutes.Dashboard.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                    )
                            }
                            composable(NavRoutes.Principal.route) {
                                PgeHomeScreen(
                                    navController = navController,
                                    isLoggedIn = isLoggedIn,
                                    onLoginSuccess = {
                                        // Esta lambda se ejecutará cuando el login sea exitoso
                                        isLoggedIn = true
                                        navController.navigate(NavRoutes.Dashboard.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                            ) }
                            composable(NavRoutes.Transparencia.route) {
                                TransparencyModuleCard(
                                    navController = navController,
                                    isLoggedIn = isLoggedIn,
                                    onLoginSuccess = {
                                        // Esta lambda se ejecutará cuando el login sea exitoso
                                        isLoggedIn = true
                                        navController.navigate(NavRoutes.Dashboard.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                ) }
                            composable(NavRoutes.Analisis.route) { AnalisisDashboardScreen(navController, isLoggedIn) }
                            composable(NavRoutes.Dependencias.route) {
                                DependenciasScreen(
                                    navController,
                                    isLoggedIn = isLoggedIn,
                                    listOf<Dependencia>(
                                        Dependencia("Secretaría de Finanzas", "Administrativa", 8),
                                        Dependencia("Secretaría de Educación", "Educativa", 12),
                                        Dependencia("Secretaría de Salud", "Salud", 6),
                                        Dependencia("Secretaría de Infraestructura", "Obras", 10)
                                    ),
                                    onLoginSuccess = {
                                        // Esta lambda se ejecutará cuando el login sea exitoso
                                        isLoggedIn = true
                                        navController.navigate(NavRoutes.Dashboard.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            composable(NavRoutes.CargaConsumos.route) { CargaConsumosScreen(navController,
                                isLoggedIn = isLoggedIn,
                                onLoginSuccess = {
                                    // Esta lambda se ejecutará cuando el login sea exitoso
                                    isLoggedIn = true
                                    navController.navigate(NavRoutes.Dashboard.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                )
                            }
                            composable(NavRoutes.Presupuestos.route) {PresupuestoScreen(
                                navController,
                                isLoggedIn = isLoggedIn,
                                onLoginSuccess = {
                                    // Esta lambda se ejecutará cuando el login sea exitoso
                                    isLoggedIn = true
                                    navController.navigate(NavRoutes.Dashboard.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                })}
                            composable(NavRoutes.Usuarios.route) {UsuariosScreen() }
                        }
                    }
                }
            }
        }
    }
}

