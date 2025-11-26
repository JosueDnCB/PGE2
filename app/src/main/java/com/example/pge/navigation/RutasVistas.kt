package com.example.pge.navigation


sealed class NavRoutes(val route: String) {


    object Transparencia : NavRoutes("Transparencia")


    object Principal : NavRoutes("Inicio")
    object Dashboard : NavRoutes("Dashboard")
    object Analisis : NavRoutes("Analisis")
    object Dependencias : NavRoutes("Dependencias")
    object CargaConsumos : NavRoutes("Carga")
    object Presupuestos : NavRoutes("Presupuestos")
    object Usuarios : NavRoutes("Usuarios")

    object InicioView : NavRoutes("firstView")

    object LoginView : NavRoutes("Login")
}