package com.example.pge.views.Navigation

import com.example.pge.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Mueve tu data class aquí para que todos la puedan ver
data class BottomNavItem(
    val icon: ImageVector,
    val route: String
)

object NavigationConfig {
    // Tus listas originales
    private val loggedOutItems = listOf(
        BottomNavItem(Icons.Default.Home, NavRoutes.Principal.route),
        BottomNavItem(Icons.Default.Dashboard, NavRoutes.PublicDashboard.route),
        BottomNavItem(Icons.Default.Visibility, NavRoutes.Transparencia.route)
    )

    private val loggedInItems = listOf(
        BottomNavItem(Icons.Default.Dashboard, NavRoutes.Dashboard.route),
        BottomNavItem(Icons.Default.AlignVerticalBottom, NavRoutes.Analisis.route),
        BottomNavItem(Icons.Default.AccountBalance, NavRoutes.Dependencias.route),
        BottomNavItem(Icons.Default.BrowserUpdated, NavRoutes.CargaConsumos.route),
        BottomNavItem(Icons.Default.AttachMoney, NavRoutes.Presupuestos.route),
        BottomNavItem(Icons.Default.AccountCircle, NavRoutes.Usuarios.route)
    )

    // La función mágica que decide cuál entregar
    fun getItems(isLoggedIn: Boolean): List<BottomNavItem> {
        return if (isLoggedIn) loggedInItems else loggedOutItems
    }
}