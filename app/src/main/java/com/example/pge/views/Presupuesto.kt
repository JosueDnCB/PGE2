package com.example.pge.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pge.models.Presupuesto.Presupuesto
import com.example.pge.models.UserResponse
import com.example.pge.ui.theme.GrayTableTop
import com.example.pge.ui.theme.PgeGreenButton
import com.example.pge.viewmodels.LoginViewModel
import com.example.pge.viewmodels.PresupuestoViewModel
import java.text.NumberFormat
import java.util.Locale

// Reutiliza tu componente DropdownFiltro aquí si lo tienes en otro archivo,
// si no, usa el ExposedDropdownMenuBox estándar como en el Dialog.

@Composable
fun PresupuestoScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    usuario: UserResponse?,
    isLoggedIn: Boolean,
    onLoginSuccess: () -> Unit
) {
    // Instanciar el ViewModel
    val viewModel: PresupuestoViewModel = viewModel()

    // Observar estados del ViewModel
    val listaDependencias by viewModel.dependencias.collectAsState()
    val listaPresupuestos by viewModel.presupuestosFiltrados.collectAsState() // Ya filtrada

    // Variables locales para el Dialog
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NewBudgetDialog(
            // Pasamos la dependencia actual para pre-llenar el diálogo si quieres
            initialDependency = viewModel.dependenciaSeleccionada?.nombre ?: "",
            onDismissRequest = { showDialog = false },
            onSaveBudget = { _, year, quarter, amount ->
                // Llamamos al ViewModel para guardar en la API
                viewModel.crearPresupuesto(year, quarter, amount) {
                    showDialog = false // Cerrar solo si fue exitoso
                }
            }
        )
    }

    Scaffold(
        topBar = {
            PgeTopAppBar(
                isLoggedIn = isLoggedIn,
                titulo = "Presupuesto",
                usuarios = usuario,
                onShowLoginClick = { onLoginSuccess() }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // SECCIÓN DE FILTROS SUPERIORES
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Filtros de Presupuesto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Selector de Dependencia (Solo si hay > 1)
                        if (listaDependencias.size > 1) {
                            FiltroDropdownGenerico(
                                label = "Dependencia",
                                options = listaDependencias.map { it.nombre },
                                selectedOption = viewModel.dependenciaSeleccionada?.nombre ?: "",
                                onOptionSelected = { nombre ->
                                    val dep = listaDependencias.find { it.nombre == nombre }
                                    if (dep != null) viewModel.seleccionarDependencia(dep)
                                }
                            )
                        } else if (listaDependencias.size == 1) {
                            // Solo mostramos el nombre como texto informativo
                            Text(
                                text = "Dependencia: ${listaDependencias[0].nombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PgeGreenButton
                            )
                        }

                        //  Selector de Año
                        FiltroDropdownGenerico(
                            label = "Año Fiscal",
                            options = viewModel.aniosDisponibles,
                            selectedOption = viewModel.anioSeleccionado,
                            onOptionSelected = { viewModel.cambiarAnio(it) }
                        )
                    }
                }
            }

            // Título y Botón "Asignar"
            item {
                TitleAndButtonRow(
                    onAsignarClick = { showDialog = true }
                )
            }

            // Tarjetas de Resumen (Conectadas al ViewModel)
            item {
                HeaderSection(
                    total = viewModel.totalPresupuesto,
                    count = listaPresupuestos.size, // Trimestres registrados
                    promedio = viewModel.promedioPresupuesto,
                    anio = viewModel.anioSeleccionado
                )
            }

            // Lista de Presupuestos (Conectada al ViewModel)
            item {
                ListaPresupuestosCard(
                    presupuestos = listaPresupuestos,
                    nombreDependencia = viewModel.dependenciaSeleccionada?.nombre ?: ""
                )
            }
        }
    }
}

// COMPONENTES AUXILIARES ACTUALIZADOS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroDropdownGenerico(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(total: Double, count: Int, promedio: Double, anio: String) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply { maximumFractionDigits = 0 }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total
        SummaryCard(
            title = "Total Asignado $anio",
            value = currencyFormat.format(total),
            valueStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        // Trimestres
        SummaryCard(
            title = "Trimestres",
            value = "$count / 4",
            valueStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(0.7f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        SummaryCard(
            title = "Promedio por Trimestre",
            value = currencyFormat.format(promedio),
            valueStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ListaPresupuestosCard(presupuestos: List<Presupuesto>, nombreDependencia: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text(
                text = "Desglose por Trimestre",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            if (presupuestos.isEmpty()) {
                Text(
                    text = "No hay presupuestos asignados para este año.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.White)
                ) {
                    PresupuestoListHeader()
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                    presupuestos.forEach { item ->
                        PresupuestoItemRow(item, nombreDependencia)
                    }
                }
            }
        }
    }
}

@Composable
fun PresupuestoItemRow(item: Presupuesto, nombreDependencia: String) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dependencia
        Text(nombreDependencia, Modifier.width(200.dp), style = MaterialTheme.typography.bodyMedium)

        // Año
        Text(item.anio.toString(), Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)

        // Trimestre (Chip)


        Box(modifier = Modifier.width(120.dp)) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = "Q${item.trimestre}",
                        color = Color.Blue
                    )
                },
                // Usar BorderStroke directamente
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Blue.copy(alpha = 0.2f)
                ),

                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color.Transparent
                )
            )
        }

        // Monto
        Text(
            text = format.format(item.monto),
            modifier = Modifier.width(150.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32) // Verde dinero
        )
    }
    Divider(color = Color.LightGray.copy(alpha = 0.5f))


}


// Título y Botón "Asignar"
@Composable
fun TitleAndButtonRow(

    onAsignarClick: () -> Unit // PASO 1: se agrega este parámetro

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Text(

            text = "Presupuestos Asignados",

            style = MaterialTheme.typography.headlineSmall,

            fontWeight = FontWeight.Bold

        )



    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Button(

            onClick = {

            // Llama a la función del parámetro

                onAsignarClick()

            },

            colors = ButtonDefaults.buttonColors(containerColor = PgeGreenButton)

        ) {

            Icon(

                Icons.Default.Add,

                contentDescription = null,

                modifier = Modifier.size(18.dp)

            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Asignar Presupuesto")



        }

    }

}
@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    // CAMBIO AQUÍ: Quitamos el valor por defecto que causaba el error
    valueStyle: TextStyle? = null
) {
    // CAMBIO AQUÍ: Resolvemos el estilo dentro de la función (donde sí es seguro)
    val finalStyle = valueStyle ?: MaterialTheme.typography.headlineMedium

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = finalStyle, // Usamos la variable resuelta
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Encabezados de la Lista

@Composable
fun PresupuestoListHeader() {

    Row(

        modifier = Modifier

            .fillMaxWidth()

            .background(GrayTableTop) // Fondo blanco o de superficie

            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interno

        verticalAlignment = Alignment.CenterVertically

    ) {

        Text(

            "Dependencia",

            Modifier.width(200.dp),

            style = MaterialTheme.typography.labelSmall,

            color = Color.DarkGray

        )

        Text(

            "Año",

            Modifier.width(100.dp),

            style = MaterialTheme.typography.labelSmall,

            color = Color.DarkGray

        )

        Text(

            "Trimestre",

            modifier = Modifier.width(150.dp),

            style = MaterialTheme.typography.labelSmall,

            color = Color.DarkGray

        )

        Text(

            "Monto Asignado",

            modifier = Modifier.width(150.dp),

            style = MaterialTheme.typography.labelSmall,

            color = Color.DarkGray

        )

        Text(

            "Acciones",

            modifier = Modifier.width(120.dp),

            style = MaterialTheme.typography.labelSmall,

            color = Color.DarkGray,

            textAlign = TextAlign.End

        )

    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBudgetDialog(
    initialDependency: String,
    onDismissRequest: () -> Unit,
    onSaveBudget: (dependency: String, year: String, quarter: String, amount: String) -> Unit
) {
    // Usamos el valor inicial que nos pasa la vista (la dependencia seleccionada)
    var selectedDependency by remember { mutableStateOf(initialDependency) }
    var fiscalYear by remember { mutableStateOf("2025") }
    var quarter by remember { mutableStateOf("Q1 (Ene-Mar)") }
    var assignedAmount by remember { mutableStateOf("") }

    val fiscalYears = listOf("2024", "2025", "2026")
    val quarters = listOf("Q1 (Ene-Mar)", "Q2 (Abr-Jun)", "Q3 (Jul-Sep)", "Q4 (Oct-Dic)")

    var yearExpanded by remember { mutableStateOf(false) }
    var quarterExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text("Asignar Presupuesto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                // Dependencia (Solo lectura en el diálogo para evitar errores)
                OutlinedTextField(
                    value = selectedDependency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dependencia") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false // Deshabilitado porque ya se seleccionó en el filtro
                )

                Spacer(Modifier.height(12.dp))

                // Año
                ExposedDropdownMenuBox(expanded = yearExpanded, onExpandedChange = { yearExpanded = it }) {
                    OutlinedTextField(
                        value = fiscalYear, onValueChange = {}, readOnly = true,
                        label = { Text("Año") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                        fiscalYears.forEach { y ->
                            DropdownMenuItem(text = { Text(y) }, onClick = { fiscalYear = y; yearExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Trimestre
                ExposedDropdownMenuBox(expanded = quarterExpanded, onExpandedChange = { quarterExpanded = it }) {
                    OutlinedTextField(
                        value = quarter, onValueChange = {}, readOnly = true,
                        label = { Text("Trimestre") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = quarterExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = quarterExpanded, onDismissRequest = { quarterExpanded = false }) {
                        quarters.forEach { q ->
                            DropdownMenuItem(text = { Text(q) }, onClick = { quarter = q; quarterExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Monto
                OutlinedTextField(
                    value = assignedAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) assignedAmount = it },
                    label = { Text("Monto (MXN)") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$") }
                )

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismissRequest) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSaveBudget(selectedDependency, fiscalYear, quarter, assignedAmount) },
                        colors = ButtonDefaults.buttonColors(containerColor = PgeGreenButton),
                        enabled = assignedAmount.isNotEmpty()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}