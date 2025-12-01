package com.example.pge.views.Navigation

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import com.example.pge.views.Navigation.NavigationConfig
import com.example.pge.views.Navigation.navigateToTab

fun Modifier.swipeToNavigate(
    navController: NavController,
    isLoggedIn: Boolean
): Modifier = this.pointerInput(isLoggedIn) {

    val items = NavigationConfig.getItems(isLoggedIn)
    val minSwipeDistance = 50f
    var totalDrag = 0f

    detectHorizontalDragGestures(
        onDragEnd = {
            val currentRoute = navController.currentDestination?.route
            val currentIndex = items.indexOfFirst { it.route == currentRoute }

            // Solo navegamos si encontramos la ruta actual en la lista
            if (currentIndex != -1) {
                // -> Deslizar DERECHA (Ir Atrás)
                if (totalDrag > minSwipeDistance) {
                    if (currentIndex > 0) {
                        val prevRoute = items[currentIndex - 1].route
                        // USA LA MISMA FUNCIÓN CENTRALIZADA
                        navigateToTab(navController, prevRoute)
                    }
                }
                // <- Deslizar IZQUIERDA (Ir Adelante)
                else if (totalDrag < -minSwipeDistance) {
                    if (currentIndex < items.size - 1) {
                        val nextRoute = items[currentIndex + 1].route
                        // USA LA MISMA FUNCIÓN CENTRALIZADA
                        navigateToTab(navController, nextRoute)
                    }
                }
            }
            totalDrag = 0f
        },
        onHorizontalDrag = { _, dragAmount ->
            totalDrag += dragAmount
        }
    )
}