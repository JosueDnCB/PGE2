package com.example.pge.navigation


sealed class NavRoutes(val route: String) {
    object Dashboard : NavRoutes("dashboard")
    object Analisis : NavRoutes("analisis")
    object Dependencias : NavRoutes("dependencias")
    object CargaConsumos : NavRoutes("carga_consumos")
    object Presupuestos : NavRoutes("presupuestos")
    object Usuarios : NavRoutes("usuarios")
}