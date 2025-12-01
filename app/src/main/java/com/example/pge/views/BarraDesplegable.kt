package com.example.pge.views

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pge.views.Navigation.NavigationConfig
import com.example.pge.views.Navigation.navigateToTab
import com.example.pge.ui.theme.DarkText
import com.example.pge.ui.theme.PgeGreenButton

@Composable
fun DrawerScreen(
    navController: NavController,
    isLoggedIn: Boolean
) {
    val items = NavigationConfig.getItems(isLoggedIn)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 1f),
        modifier = Modifier.drawBehind {
            val strokeWidth = 1.dp.toPx()
            val color = Color.LightGray
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
        }
    ) {
        val currentRoute = currentRoute(navController)

        items.forEach { item ->
            // Comparación segura para resaltar el ícono
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // USA LA MISMA FUNCIÓN QUE EL SWIPE
                    navigateToTab(navController, item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = item.route, fontSize = 9.sp)
                },
                alwaysShowLabel = false,
                colors = if (isLoggedIn) {
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.Gray,
                        indicatorColor = PgeGreenButton,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                } else {
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkText,
                        selectedTextColor = Color.Gray,
                        indicatorColor = Color.Gray.copy(alpha = 0.3f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}