package com.example.pge.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AnalisisDashboardScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Título Principal
        item {
            TituloPrincipal()
        }

        // 2. Tarjeta de Filtros
        item {
            FiltrosCard()
        }

        // 3. Tarjeta de Predicción de Gasto
        item {
            PrediccionGastoCard()
        }

        // 4. Tarjeta de Comparativa de Consumo
        item {
            ComparativaConsumoCard()
        }

        // 5. Tarjeta de Patrones de Consumo Mensual
        item {
            PatronesConsumoMensualCard()
        }

        // 6. Tarjeta de Intensidad y Patrón
        item {
            IntensidadConsumoCard()
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
fun FiltrosCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown de Rango de Fechas
            DropdownFiltro(
                label = "Rango de Fechas",
                icono = { Icon(Icons.Default.CalendarMonth, contentDescription = "Rango de Fechas") },
                opciones = listOf("Últimos 12 meses", "Últimos 6 meses", "Último mes")
            )

            // Dropdown de Dependencia
            DropdownFiltro(
                label = "Dependencia",
                opciones = listOf("Secretaría de Finanzas", "Secretaría de Educación", "Secretaría de Salud")
            )

            // Dropdown de Categoría
            DropdownFiltro(
                label = "Categoría",
                opciones = listOf("Todas las Categorías", "Categoría A", "Categoría B")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltro(
    label: String,
    opciones: List<String>,
    icono: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(opciones[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Importante para anclar el menú
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = icono,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun PrediccionGastoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Predicción de Gasto para los Próximos 6 Meses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // --- PLACEHOLDER PARA LA GRÁFICA ---
            // Aquí debes reemplazar este Box con tu componente de gráfica (de Vico, MPAndroidChart, etc.)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Placeholder: Gráfica de Línea",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Leyenda (simplificada)
            Column {
                Text("Interpretación:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    "La predicción muestra una tendencia al alza en el gasto energético. El área sombreada representa el intervalo de confianza del 95% para las proyecciones.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun ComparativaConsumoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Promedio: 179,646 kWh/mes",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PatronesConsumoMensualCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(12.dp)) {
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
}

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

