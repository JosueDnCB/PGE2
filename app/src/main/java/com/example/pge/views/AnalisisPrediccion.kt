package com.example.pge.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pge.models.UserResponse
import com.example.pge.models.analisisprediccion.AccionEstrategica
import com.example.pge.ui.theme.GrayCard
import com.example.pge.ui.theme.PgeGreenButton
import com.example.pge.viewmodels.AnalisisUiState
import com.example.pge.viewmodels.AnalisisViewModel
import com.example.pge.viewmodels.IaUiState
import com.example.pge.views.AnalisisPrediccion.GraficaProyeccionInteractiva



@Composable
fun AnalisisDashboardScreen(navController: NavController, isLoggedIn: Boolean, usuario: UserResponse?) {

    // Estado para controlar la visibilidad del diálogo
    var showLoginDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val viewModel: AnalisisViewModel = viewModel()

    // Estado para saber si el usuario inició sesión
    Scaffold(
        topBar = {
            PgeTopAppBar(
                isLoggedIn = isLoggedIn,
                titulo = "Análisis y predicción",
                usuarios = usuario,
                onShowLoginClick = { showLoginDialog = true }
            )
        },
        containerColor = Color(0xFFF8FAFC) // Un fondo gris muy claro
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Título Principal
            item {
                TituloPrincipal()
            }

            // 2. Tarjeta de Filtros
            item {
                FiltrosCard(viewModel = viewModel)
            }

            // 3. Tarjeta de Predicción de Gasto
            item {
                PrediccionGastoCard(viewModel = viewModel)
            }
            // 4. Tarjeta de Análisis Estratégico
            item {
                AnalisisEstrategicoCard(viewModel = viewModel)
            }

            // 5. Tarjeta de Comparativa de Consumo
            item {
                ComparativaConsumoCard()
            }
        }
    }
}

@Composable
fun TituloPrincipal() {



    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Análisis y Predicción Energética",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Análisis avanzado de consumo y predicciones basadas en datos históricos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosCard(viewModel: AnalisisViewModel) {
    // Observamos el valor actual del ViewModel
    val rangoFechaActual by viewModel.rangoSeleccionado.collectAsState()
    val rangoHistorialActual by viewModel.opcionHistorial.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Rango de Fechas"
                )
                Text(
                    text = "Rango de Fechas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Dropdown de Rango de Fechas
            DropdownFiltro(
                opciones = listOf("Últimos 12 meses", "Últimos 6 meses", "Último mes"),
                // Le decimos qué mostrar
                seleccionActual = rangoFechaActual,

                // Le decimos qué hacer cuando cambia
                onSeleccionChange = { nuevoValor ->
                    viewModel.cambiarRangoFecha(nuevoValor)
                }
            )

            Text(
                text = "Periodo del historial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Dropdown de Categoría
            DropdownFiltro(
                opciones = listOf( "Todo el historial", "Año actual"),
                // Le decimos qué mostrar
                seleccionActual = rangoHistorialActual,

                // Le decimos qué hacer cuando cambia
                onSeleccionChange = { nuevoValor ->
                    viewModel.cambiarRangoHistorial(nuevoValor)
                }
            )

            /*// Obtener lista de dependencias desde la api que devuelba el id y nombre de cada una
            Text(
                text = "Dependencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            DropdownFiltro(
                // integrar ids de estas dependencias
                opciones = listOf("Secretaría de Finanzas", "Secretaría de Educación", "Secretaría de Salud")
            )*/
            /*
            Text(
                text = "Presupuestos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Dropdown de Categoría
            DropdownFiltro(
                opciones = listOf("Todas las Categorías", "Categoría A", "Categoría B")
            )*/
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltro(
    opciones: List<String>,
    seleccionActual: String? = null,          // <--- RECIBE EL VALOR
    onSeleccionChange: ((String) -> Unit)? = null, // <--- AVISA EL CAMBIO
    modifier: Modifier = Modifier,
    label: String? = null,
    icono: @Composable (() -> Unit)? = null
) {

    // Estado para saber si el menú está expandido o no
    var isExpanded by remember { mutableStateOf(false) }

    // Estado para guardar la opción seleccionada
    var selectedOption by remember {
        mutableStateOf(opciones.getOrNull(0) ?: "")
    }

    // El contenedor principal del menú dropdown
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier
    ) {

        // El campo de texto que muestra la selección
        if (seleccionActual != null) {
            OutlinedTextField(
                value = seleccionActual, // USAR EL VALOR RECIBIDO
                onValueChange = {}, // Vacío porque es de solo lectura
                readOnly = true,
                // --- Lógica del Label ---
                // Si el label NO es nulo, lo mostramos.
                label = label?.let { { Text(text = it) } },
                leadingIcon = icono,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // Importante para anclar el menú

            )
        }
        // El menú que se despliega
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(PgeGreenButton.copy(alpha = 0.3f))
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(
                        text = opcion
                    ) },
                    onClick = {
                        if (onSeleccionChange != null) {
                            onSeleccionChange(opcion)
                        }
                        isExpanded = false
                    }

                )
            }
        }
    }
}

@Composable
fun PrediccionGastoCard(
    viewModel: AnalisisViewModel
) {
    // Recolectamos el estado del ViewModel que nos pasaron
    val uiState by viewModel.uiState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Predicción de Gasto (Próximos Meses)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Contenido dinámico según estado
            when (val state = uiState) {
                // Cargando
                is AnalisisUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // Mensaje de error
                is AnalisisUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}", color = Color.Red)
                    }
                }
                //Se dibuja la gráfica
                is AnalisisUiState.Success -> {
                    val datos = state.data.datosGrafica
                    val resumen = state.data.resumen

                    // AQUI LLAMAMOS A LA GRÁFICA PERSONALIZADA
                    GraficaProyeccionInteractiva(
                        datos = datos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp) // Un poco más alto para el tooltip
                            .padding(vertical = 8.dp)
                    )

                    // Leyenda dinámica basada en datos reales
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F8E9), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Interpretación (${resumen.tendencia}):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Se proyecta un total de $${String.format("%,.2f", resumen.costoTotalProyectado)} en los próximos ${resumen.horizonteMeses} meses. El área verde indica el margen de error posible.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalisisEstrategicoCard(
    viewModel: AnalisisViewModel
) {
    val iaState by viewModel.iaUiState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth()
            .animateContentSize() //  crezca suavemente
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabecera con Icono
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "IA",
                    tint = Color(0xFF6200EA) // Un color morado tipo "IA"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Consultoría Estratégica por IA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (val state = iaState) {
                is IaUiState.Idle -> {
                    Text("Obtener un análisis cualitativo avanzado sobre tus proyecciones.")
                    Button(
                        onClick = { viewModel.cargarAnalisisEstrategico() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Generar Estrategias")
                    }
                }
                is IaUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6200EA))
                        Spacer(Modifier.height(8.dp))
                        Text("Analizando datos con Gemini...", style = MaterialTheme.typography.bodySmall)
                    }
                }
                is IaUiState.Error -> {
                    Text("No se pudo generar el reporte: ${state.message}", color = Color.Red)
                    Button(onClick = { viewModel.cargarAnalisisEstrategico() }) { Text("Reintentar") }
                }
                is IaUiState.Success -> {
                    val data = state.data

                    // Título y Riesgo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.titulo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        BadgeRiesgo(nivel = data.nivelRiesgo)
                    }

                    // Resumen Ejecutivo
                    Text(
                        text = data.resumenEjecutivo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.DarkGray
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Acciones Recomendadas:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Lista de Acciones
                    // permitir que la tarjeta crezca lo necesario
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.acciones.forEach { accion ->
                            ItemAccionEstrategica(accion)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeRiesgo(nivel: String) {
    val (colorFondo, texto) = when (nivel.uppercase()) {
        "BAJO" -> Color(0xFFC8E6C9) to "Riesgo Bajo"
        "MEDIO" -> Color(0xFFFFE0B2) to "Riesgo Medio"
        "ALTO" -> Color(0xFFFFCDD2) to "Riesgo Alto"
        else -> Color.LightGray to nivel
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ItemAccionEstrategica(item: AccionEstrategica) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "*", modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = item.accion,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Plazo: ${item.plazo}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6200EA),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
fun ComparativaConsumoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Comparativa de Consumo por Dependencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // --- PLACEHOLDER PARA LA GRÁFICA ---
            // Aquí debes reemplazar este Box con tu componente de gráfica de barras
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GrayCard.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Placeholder: Gráfica de Barras",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Leyenda
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).background(Color(0xFFB71C1C), CircleShape)) // Rojo
                    Spacer(Modifier.width(8.dp))
                    Text("Por encima del promedio", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).background(Color(0xFF1A237E), CircleShape)) // Azul
                    Spacer(Modifier.width(8.dp))
                    Text("Por debajo del promedio", style = MaterialTheme.typography.bodySmall)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PgeGreenButton)
                ) {
                    Text(
                        text = "Promedio: 179,646 kWh/mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
/*
@Composable
fun PatronesConsumoMensualCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Patrones de Consumo Mensual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // --- PLACEHOLDER PARA EL HEATMAP ---
            // Esta vista es un "heatmap" o tabla.
            // Se puede construir con LazyRow y Columns, o un Layout personalizado.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GrayCard.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Placeholder: Heatmap de Consumo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun IntensidadConsumoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Intensidad de consumo:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Leyenda de colores
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Bajo", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ColorBox(Color(0xFFC8E6C9)) // Verde claro
                    ColorBox(Color(0xFF81C784)) // Verde
                    ColorBox(Color(0xFFFFE0B2)) // Naranja claro
                    ColorBox(Color(0xFFFFAB91)) // Naranja/Rojo
                    ColorBox(Color(0xFFE57373)) // Rojo
                }
                Text("Alto", style = MaterialTheme.typography.bodySmall)
            }

            // Tarjeta de patrón identificado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = GrayCard.copy(alpha = 0.5f)
                )
            ) {
                Column(Modifier.padding(12.dp)
                    ) {
                    Text(
                        text = "Patrón identificado:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Se observa un incremento en el consumo durante los meses de verano (Jun-Ago), probablemente debido al mayor uso de aire acondicionado.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}*/

// Pequeño Composable de ayuda para los cuadros de color
@Composable
fun ColorBox(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color, RoundedCornerShape(4.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(4.dp)
            )
    )
}



