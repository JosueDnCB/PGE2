package com.example.pge.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.collections.listOf


data class Presupuesto(
    val dependencia: String,
    val anio: String,
    val trimestre: String,
    val monto: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBudgetDialog(
    onDismissRequest: () -> Unit,
    onSaveBudget: (dependency: String, year: String, quarter: String, amount: String) -> Unit
) {
    var selectedDependency by remember { mutableStateOf("Secretaría de Finanzas") }
    var fiscalYear by remember { mutableStateOf("2025") }
    var quarter by remember { mutableStateOf("Q4 (Oct-Dec)") }
    var assignedAmount by remember { mutableStateOf("0.00") }

    val dependencies = listOf("Secretaría de Finanzas", "Secretaría de Educación", "Secretaría de Salud")
    val fiscalYears = listOf("2023", "2024", "2025", "2026")
    val quarters = listOf("Q1 (Jan-Mar)", "Q2 (Apr-Jun)", "Q3 (Jul-Sep)", "Q4 (Oct-Dec)")

    // Se añade 3 variables de estado para controlar cada menú
    var dependencyExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var quarterExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), //desplazamiento horizontal
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .width(320.dp)
            ) {
                // Header del Diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asignar Nuevo Presupuesto",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp)
                    .fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))


                // PRIMER MENÚ (DEPENDENCIAS)
                Text(text = "Seleccionar Dependencia *", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = dependencyExpanded, // Variable remember
                    onExpandedChange = { dependencyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDependency,
                        onValueChange = { },
                        readOnly = true,
                       // label = { Text("Secretaría de Finanzas") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dependencyExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dependencyExpanded, // Variable remember
                        onDismissRequest = { dependencyExpanded = false }
                    ) {
                        dependencies.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedDependency = selectionOption
                                    dependencyExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // SEGUNDO MENÚ (AÑO FISCAL)
                Text(text = "Año Fiscal *", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = yearExpanded, // Variable remember
                    onExpandedChange = { yearExpanded = it }
                ) {
                    OutlinedTextField(
                        value = fiscalYear,
                        onValueChange = { },
                        readOnly = true,
                        // label = { Text("2025") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) }, // <<< CAMBIO AQUÍ
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = yearExpanded,// Variable remember
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        fiscalYears.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    fiscalYear = selectionOption
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                //TERCER MENÚ (TRIMESTRE)
                Text(text = "Trimestre *", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = quarterExpanded, // <<< CAMBIO AQUÍ
                    onExpandedChange = { quarterExpanded = it } // <<< CAMBIO AQUÍ
                ) {
                    OutlinedTextField(
                        value = quarter,
                        onValueChange = { },
                        readOnly = true,
                       // label = { Text("Q4 (Oct-Dec)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = quarterExpanded) }, // <<< CAMBIO AQUÍ
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = quarterExpanded, // Variable remember
                        onDismissRequest = { quarterExpanded = false }
                    ) {
                        quarters.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    quarter = selectionOption
                                    quarterExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // El resto del formulario y botones
                Text(text = "Monto Asignado (MXN) *", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = assignedAmount,
                    onValueChange = { assignedAmount = it },
                  //  label = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Borde de el button
                        modifier = Modifier.weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Cancelar", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSaveBudget(
                                selectedDependency,
                                fiscalYear,
                                quarter,
                                assignedAmount
                            )
                        },
                        shape = RoundedCornerShape(12.dp),// Forma de bordes redondeados
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Guardar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewBudgetDialog() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f)), // Fondo semi-transparente para simular el diálogo
            contentAlignment = Alignment.Center
        ) {

            NewBudgetDialog(

                onDismissRequest = { },
                onSaveBudget = { _, _, _, _ -> }
            )
        }
    }
}



@Composable
fun PresupuestoScreen(navController: NavController) {


    // Datos de ejemplo
    val presupuestos = listOf(
        Presupuesto("Secretaría de Finanzas", "2025", "Q4 (Oct-Dic)", 3000000.0),
        Presupuesto("Secretaría de Educación", "2025", "Q4 (Oct-Dic)", 8500000.0),
        Presupuesto("Secretaría de Salud", "2025", "Q4 (Oct-Dic)", 5200000.0),
        Presupuesto("Seguridad Pública", "2025", "Q4 (Oct-Dic)", 4100000.0)
    )
    // PASO 1: el estado 'showDialog' aquí, al nivel superior.
    var showDialog by remember { mutableStateOf(false) }

    // PASO 2: Llama al diálogo aquí, fuera del LazyColumn.
    // De esta forma, se mostrará sobre toda la pantalla.
    if (showDialog) {
        NewBudgetDialog(
            onDismissRequest = {
                showDialog = false // Cierra el diálogo
            },
            onSaveBudget = { dep, year, q, amount ->
                // ... lógica para guardar ...
                Log.d("PresupuestoScreen", "Guardando: $dep, $year, $q, $amount")
                showDialog = false // Cierra el diálogo al guardar
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título y Botón
        item {
            // PASO 3: Pasa una función lambda al botón para que
            TitleAndButtonRow(
                onAsignarClick = {
                    showDialog = true
                }
            )
        }

        // Tarjetas de Resumen ---
        item {
            HeaderSection()
        }

        // Tarjeta de la Lista ---
        item {
            ListaPresupuestosCard(presupuestos)
        }
    }

}

//  Componente: Título y Botón "Asignar"
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
    ){
        Button(
            onClick = {
                // PASO 3: Llama a la función del parámetro
                onAsignarClick()
            }
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

// Componente: Sección de 3 Tarjetas de Resumen
@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Presupuesto Total Q4 2025",
            value = "$27,100,000",
            valueStyle = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(){

        SummaryCard(
            title = "Dependencias",
            value = "5",
            valueStyle = MaterialTheme.typography.headlineMedium, // Más grande
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(){
        SummaryCard(
            title = "Promedio por Dependencia",
            value = "$5,420,000",
            valueStyle = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )
    }
}

// Componente: Tarjeta de Resumen Reutilizable
@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    Card(modifier = modifier) {
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
                style = valueStyle,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//Componente: Tarjeta de la Lista de Presupuestos ---
@Composable
fun ListaPresupuestosCard(presupuestos: List<Presupuesto>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lista de Presupuestos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Encabezados de la Lista ---
            PresupuestoListHeader()
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Filas de la Lista ---
            // Usamos un Column normal porque ya estamos dentro de un LazyColumn

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    presupuestos.forEach { presupuesto ->
                        PresupuestoItemRow(presupuesto)
                    }

                }

        }
    }
}

// Componente: Encabezados de la Lista ---
@Composable
fun PresupuestoListHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Dependencia", Modifier.weight(3f), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text("Año", Modifier.weight(2f), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text("Trimestre", Modifier.weight(4.5f), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text("Monto Asignado", Modifier.weight(3f), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text("Acciones", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.End)
    }
}

// Componente: Fila de un Item de Presupuesto ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoItemRow(presupuesto: Presupuesto) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()) //desplazamiento horizontal
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(presupuesto.dependencia, Modifier.width(200.dp), style = MaterialTheme.typography.bodyMedium)

        Text(presupuesto.anio, Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)

        Box(modifier = Modifier.width(150.dp)) {
            AssistChip(
                onClick = { },
                label = { Text(presupuesto.trimestre, style = MaterialTheme.typography.labelMedium) }
            )
        }

        Text(
            text = format.format(presupuesto.monto),
            modifier = Modifier.width(150.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /* Acción Editar */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { /* Acción Eliminar */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}




/*
@Preview(showBackground = true)
@Composable
fun PresupuestoScreenPreview() {
    MaterialTheme {

        val navController = rememberNavController()

        PresupuestoScreen(navController)
    }
}*/