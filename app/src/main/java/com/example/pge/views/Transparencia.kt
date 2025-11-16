package com.example.pge.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage // Este ícono se parece al de tu imagen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pge.navigation.NavRoutes
import com.example.pge.ui.theme.DarkText
import com.example.pge.ui.theme.PgeCardBorder
import com.example.pge.ui.theme.PgeGreenButton


/*
Tarjeta principal del Módulo de Transparencia
 */
@Composable
fun TransparencyModuleCard(navController: NavController, isLoggedIn: Boolean = false,  onLoginSuccess: () -> Unit) {
    // Estado para controlar la visibilidad del diálogo
    var showLoginDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { PgeTopAppBar(
            isLoggedIn = isLoggedIn,
            titulo = "Transparencia",
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
                    // --- Aquí va tu lógica de inicio de sesión ---
                    Log.d("Login", "Email: $email, Pass: $pass")
                    // Si el login es exitoso:
                    onLoginSuccess()
                    showLoginDialog = false // Cierra el diálogo
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
            item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    )  {
                        // Título Principal ---

                            Text(
                                text = "Módulo de Transparencia",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            // Párrafo de Descripción ---
                            Text(
                                text = "Consulta ciudadana del consumo y gasto energético por dependencia. Esta sección expone indicadores clave y series históricas consumidas desde una API pública conforme al Núcleo Digital de Gobierno.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Color de texto secundario
                            )

                            // Tarjetas de Características ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureInfoCard(
                                    title = "Indicadores abiertos",
                                    description = "Consumo total, gasto y eficiencia por kWh."
                                )
                                FeatureInfoCard(
                                    title = "Descarga de datos",
                                    description = "Exportación en CSV y JSON desde la API."
                                )
                                FeatureInfoCard(
                                    title = "Comparativas",
                                    description = "Benchmarking entre dependencias y periodos."
                                )
                            }

                            // Botón de Documentación ---
                            Button(
                                onClick = { /* TODO: Acción del botón */ },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PgeGreenButton, // Tu color verde
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage, // Ícono de "servidor" o "API"
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Documentación de API",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }


                    }

            }

            item {
                ObjectivesSection()
            }
            item {
                ApiSection()
            }
            }
        }
}


/*
Composable reutilizable para las 3 tarjetas de características internas.
 */
@Composable
fun FeatureInfoCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Borde sutil, idéntico al de la imagen
        border = BorderStroke(1.dp, PgeCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título de la característica
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Descripción de la característica
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Función de pruebas
@Preview(showBackground = true)
@Composable
fun TransparencyModulePreview() {

    MaterialTheme {

        // Fondo gris claro para que la tarjeta blanca resalte, como en tu imagen
            val navController = rememberNavController()
            val isLoggedIn = true // Controlar el estado de inicio de sesión
            TransparencyModuleCard(
                navController = navController,
                isLoggedIn = isLoggedIn,
                onLoginSuccess = {

                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
    }
}