package com.example.pge.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pge.models.DashboardResponse
import com.example.pge.models.InmuebleItem
import com.example.pge.models.UserResponse
import com.example.pge.ui.theme.DarkText
import com.example.pge.ui.theme.PgeChartBlue
import com.example.pge.viewmodels.DashboardUiState
import com.example.pge.viewmodels.DashboardViewModel
import com.example.pge.viewmodels.LoginViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

@Composable
fun DashboardScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    isLoggedIn: Boolean,
    usuario: UserResponse?,
    onLoginSuccess: () -> Unit,
    viewModel: DashboardViewModel = viewModel() // Inyectamos el ViewModel aquí
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {

            PgeTopAppBar(
                 isLoggedIn = isLoggedIn,
                 titulo = "Dashboard",
                usuarios = usuario,
                onShowLoginClick = {
                    onLoginSuccess() // si quieres disparar la acción de login
                }
            )

        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            when (uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Text("Ocurrió un error: ${uiState.message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.fetchDashboardData() }) {
                            Text("Reintentar")
                        }
                    }
                }
                is DashboardUiState.Success -> {
                    // Renderizamos el contenido real con los datos de la API
                    DashboardContent(data = uiState.data)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(data: DashboardResponse) {

    // Formateadores
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Secretaría de Finanzas - Período ${data.periodo.mes}/${data.periodo.año}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 1. Consumo
        item {
            InfoCard(
                title = "Consumo del Mes (kWh)",
                value = "${numberFormat.format(data.kpis.consumo_mes_kwh)} kWh",
                change = "Datos actualizados", // Podrías calcular la variación si el backend la manda
                //change = "▲ 5.2% vs Mes Anterior",
                changeColor = Color(0xFF388E3C),
                icon = Icons.Default.Bolt
            )
        }

        // 2. Costo
        item {
            InfoCard(
                title = "Costo del Mes (MXN)",
                value = currencyFormat.format(data.kpis.costo_mes),
                change = "Total facturado",
               // change = "▼ 2.1% vs Mes Anterior",
                changeColor = Color(0xFFD32F2F),
                icon = Icons.Default.AttachMoney
            )
        }

        // 3. Presupuesto
        item {
            // Calcular uso para la barra de progreso
            // Evitar división por cero
            val used = data.kpis.costo_mes.toFloat()
            val total = data.kpis.presupuesto_trimestre.toFloat()

            BudgetCard(
                title = "Presupuesto Trimestre ${data.periodo.trimestre}",
                usedAmount = used,
                totalAmount = total
            )
        }

        // 4. Gráfica
        item {
            // Pasamos los datos reales para pintar las barras dinámicamente (simplificado)
            EvolutionChartCard(data.data_evolucion.map { it.total_consumo.toFloat() })
        }

        // 5. Top Inmuebles
        item {
            TopConsumptionCard(inmuebles = data.data_inmuebles)
        }
    }
}

// --- Componentes Reutilizables Adaptados ---

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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(text = change, style = MaterialTheme.typography.bodySmall, color = changeColor)
            }
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun BudgetCard(title: String, usedAmount: Float, totalAmount: Float) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    // Evitamos NaN si el total es 0
    val progress = if (totalAmount > 0) (usedAmount / totalAmount).coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (progress > 0.9f) Color.Red else PgeChartBlue,
                trackColor = Color.LightGray,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Gastado: ${currencyFormat.format(usedAmount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text(text = "Total: ${currencyFormat.format(totalAmount)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EvolutionChartCard(dataPoints: List<Float>) {
    val maxVal = dataPoints.maxOrNull() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Evolución Consumo (Últimos 12 meses)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Si no hay datos, mostramos placeholder
                val listToRender = if (dataPoints.isEmpty()) List(5) { 0.2f } else dataPoints

                listToRender.forEach { value ->
                    // Altura relativa
                    val heightFraction = (value / maxVal).coerceIn(0.1f, 1f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(heightFraction)
                            .padding(horizontal = 4.dp)
                            .background(PgeChartBlue, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun TopConsumptionCard(inmuebles: List<InmuebleItem>) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Inmuebles con Mayor Consumo", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            inmuebles.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${index + 1}", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, color = PgeChartBlue)
                    Text(text = item.nombre_edificio, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${numberFormat.format(item.consumo)} kWh", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                if (index < inmuebles.lastIndex) Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}