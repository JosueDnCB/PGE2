package com.example.pge.views

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.pge.viewmodels.DependenciasViewModel
import com.example.pge.models.Dependencia
import com.example.pge.models.UserResponse
import com.example.pge.viewmodels.LoginViewModel

@Composable
fun DependenciasScreenConnected(
    navController: NavController,
    loginViewModel: LoginViewModel,
    isLoggedIn: Boolean,
    usuario: UserResponse?,
    onLoginSuccess: () -> Unit
) {
    var showLoginDialog by remember { mutableStateOf(!isLoggedIn) }

    if (showLoginDialog) {
        LoginDialog(
            loginViewModel = loginViewModel,
            onDismissRequest = { showLoginDialog = false },
            onLoginSuccess = {
                showLoginDialog = false
                onLoginSuccess()
            }
        )
    }

    if (isLoggedIn) {
        val context = LocalContext.current.applicationContext as Application
        val viewModel = remember { DependenciasViewModel(context) }
        val dependencias by viewModel.dependencias.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.cargarDependencias()
        }

        DependenciasScreen(
            navController = navController,
            isLoggedIn = isLoggedIn,
            dependencias = dependencias,
            usuario = usuario,
            onLoginSuccess = onLoginSuccess
        )
    }
}

@Composable
fun DependenciasScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    dependencias: List<Dependencia>,
    usuario: UserResponse?,
    onLoginSuccess: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NewDependenciaDialog(
            onDismissRequest = { showDialog = false },
            onSave = { nombre, categoria, edificios ->
                // Aquí podrías llamar a ViewModel para guardar en backend
                println("Nueva dependencia: $nombre, $categoria, $edificios")
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            PgeTopAppBar(
                isLoggedIn = isLoggedIn,
                titulo = "Dependencias",
                usuarios = usuario,
                onShowLoginClick = { }
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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dependencias",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Agregar")
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nombre dependencia", Modifier.weight(0.25f), fontWeight = FontWeight.Bold)
                            Text("Categoría", Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
                            Text("Acciones", Modifier.weight(0.15f), fontWeight = FontWeight.Bold)
                        }

                        Divider(Modifier.padding(vertical = 4.dp))

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
                                    Text(dependencia.nombre, Modifier.weight(0.25f))
                                    Text(dependencia.categoria, Modifier.weight(0.2f))

                                    Row(
                                        modifier = Modifier
                                            .weight(0.15f),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = { }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = expanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF5F5F5))
                                            .padding(8.dp)
                                    ) {
                                        Text("Edificios: ${dependencia.edificios}")
                                    }
                                }

                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

//Modal para agregar dependencias
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDependenciaDialog(
    onDismissRequest: () -> Unit,
    onSave: (nombre: String, categoria: String, edificios: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var edificios by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf(
        "Gubernamental", "Educación", "Salud", "Infraestructura", "Seguridad",
        "Transporte", "Finanzas", "Cultura", "Deporte"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .padding(24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Agregar Dependencia", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider(Modifier.padding(vertical = 12.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categorias.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    categoria = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = edificios,
                    onValueChange = { edificios = it },
                    label = { Text("Número de edificios") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismissRequest, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                        Text("Cancelar")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = { onSave(nombre, categoria, edificios) },
                        enabled = nombre.isNotBlank() && categoria.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}