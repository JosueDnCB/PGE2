package com.example.pge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
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
import com.example.pge.views.DrawerScreen
import com.example.pge.views.PgeHomeScreen
import com.example.pge.views.PresupuestoScreen
import com.example.pge.views.TransparencyModuleCard
import androidx.core.view.WindowCompat
import com.example.pge.viewmodels.LoginViewModel
import com.example.pge.views.DependenciasScreenConnected
import com.example.pge.views.LoginDialog
import com.example.pge.views.PerfilUsuarioScreen
import com.example.pge.data.preferences.TokenManager
import com.example.pge.views.Navigation.swipeToNavigate
import com.example.pge.views.PublicDashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        tokenManager.clearToken()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PGETheme {

                val navController = rememberNavController()

                // ⬅️ Agregamos el LoginViewModel
                val loginViewModel = remember { LoginViewModel(this) }

                // Estado del login
                val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
                val usuarioLogin by loginViewModel.usuario.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            DrawerScreen(navController = navController, isLoggedIn = isLoggedIn)


                        }
                    ) { innerPadding ->

                        // Agregamos el modificador al contenedor principal
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .swipeToNavigate(navController, isLoggedIn) // <--- AGREGAR ESTO
                                .padding(innerPadding)

                        ) {

                            NavHost(
                                navController = navController,
                                startDestination = NavRoutes.Principal.route
                            ) {

                                composable(NavRoutes.Dashboard.route) {
                                    DashboardScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel,   // 👈 SE AGREGA
                                        isLoggedIn = isLoggedIn,
                                        usuario = usuarioLogin,      // ✔ se lo enviamos
                                        onLoginSuccess = {
                                            loginViewModel.getUser()
                                            navController.navigate(NavRoutes.Dashboard.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.PublicDashboard.route) {
                                    PublicDashboardScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel,   // 👈 SE AGREGA
                                        isLoggedIn = isLoggedIn,
                                        usuario = usuarioLogin,      // ✔ se lo enviamos
                                        onLoginSuccess = {
                                            loginViewModel.getUser()
                                            navController.navigate(NavRoutes.Dashboard.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.Principal.route) {
                                    PgeHomeScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel,   // 👈 SE AGREGA
                                        isLoggedIn = isLoggedIn,
                                        usuarios = usuarioLogin, // 👈 usuario real o null si no hay
                                        onLoginSuccess = {
                                            // Actualiza el usuario en tu ViewModel
                                            loginViewModel.getUser()
                                            // Navega al dashboard
                                            navController.navigate(NavRoutes.Dashboard.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }




                                composable(NavRoutes.Transparencia.route) {
                                    TransparencyModuleCard(
                                        navController = navController,
                                        isLoggedIn = isLoggedIn,
                                        usuarios = usuarioLogin,
                                        onLoginSuccess = {
                                            // Actualiza el usuario en tu ViewModel
                                            loginViewModel.getUser()
                                            // Navega al dashboard
                                            navController.navigate(NavRoutes.Dashboard.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.LoginView.route) {

                                    LoginDialog(
                                        loginViewModel = loginViewModel,

                                        // 2. Qué hacer si cierran el diálogo (tachecito)
                                        onDismissRequest = {
                                            navController.popBackStack() // Regresamos a la pantalla anterior
                                        },

                                        // 3. Qué hacer si el login es exitoso
                                        onLoginSuccess = {
                                            navController.navigate(NavRoutes.InicioView.route) {
                                                popUpTo(NavRoutes.LoginView.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }


                                composable(NavRoutes.Analisis.route) {
                                    AnalisisDashboardScreen(
                                        navController = navController,
                                        isLoggedIn = isLoggedIn,
                                        usuario = usuarioLogin,      // ✔ se lo enviamos
                                    )
                                }

                                composable(NavRoutes.Dependencias.route) {
                                    DependenciasScreenConnected(
                                        navController = navController,
                                        loginViewModel = loginViewModel,
                                        isLoggedIn = isLoggedIn,
                                        usuario = usuarioLogin,      // ✔ se lo enviamos
                                        onLoginSuccess = {
                                            loginViewModel.getUser()
                                            navController.navigate(NavRoutes.Dependencias.route) {
                                                popUpTo(NavRoutes.Principal.route) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.CargaConsumos.route) {
                                    CargaConsumosScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel,   // 👈 SE AGREGA
                                        isLoggedIn = isLoggedIn,
                                        usuario = usuarioLogin,
                                        onLoginSuccess = {
                                            loginViewModel.getUser()
                                            navController.navigate(NavRoutes.Dependencias.route) {
                                                popUpTo(NavRoutes.Principal.route) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.Presupuestos.route) {
                                    PresupuestoScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel, // 👈 SE AGREGA
                                        usuario = usuarioLogin,
                                        isLoggedIn = isLoggedIn,
                                        onLoginSuccess = {
                                            loginViewModel.getUser()
                                            navController.navigate(NavRoutes.Dashboard.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                composable(NavRoutes.Usuarios.route) {
                                    PerfilUsuarioScreen(
                                        navController = navController,
                                        loginViewModel = loginViewModel,   // 👈 SE AGREGA
                                        usuario = usuarioLogin,
                                        isLoggedIn = isLoggedIn
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


