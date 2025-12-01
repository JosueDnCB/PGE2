package com.example.pge.views.Navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun navigateToTab(navController: NavController, route: String) {

    // Verificamos si ya estamos en esa pantalla para evitar recargas
    val currentRoute = navController.currentDestination?.route
    if (currentRoute == route) return

    navController.navigate(route) {
        // Pop hasta el inicio del grafo para no acumular pantallas
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true // GUARDA el estado de las pantallas previas
        }
        // Evita copias múltiples de la misma pantalla si spameas el click
        launchSingleTop = true

        // RESTAURA el estado (scroll, inputs) al volver a esta pestaña
        restoreState = true
    }
}