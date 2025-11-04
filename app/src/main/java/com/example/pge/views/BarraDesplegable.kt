package com.example.pge.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BrowserUpdated
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pge.navigation.NavRoutes

@Composable
fun DrawerScreen(navController: NavController) {
    val items = listOf(
        BottomNavItem(Icons.Default.Dashboard, NavRoutes.Dashboard.route),
        BottomNavItem(Icons.Default.AlignVerticalBottom, NavRoutes.Analisis.route),
        BottomNavItem(Icons.Default.AccountBalance, NavRoutes.Dependencias.route),
        BottomNavItem(Icons.Default.BrowserUpdated, NavRoutes.CargaConsumos.route),
        BottomNavItem(Icons.Default.AttachMoney, NavRoutes.Presupuestos.route),
        BottomNavItem(Icons.Default.AccountCircle, NavRoutes.Usuarios.route)
    )


    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        val currentRoute = currentRoute(navController)

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = null,
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomNavItem(
    val icon: ImageVector,
    val route: String
)

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

