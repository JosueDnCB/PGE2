package com.example.pge.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AnalisisScreen() {
    Text("📊 Vista de Análisis", modifier = Modifier.padding(24.dp))
}

@Composable
fun DependenciaScreen() {
    Text("🏢 Vista de Dependencias", modifier = Modifier.padding(24.dp))
}

@Composable
fun CargaConsumosScreen() {
    Text("⚡ Vista de Carga de Consumos", modifier = Modifier.padding(24.dp))
}

@Composable
fun PresupuestosScreen() {
    Text("💰 Vista de Presupuestos", modifier = Modifier.padding(24.dp))
}

@Composable
fun UsuariosScreen() {
    Text("👥 Vista de Usuarios", modifier = Modifier.padding(24.dp))
}
