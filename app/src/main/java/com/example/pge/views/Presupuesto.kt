package com.example.pge.views

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


data class Presupuesto(
    val dependencia: String,
    val anio: String,
    val trimestre: String,
    val monto: Double
)


@Composable
fun PresupuestoScreen(navController: NavController) {


    // Datos de ejemplo
    val presupuestos = listOf(
        Presupuesto("Secretaría de Finanzas", "2025", "Q4 (Oct-Dic)", 3000000.0),
        Presupuesto("Secretaría de Educación", "2025", "Q4 (Oct-Dic)", 8500000.0),
        Presupuesto("Secretaría de Salud", "2025", "Q4 (Oct-Dic)", 5200000.0),
        Presupuesto("Seguridad Pública", "2025", "Q4 (Oct-Dic)", 4100000.0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título y Botón
        item {
            TitleAndButtonRow()
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
fun TitleAndButtonRow() {
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
            onClick = { /* Acción para asignar presupuesto */ }
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Asignar")
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





@Preview(showBackground = true)
@Composable
fun PresupuestoScreenPreview() {
    MaterialTheme {

        val navController = rememberNavController()

        PresupuestoScreen(navController)
    }
}