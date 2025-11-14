package com.example.pge.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BrowserUpdated
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pge.navigation.NavRoutes
import kotlin.collections.listOf

@Composable
fun DrawerScreen(navController: NavController,
                 isLoggedIn: Boolean
) {

    // Lista de opciones para usuarios NO conectados
    val loggedOutItems = listOf(
        BottomNavItem(Icons.Default.Home, NavRoutes.Principal.route),
        BottomNavItem(Icons.Default.Dashboard, NavRoutes.Dashboard.route),
        BottomNavItem(Icons.Default.Visibility, NavRoutes.Transparencia.route)
    )
    val  loggedInItems = listOf(
        BottomNavItem(Icons.Default.Dashboard, NavRoutes.Dashboard.route),
        BottomNavItem(Icons.Default.AlignVerticalBottom, NavRoutes.Analisis.route),
        BottomNavItem(Icons.Default.AccountBalance, NavRoutes.Dependencias.route),
        BottomNavItem(Icons.Default.BrowserUpdated, NavRoutes.CargaConsumos.route),
        BottomNavItem(Icons.Default.AttachMoney, NavRoutes.Presupuestos.route),
        BottomNavItem(Icons.Default.AccountCircle, NavRoutes.Usuarios.route)
    )

    // Elige la lista correcta basado en el estado
    val items = if (isLoggedIn) {
        loggedInItems
    } else {
        loggedOutItems
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 1f),
        tonalElevation = 4.dp,
        // linea superior
        modifier = Modifier.drawBehind {
            // Define el grosor y el color de la línea
            val strokeWidth = 1.dp.toPx()
            val color = Color.LightGray

            // Dibuja la línea
            drawLine(
                color = color,
                start = Offset(0f, 0f), // Esquina superior izquierda (x=0, y=0)
                end = Offset(size.width, 0f), // Esquina superior derecha (x=ancho, y=0)
                strokeWidth = strokeWidth
            )
        }
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
                label = { Text(item.route)},
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

