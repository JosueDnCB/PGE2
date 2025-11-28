package com.example.pge.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pge.models.EvolucionItem
import com.example.pge.models.PublicDashboard.RankingItem
import com.example.pge.models.UserResponse
import com.example.pge.navigation.NavRoutes
import com.example.pge.viewmodels.LoginViewModel
import com.example.pge.viewmodels.PublicDashboardViewModel
import com.example.pge.views.Dashboard.EvolutionChartCard
import com.example.pge.views.PublicDashboardComponents.BudgetVsExpenseCard
import com.example.pge.views.PublicDashboardComponents.MultiLineChartCard
import com.example.pge.views.PublicDashboardComponents.PublicKpiCard
import com.example.pge.views.PublicDashboardComponents.RankingBarChart
import com.example.pge.views.PublicDashboardComponents.RankingCard
import java.text.NumberFormat
import java.util.*

@Composable
fun PublicDashboardScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    isLoggedIn: Boolean,
    usuario: UserResponse?,
    onLoginSuccess: () -> Unit
) {

    var showLoginDialog by remember { mutableStateOf(false) }
    // Instanciar ViewModel
    val viewModel: PublicDashboardViewModel = viewModel()

    // ESTADOS DE UI (Error y Carga)
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estado del Nivel
    val nivelActual by viewModel.nivelSeleccionado.collectAsState()

    // OBSERVAR LOS ESTADOS DE LOS FILTROS
    val sectores by viewModel.sectores.collectAsState()
    val dependencias by viewModel.dependencias.collectAsState()
    val edificios by viewModel.edificios.collectAsState()

    val sectorSel by viewModel.sectorSel.collectAsState()
    val dependenciaSel by viewModel.dependenciaSel.collectAsState()
    val edificioSel by viewModel.edificioSel.collectAsState()

    // Observar los datos crudos de las APIs
    val consumoData by viewModel.comparativaConsumo.collectAsState()
    val costoData by viewModel.comparativaCostos.collectAsState()
    val rankingData by viewModel.ranking.collectAsState()

    val anioSeleccionado by viewModel.anioSel.collectAsState()
    val presupuestoData by viewModel.presupuestoVsGasto.collectAsState()

    // LÓGICA DE AUTO-SELECCIÓN (CASCADA)

    // Auto-seleccionar primer SECTOR al cargar la pantalla
    LaunchedEffect(sectores) {
        if (sectores.isNotEmpty() && sectorSel == null) {
            viewModel.seleccionarSector(sectores[0])
        }
    }

    // Auto-seleccionar primera DEPENDENCIA cuando cambia la lista
    LaunchedEffect(dependencias) {
        if (dependencias.isNotEmpty()) {
            val primera = dependencias[0]
            // Validamos que sea diferente para no ciclar
            if (dependenciaSel?.nombre != primera.nombre) {
                viewModel.seleccionarDependencia(primera)
            }
        }
    }

    // Auto-seleccionar primer EDIFICIO cuando cambia la lista
    LaunchedEffect(edificios) {
        if (edificios.isNotEmpty()) {
            val primero = edificios[0]
            // Validamos que sea diferente
            if (edificioSel?.nombre != primero.nombre) {
                viewModel.seleccionarEdificio(primero)
            }
        }
    }



    //  EFECTO PARA MOSTRAR ERROR
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            viewModel.limpiarError()
        }
    }

    // LÓGICA DE TRANSFORMACIÓN DE DATOS
    val totalConsumoKwh = remember(consumoData) {
        consumoData?.series?.firstOrNull()?.datos?.sum() ?: 0.0
    }

    val totalGastoDinero = remember(costoData) {
        costoData?.series?.firstOrNull()?.datos?.sum() ?: 0.0
    }

    val rankingItems = remember(rankingData) {
        rankingData?.let { resp ->
            val valores = resp.series.firstOrNull()?.datos ?: emptyList()
            resp.ejeX.zip(valores) { nombre, valor ->
                RankingItem(nombre, valor)
            }
        } ?: emptyList()
    }

    val chartItems = remember(consumoData, costoData) {
        val listaConsumo = consumoData?.series?.firstOrNull()?.datos ?: List(12) { 0.0 }
        val listaCosto = costoData?.series?.firstOrNull()?.datos ?: List(12) { 0.0 }

        listaConsumo.zip(listaCosto).mapIndexed { index, (cons, cost) ->
            EvolucionItem(
                año = anioSeleccionado,
                mes = index + 1,
                total_consumo = cons,
                total_costo = cost
            )
        }
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX"))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply { maximumFractionDigits = 0 }


    // UI PRINCIPAL
    Scaffold(
        topBar = {
            PgeTopAppBar(
                isLoggedIn = isLoggedIn,
                titulo = "Transparencia",
                usuarios = usuario,
                onShowLoginClick = {  showLoginDialog = true}
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->

        if (showLoginDialog) {
            LoginDialog(
                loginViewModel = loginViewModel,
                onDismissRequest = { showLoginDialog = false },
                onLoginSuccess = {
                    showLoginDialog = false

                    // Navegar al Dashboard
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }


        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // FILTROS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Configuración de Comparativa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )

                            // SELECTOR DE AÑO (Global)
                            DropdownFiltro(
                                label = "Año Fiscal",
                                opciones = viewModel.aniosDisponibles.map { it.toString() },
                                seleccionActual = anioSeleccionado.toString(),
                                onSeleccionChange = { viewModel.seleccionarAnio(it.toInt()) }
                            )

                            Divider(color = Color.LightGray.copy(alpha = 0.3f))

                            // SELECTOR DE NIVEL (Sectores vs Dependencias vs Edificios)
                            Text(
                                "¿Qué deseas comparar?",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            DropdownFiltro(
                                label = "Nivel de Comparación",
                                opciones = viewModel.nivelesDisponibles, // ["Sectores", "Dependencias", "Edificios"]
                                seleccionActual = nivelActual,
                                onSeleccionChange = { nuevoNivel ->
                                    viewModel.cambiarNivel(nuevoNivel)
                                }
                            )

                            // FILTROS CONTEXTUALES (Aparecen según el nivel)

                            // CASO A: Si compara DEPENDENCIAS, necesita elegir un Sector Padre
                            if (nivelActual == "Dependencias" || nivelActual == "Edificios") {
                                DropdownFiltro(
                                    label = "Filtrar por Sector",
                                    opciones = sectores.map { it.nombre },
                                    seleccionActual = sectorSel?.nombre,
                                    onSeleccionChange = { nombre ->
                                        val obj = sectores.find { it.nombre == nombre }
                                        obj?.let { viewModel.seleccionarSector(it) }
                                    }
                                )
                            }

                            // Si compara EDIFICIOS, necesita elegir una Dependencia Padre
                            if (nivelActual == "Edificios") {
                                // Solo mostramos si ya eligió sector (o si hay dependencias cargadas)
                                if (dependencias.isNotEmpty()) {
                                    DropdownFiltro(
                                        label = "Filtrar por Dependencia",
                                        opciones = dependencias.map { it.nombre },
                                        seleccionActual = dependenciaSel?.nombre,
                                        onSeleccionChange = { nombre ->
                                            val obj = dependencias.find { it.nombre == nombre }
                                            obj?.let { viewModel.seleccionarDependencia(it) }
                                        }
                                    )
                                } else {
                                    Text(
                                        "Selecciona un sector primero",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Red
                                    )
                                }
                            }

                            // Mensaje informativo
                            val infoText = when (nivelActual) {
                                "Sectores" -> "Comparando todos los sectores del estado."
                                "Dependencias" -> "Comparando todas las dependencias del sector ${sectorSel?.nombre ?: "seleccionado"}."
                                "Edificios" -> "Comparando todos los edificios de ${dependenciaSel?.nombre ?: "la dependencia"}."
                                else -> ""
                            }
                            Text(
                                text = infoText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Ranking de mayor consumo energético",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // KPIS
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PublicKpiCard(
                                titulo = "Consumo Energético Total",
                                valor = "${numberFormat.format(totalConsumoKwh)} kWh",
                                icono = Icons.Default.Bolt,
                                colorIcono = Color(0xFFFFA000)
                            )
                        }

                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PublicKpiCard(
                                titulo = "Gasto Público Total",
                                valor = currencyFormat.format(totalGastoDinero),
                                icono = Icons.Default.AttachMoney,
                                colorIcono = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                // RANKING
                item {
                    Text(
                        text = "Ranking de mayor consumo energético",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    // Si hay datos en rankingData
                    if (rankingData != null && rankingData!!.series.isNotEmpty()) {

                        // Pasar el objeto completo
                        RankingBarChart(data = rankingData)

                    } else if (sectorSel != null && !isLoading) {
                        Text(
                            text = "No hay datos de ranking disponibles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // GRÁFICAS
                item {
                    Text(
                        text = "Análisis Comparativo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Gráfica de Consumo (kWh)
                item {
                    // Verificamos si hay datos antes de pintar
                    if (consumoData != null && consumoData!!.series.isNotEmpty()) {
                        MultiLineChartCard(
                            data = consumoData,
                            isCurrency = false // Formato numérico normal
                        )
                    } else if (sectorSel != null && !isLoading) {
                        // Mensaje discreto si no hay datos
                        Text(
                            "No hay datos de consumo para mostrar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                // Gráfica de Costos ($)
                item {
                    if (costoData != null && costoData!!.series.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre gráficas

                        MultiLineChartCard(
                            data = costoData,
                            isCurrency = true // Formato dinero ($)
                        )
                    }
                }

                // GRÁFICA PRESUPUESTO VS GASTO
                item {
                    // Siempre mostramos el título para saber de qué trata la sección
                    Text(
                        text = "Análisis Financiero",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )

                    if (presupuestoData != null && presupuestoData!!.series.isNotEmpty()) {

                        // SI HAY DATOS Mostrar la gráfica
                        BudgetVsExpenseCard(data = presupuestoData)

                    } else {

                        // NO HAY DATOS Mostramos la tarjeta de "Sin información"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Personalizamos el mensaje dependiendo si es un error o simplemente que está vacío
                                val icon = if (errorMessage != null) Icons.Default.Error else Icons.Default.AttachMoney // Use icono de dinero para contexto
                                val text = if (errorMessage != null) "Error al cargar finanzas" else "Sin información financiera"
                                val color = if (errorMessage != null) MaterialTheme.colorScheme.error else Color.LightGray

                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = text,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

            }
            // INDICADOR DE CARGA (Overlay)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
