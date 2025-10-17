package com.example.pge.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dependencia(
    val nombre: String,
    val categoria: String,
    val numEdificios: Int
)


@Composable
fun DependenciasScreen(dependencias: List<Dependencia>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título
        item {
            Text(
                text = "Dependencias del Estado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Tarjeta con tabla
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        // --- Header de la tabla ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Nombre dependencia",
                                Modifier.weight(0.4f),
                                fontWeight = FontWeight.Bold
                            )
                            Text("Categoría", Modifier.weight(0.25f), fontWeight = FontWeight.Bold)
                            Text(
                                "N° Edificios",
                                Modifier.weight(0.2f),
                                fontWeight = FontWeight.Bold
                            )
                            Text("Acciones", Modifier.weight(0.15f), fontWeight = FontWeight.Bold)
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        // --- Filas de datos ---
                        dependencias.forEach { dependencia ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(dependencia.nombre, Modifier.weight(0.4f))
                                Text(dependencia.categoria, Modifier.weight(0.25f))
                                Text(dependencia.numEdificios.toString(), Modifier.weight(0.2f))
                                Row(
                                    modifier = Modifier
                                        .weight(0.15f)
                                        .padding(start = 4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { /* TODO editar */ }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { /* TODO eliminar */ }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
    }
}



@Preview
@Composable
    fun DependenciasTablePreview() {
        val sampleData = listOf(
            Dependencia("Secretaría de Finanzas", "Administrativa", 8),
            Dependencia("Secretaría de Educación", "Educativa", 12),
            Dependencia("Secretaría de Salud", "Salud", 6),
            Dependencia("Secretaría de Infraestructura", "Obras", 10)
        )
        MaterialTheme {
            DependenciasScreen(sampleData)
        }
    }
