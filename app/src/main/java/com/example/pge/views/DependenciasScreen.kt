package com.example.pge.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Dependencia(
    val nombre: String,
    val categoria: String,
    val numEdificios: Int,
)


@Composable
fun DependenciasScreen(navController: NavController, dependencias: List<Dependencia>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dependencias del Estado",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

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
                            Modifier.weight(0.25f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Categoría",
                            Modifier.weight(0.2f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Acciones",
                            Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // --- Filas de datos ---
                    dependencias.forEach { dependencia ->
                        var expanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(dependencia.nombre, Modifier.weight(0.2f))
                                Text(dependencia.categoria, Modifier.weight(0.2f))
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

                            AnimatedVisibility(
                                visible = expanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5))
                                        .padding(8.dp)
                                ) {
                                    Row {
                                        Text(
                                            "Edificios: ${dependencia.numEdificios}",
                                            Modifier.weight(0.4f)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                    }
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

    }
}
