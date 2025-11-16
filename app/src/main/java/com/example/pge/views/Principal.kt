package com.example.pge.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pge.navigation.NavRoutes
import com.example.pge.ui.theme.PgeApiGetBg
import com.example.pge.ui.theme.PgeBulletGreen
import com.example.pge.ui.theme.PgeChartBlue
import com.example.pge.ui.theme.PgeGreenButton
import com.example.pge.ui.theme.PgeProgressBarBg
import com.example.pge.ui.theme.PgeTagBg
import com.example.pge.ui.theme.PgeTagText
import kotlin.random.Random

/*
 Pantalla principal que contiene todas las secciones.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PgeHomeScreen(navController: NavController,
                  isLoggedIn: Boolean,
                  onLoginSuccess: () -> Unit // Recibe la lambda
    ) {

    // Estado para controlar la visibilidad del diálogo
    var showLoginDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { PgeTopAppBar(
            isLoggedIn = isLoggedIn,
            onShowLoginClick = {
                showLoginDialog = true
            }) },
        containerColor = Color(0xFFF8FAFC) // Un fondo gris muy claro
    ) { paddingValues ->

        // Si showLoginDialog es true, dibuja el LoginDialog
        if (showLoginDialog) {
            LoginDialog(
                onDismissRequest = {
                    // Cierra el diálogo si se toca fuera o se presiona "X"
                    showLoginDialog = false
                },
                onLoginClick = { email, pass ->
                    // 1. (Opcional) Aquí validas el email y password...
                    // if (viewModel.login(email, pass)) { ... }

                    // 2. Actualizas los estados
                    // 3. SE LLAMA A LA LAMBDA DEL PADRE
                    onLoginSuccess()
                    showLoginDialog = false

                        //  NAVEGAR A DASHBOARD
                        navController.navigate(NavRoutes.Dashboard.route) {
                        // Esto limpia la pila de navegación para que el usuario
                        // no pueda "volver" a la pantalla de login con el botón de atrás.
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        // Asegura que no se apilen múltiples copias de Principal
                        launchSingleTop = true
                    }
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sección 1: Hero (Título, botones)
            item {
                HeroSection(
                    onExploreClick = {
                        //  NAVEGAR A DASHBOARD
                        navController.navigate(NavRoutes.Dashboard.route) {
                            // Esto limpia la pila de navegación para que el usuario
                            // no pueda "volver" a la pantalla de login con el botón de atrás.
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            // Asegura que no se apilen múltiples copias de Principal
                            launchSingleTop = true
                        }
                    },
                    onTransparencyClick = {
                        //  NAVEGAR A DASHBOARD
                        navController.navigate(NavRoutes.Transparencia.route) {
                            // Esto limpia la pila de navegación para que el usuario
                            // no pueda "volver" a la pantalla de login con el botón de atrás.
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            // Asegura que no se apilen múltiples copias de Principal
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Sección 2: Tarjetas de Dashboard
            item {
                DashboardPreviewSection()
            }

            // Sección 3: Objetivos
            item {
                ObjectivesSection()
            }

            // Sección 4: Interoperabilidad y API
            item {
                ApiSection()
            }
        }
    }
}


/*
Sección 1:  Titulos
 */
@Composable
fun HeroSection(
    onExploreClick: () -> Unit,
    onTransparencyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Etiqueta de plataforma de gestión Energética de Q.Roo
        Surface(
            color = PgeTagBg,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Plataforma de Gestión Energética · Quintana Roo",
                color = PgeTagText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Título: "PGE-QROO"
        Text(
            text = "PGE-QROO",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        // Descripción de la plataforma
        Text(
            text = "Centraliza, analiza y predice el consumo y gasto eléctrico de las dependencias estatales para optimizar la asignación presupuestal y fomentar la transparencia.",
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )

        // Botones de acceso y exploración
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onExploreClick,
                colors = ButtonDefaults.buttonColors(containerColor = PgeGreenButton),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Explorar Dashboard",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    fontSize = 12.sp
                )
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            OutlinedButton(
                onClick = onTransparencyClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Transparencia pública",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    fontSize = 12.sp
                )
            }
        }
        // Lista de características de la plataforma
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BulletedListItem(text = "Análisis histórico", color = PgeBulletGreen)
            BulletedListItem(text = "Predicción de gasto", color = PgeBulletGreen)
            BulletedListItem(text = "Interoperable con NDG", color = PgeBulletGreen)
        }
    }
}

@Composable
fun BulletedListItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

/*
Sección 2: Dashboard Preview (captura "5.00.40.png")
 */
@Composable
fun DashboardPreviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


            // Tarjeta de Gasto Total
            StatCard(
                title = "Gasto total",
                value = "$3,640,000",
                modifier = Modifier.fillMaxWidth()
            ) {
                SimulatedBarChart()
            }

            // Tarjeta de Consumo kWh
            StatCard(
                title = "Consumo kWh",
                value = "161,000",
                modifier = Modifier.fillMaxWidth()
            ) {
                SimulatedBarChart()
            }


        // Fila 2: Costo promedio
        StatCard(
            title = "Costo promedio kWh",
            value = "$9.1",
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressIndicator(
                progress = { 0.7f }, // 70% de progreso
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = PgeChartBlue,
                trackColor = PgeProgressBarBg
            )
        }
    }
}

/*
  Tarjeta reutilizable para estadísticas
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.height(IntrinsicSize.Min), // Asegura que las tarjetas en una fila tengan la misma altura
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(), // Rellena la altura mínima
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

/*
Simulación de gráfica de barras
 */
@Composable
fun SimulatedBarChart() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Lista de alturas relativas para las barras
        val barHeights = List(8) { Random.nextFloat() * 0.8f + 0.2f } // entre 20% y 100%

        barHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(height) // Altura relativa
                    .padding(horizontal = 2.dp)
                    .background(PgeChartBlue, RoundedCornerShape(4.dp))
            )
        }
    }
}

/*
Sección 3: Objetivos
 */
@Composable
fun ObjectivesSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Objetivo General
            Text(
                text = "Objetivo general",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Desarrollar una plataforma que se interconecte con los datos de las dependencias para centralizar, analizar y predecir el consumo y gasto energético en Quintana Roo, optimizando la asignación presupuestal y promoviendo la eficiencia.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            // Objetivos Específicos
            Text(
                text = "Objetivos específicos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("•  Analítica de datos históricos con reportes y patrones de consumo.")
                Text("•  Modelo predictivo para estimar gastos futuros por dependencia.")
                Text("•  Dashboard intuitivo para exploración de datos.")
                Text("•  Interoperabilidad con el Núcleo Digital de Gobierno.")
            }
        }
    }
}

/*
Sección 4: API
 */
@Composable
fun ApiSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Interoperabilidad y API",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Este sistema está preparado para consumir una API externa. Configure la variable VITE_API_BASE_URL para apuntar al servicio de datos abiertos.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            // Endpoints
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ApiEndpointItem(method = "GET", endpoint = "/dashboard — Resumen, histórico y predicciones")
                ApiEndpointItem(method = "GET", endpoint = "/departments — Catálogo de dependencias")
            }
        }
    }
}

/*
Item para mostrar un endpoint de API
 */
@Composable
fun ApiEndpointItem(method: String, endpoint: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PgeApiGetBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = method,
                color = Color(0xFF334155),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.width(40.dp) // Ancho fijo para "GET"
            )
            Text(
                text = endpoint,
                color = Color(0xFF475569),
                fontSize = 13.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PgeHomeScreenPreview() {
    MaterialTheme {

        // Fondo gris claro para que la tarjeta blanca resalte, como en tu imagen
        val navController = rememberNavController()
        val isLoggedIn = false // Controlar el estado de inicio de sesión
        PgeHomeScreen(
            navController = navController,
            isLoggedIn = isLoggedIn,
            onLoginSuccess = {
                // Esta lambda se ejecutará cuando el login sea exitoso

                navController.navigate(NavRoutes.Dashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}