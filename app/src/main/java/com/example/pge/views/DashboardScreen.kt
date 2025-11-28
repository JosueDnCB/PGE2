package com.example.pge.views

import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.pge.viewmodels.DependenciasViewModel
import com.example.pge.viewmodels.LoginViewModel
import com.example.pge.views.Dashboard.EvolutionChartCard
import java.text.NumberFormat
import java.util.*
import com.example.pge.models.PublicDashboard.RespuestaComparativa
import com.example.pge.models.PublicDashboard.SerieGrafica
import com.example.pge.views.PublicDashboardComponents.RankingBarChart // Importa tu componente gráfico

@Composable
fun DashboardScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    isLoggedIn: Boolean,
    usuario: UserResponse?,
    onLoginSuccess: () -> Unit
) {
    var showLoginDialog by remember { mutableStateOf(false) }

    val viewModel: DashboardViewModel = viewModel()
    val uiState = viewModel.uiState

    val nombreDependencia by viewModel.opcionDependencia.collectAsState()

    Scaffold(
        topBar = {

            PgeTopAppBar(
                 isLoggedIn = isLoggedIn,
                 titulo = "Dashboard",
                usuarios = usuario,
                onShowLoginClick = {
                    onLoginSuccess()
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
                    DashboardContent(
                        data = uiState.data,
                        nombreSeleccionado = nombreDependencia
                    )
                }
            }
        }
    }
}
@Composable
fun DashboardContent(
    data: DashboardResponse,
    nombreSeleccionado: String
) {

    // Formateadores
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX"))

    val context = LocalContext.current.applicationContext as Application
    val viewModelDespendencias = remember { DependenciasViewModel(context) }
    val viewModel: DashboardViewModel = viewModel()

    // Estado de la UI (Texto seleccionado)
    val textoSeleccionado by viewModel.opcionDependencia.collectAsState()

    // Convertimos el flujo de datos del ViewModel en un Estado de Compose
    val listaDependencias by viewModelDespendencias.dependencias.collectAsState()

    // CARGAR LOS DATOS
    // LaunchedEffect para pedir las dependencias apenas se dibuje este componente
    LaunchedEffect(Unit) {
        viewModelDespendencias.cargarDependencias()
    }

    // AUTO SELECCIONAR EL PRIMER ELEMENTO
    // Se ejecuta cada vez que la lista de dependencias cambia ( cuando la API responde)
    LaunchedEffect(listaDependencias) {
        if (listaDependencias.isNotEmpty()) {

            // Tomamos el primer objeto COMPLETO (ID y Nombre) directamente de la lista
            val primeraDependencia = listaDependencias[0]

            // Solo actualizamos si lo que está seleccionado es diferente (para evitar bucles)
            if (textoSeleccionado != primeraDependencia.nombre) {

                viewModel.cambiarDependencia(
                    primeraDependencia.id,      // ID real (ej. 5)
                    primeraDependencia.nombre   // Nombre real (ej. "Secretaría de Salud")
                )
            }
        }
    }
    // ADAPTADOR DE DATOS
    // Convertimos la lista de Inmuebles (Privado) al formato de RespuestaComparativa (Público)
    // para poder reutilizar la gráfica RankingBarChart.
    val rankingData = remember(data.data_inmuebles) {
        if (data.data_inmuebles.isEmpty()) null
        else {
            RespuestaComparativa(
                titulo = "Inmuebles con Mayor Consumo",
                ejeX = data.data_inmuebles.map { it.nombre_edificio },
                series = listOf(
                    SerieGrafica(
                        nombre = "Consumo",
                        datos = data.data_inmuebles.map { it.consumo },
                        color = null
                    )
                ),
                dependenciasInvolucradas = emptyList()
            )
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // El titulo
        item {
            // cambio de nombre de la dependencia de manera automatica
            val tituloAMostrar = if (nombreSeleccionado == "Dependencias") "Vista General" else nombreSeleccionado

            Text(
                text = "$tituloAMostrar - Período ${data.periodo.mes}/${data.periodo.año}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
        // FILTROS
        // Solo se mustramos este item si hay MÁS de una dependencia en la lista.
        // Si hay 0 (cargando/error) o 1 (usuario restringido), se oculta.
        if (listaDependencias.size > 1) {
            item {
                FiltrosCardDashboard(
                    viewModel = viewModel,
                    viewModelDependencias = viewModelDespendencias
                )
            }
        }

        // Consumo
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

        // Costo
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

        // Presupuesto
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

        // Gráfica de Evolución
        item {

            EvolutionChartCard(
                dataPoints = data.data_evolucion
            )
        }

        // Top Inmuebles
        item {
            if (rankingData != null) {
                // Usamos el componente visual que hicimos para el público
                RankingBarChart(data = rankingData)
            } else {
                // Fallback por si la lista viene vacía
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No hay datos de inmuebles para este periodo.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Componentes Reutilizables Adaptados

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


/*
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
}*/

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

@Composable
fun FiltrosCardDashboard(
    viewModel: DashboardViewModel,
    viewModelDependencias: DependenciasViewModel
) {
    // Estados observados
    val textoSeleccionado by viewModel.opcionDependencia.collectAsState()
    val anioSel by viewModel.anioSeleccionado.collectAsState()
    val mesSel by viewModel.mesSeleccionado.collectAsState()

    val listaDependencias by viewModelDependencias.dependencias.collectAsState()

    // Opciones para Dependencias
    val opcionesDependencias = remember(listaDependencias) { listaDependencias.map { it.nombre } }

    // Opciones para Meses (Dinámico)
    val mesesValidos = viewModel.obtenerMesesValidos()
    val nombresMeses = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    // Cargar Dependencias al inicio
    LaunchedEffect(Unit) {
        viewModelDependencias.cargarDependencias()
    }

    // AUTO SELECCIONAR EL PRIMER ELEMENTO (Dependencia)
    LaunchedEffect(listaDependencias) {
        if (listaDependencias.isNotEmpty()) {
            val primera = listaDependencias[0]
            if (textoSeleccionado != primera.nombre) {
                viewModel.cambiarDependencia(primera.id, primera.nombre)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // DEPENDENCIA
            DropdownFiltro(
                label = "Dependencia",
                opciones = opcionesDependencias,
                seleccionActual = textoSeleccionado,
                onSeleccionChange = { nombre ->
                    val dep = listaDependencias.find { it.nombre == nombre }
                    if (dep != null) viewModel.cambiarDependencia(dep.id, dep.nombre)
                }
            )

            // FILA DE FECHAS
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // AÑO
                Box(modifier = Modifier.weight(1f)) {
                    DropdownFiltro(
                        label = "Año",
                        opciones = viewModel.aniosDisponibles.map { it.toString() },
                        seleccionActual = anioSel.toString(),
                        onSeleccionChange = { nuevoAnio ->
                            viewModel.cambiarAnio(nuevoAnio.toInt())
                        }
                    )
                }

                // MES
                Box(modifier = Modifier.weight(1f)) {
                    // Convertimos el número de mes (11) a nombre ("Noviembre") para mostrar
                    // Restamos 1 porque la lista nombresMeses empieza en índice 0
                    val nombreMesActual = nombresMeses.getOrElse(mesSel - 1) { "" }

                    // Solo mostramos los nombres de meses válidos según el año
                    val opcionesMesesTexto = mesesValidos.map { nombresMeses[it - 1] }

                    DropdownFiltro(
                        label = "Mes",
                        opciones = opcionesMesesTexto,
                        seleccionActual = nombreMesActual,
                        onSeleccionChange = { nombre ->
                            // Convertimos nombre "Noviembre" a número (11)
                            val numeroMes = nombresMeses.indexOf(nombre) + 1
                            viewModel.cambiarMes(numeroMes)
                        }
                    )
                }
            }
        }
    }
}
