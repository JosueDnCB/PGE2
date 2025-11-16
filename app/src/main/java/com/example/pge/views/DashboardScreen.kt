package com.example.pge.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pge.ui.theme.DarkText
import com.example.pge.ui.theme.PgeChartBlue
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

// --- Data classes para los datos de ejemplo ---
data class Inmueble(val nombre: String, val consumo: String)

// --- Composable Principal ---
@Composable
fun DashboardScreen(navController: NavController, isLoggedIn: Boolean, onLoginSuccess: () -> Unit) {

    // Estado para controlar la visibilidad del diálogo
    var showLoginDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { PgeTopAppBar(
            isLoggedIn = isLoggedIn,
            titulo = "Dashboard",
            // Esta lambda se ejecutará cuando el login sea exitoso
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Secretaría de Finanzas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- Resto de tus tarjetas ---
        item {
            InfoCard(
                title = "Consumo del Mes (kWh)",
                value = "150,230 kWh",
                change = "▲ 5.2% vs Mes Anterior",
                changeColor = Color(0xFF388E3C),
                icon = Icons.Default.Bolt
            )
        }
        item {
            InfoCard(
                title = "Costo del Mes (MXN)",
                value = "$285,437.00",
                change = "▼ 2.1% vs Mes Anterior",
                changeColor = Color(0xFFD32F2F),
                icon = Icons.Default.AttachMoney
            )
        }
        item {
            BudgetCard(
                title = "Uso del Presupuesto (Q4)",
                usedAmount = 2.25f,
                totalAmount = 3.00f
            )
        }
        item {
            InfoCard(
                title = "Ahorro vs Mismo Mes Año Anterior",
                value = "$30,150.00",
                change = "(Octubre 2024)",
                changeColor = Color.Gray,
                icon = Icons.Default.Savings
            )
        }
        item {
            EvolutionChartCard()
        }
        item {
            TopConsumptionCard(
                inmuebles = listOf(
                    Inmueble("Edificio Central", "25,120 kWh"),
                    Inmueble("Oficinas Zona Norte", "22,500 kWh"),
                    Inmueble("Archivo General", "18,900 kWh"),
                    Inmueble("Bodega Principal", "15,340 kWh")
                )
            )
        }

    }
}
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    change: String,
    changeColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = change, style = MaterialTheme.typography.bodySmall, color = changeColor)
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BudgetCard(title: String, usedAmount: Float, totalAmount: Float) {
    // Formateador para moneda
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    currencyFormat.maximumFractionDigits = 2

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { usedAmount / totalAmount },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PgeChartBlue,     // Este es el color del progreso
                trackColor = Color.Gray   // color restante


            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatAmount(usedAmount)}M / ${formatAmount(totalAmount)}M",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
        }
    }
}
// Helper para formatear los millones
private fun formatAmount(amount: Float): String {
    return if (amount.rem(1) == 0f) {
        String.format(Locale.US, "%.2f", amount)
    } else {
        String.format(Locale.US, "%.2f", amount)
    }
}


@Composable
fun EvolutionChartCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Evolución de Consumo y Costo - 12 Meses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(16.dp))
            // --- Placeholder para el gráfico ---


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
    }
}


@Composable
fun TopConsumptionCard(inmuebles: List<Inmueble>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inmuebles con Mayor Consumo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Encabezados de la tabla
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "POS.",
                    modifier = Modifier.weight(0.15f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "NOMBRE INMUEBLE",
                    modifier = Modifier.weight(0.55f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CONSUMO",
                    modifier = Modifier.weight(0.3f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Lista de inmuebles
            inmuebles.forEachIndexed { index, inmueble ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.weight(0.15f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = inmueble.nombre,
                        modifier = Modifier.weight(0.55f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText
                    )
                    Text(
                        text = inmueble.consumo,
                        modifier = Modifier.weight(0.3f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End,
                        color = DarkText
                    )
                }
            }
        }
    }
}