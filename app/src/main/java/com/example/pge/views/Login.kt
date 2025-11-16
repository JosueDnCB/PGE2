package com.example.pge.views

// Importa lo que necesites
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pge.ui.theme.PgeGreenButton


val PgeLoginButtonGreen = Color(0xFFA0CBBF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDialog(
    onDismissRequest: () -> Unit,
    onLoginClick: (email: String, pass: String) -> Unit
) {
    // Estado interno para guardar lo que escribe el usuario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp), // Más padding para que se vea espaciado
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //  Título y Botón de cerrar ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acceso para servidores públicos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        // El modifier.weight(1f) asegura que el texto se ajuste
                        // si es muy largo, sin empujar al ícono
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                // subtítulo
                Text(
                    text = "Inicia sesión con tu correo institucional.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Campos del Formulario
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Campo de Correo
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                // Botón "Entrar"
                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), // Un espacio extra arriba
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                       // containerColor = PgeLoginButtonGreen,
                        containerColor = PgeGreenButton,
                        contentColor = Color.White // Tu imagen muestra texto blanco
                    )
                ) {
                    Text(
                        text = "Entrar",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}