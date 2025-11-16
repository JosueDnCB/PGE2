package com.example.pge.views

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pge.ui.theme.DarkText
import com.example.pge.ui.theme.PgeButtonText
import com.example.pge.ui.theme.PgeGreenButton
import com.example.pge.ui.theme.Purple


// Define tus colores aquí para que el Composable los conozca


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PgeTopAppBar(
    isLoggedIn: Boolean,
    titulo: String? = "",
    // Parámetros para el email y la inicial del usuario
    userEmail: String? = "josue_dan7@outlook.com",
    userInitial: String? = "J",
    onShowLoginClick: () -> Unit
) {


    //  Definir el TÍTULO dinámicamente
    val titleComposable: @Composable () -> Unit = {
        if (isLoggedIn) {
            // Estado "Logged In"
            Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(

                text = titulo?:"PGE-QROO",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkText
            )}
        } else {
            // Estado "Logged Out": Muestra el título original
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "PGE-QROO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkText
                )
            }
        }
    }

    //  Definir el ÍCONO DE NAVEGACIÓN dinámicamente
    val navIconComposable: @Composable () -> Unit = {
        if (!isLoggedIn) {
            // Solo muestra el logo si NO está logueado
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Logo PGE",
                tint = PgeButtonText,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(28.dp)
                    .background(PgeButtonText.copy(alpha = 0.2f), CircleShape)
                    .clip(CircleShape)
                    .background(PgeGreenButton)
                    .padding(4.dp)
            )
        }
        // Si está logueado, no se muestra nada
    }

    // Definir las ACCIONES dinámicamente
    val actionsComposable: @Composable RowScope.() -> Unit = {
        if (isLoggedIn) {
            //  Estado "Logged In"
            Column(
                // horizontalAlignment = Alignment.End alinea todo a la derecha
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Spacer(Modifier.width(8.dp))

                // Etiqueta "Admin" o tipo de usuario
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFEEF2FF) // Fondo gris/azul pálido
                ) {
                    Text(
                        text = "Admin",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Purple, // Texto azul/morado
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))

                // Avatar con la inicial
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PgeGreenButton), // Color verde de tu app
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInitial ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
             }
                // Espacio vertical entre la fila de arriba y el correo
                Spacer(Modifier.height(4.dp))

                // Texto de Email (abajo)
                Text(
                    text = userEmail ?: "Correo del usuario",
                    fontSize = 12.sp,
                    color = DarkText
                )
            }
        } else {
            // Estado "Logged Out"
            Button(
                onClick = {
                    onShowLoginClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PgeGreenButton),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text("Acceso servidores públicos",
                    fontSize = 12.sp,
                    color = PgeButtonText)
            }
        }
    }
    // Modal de inicio de sesión


    // Construir el TopAppBar
    TopAppBar(
        title = titleComposable,
        navigationIcon = navIconComposable,
        actions = actionsComposable,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        // Modificador para la línea inferior
        modifier = Modifier.drawBehind {
            val strokeWidth = 1.dp.toPx()
            val color = Color.LightGray
            drawLine(
                color = color,
                start = Offset(0f, size.height), // Esquina inferior izquierda
                end = Offset(size.width, size.height), // Esquina inferior derecha
                strokeWidth = strokeWidth
            )
        }
    )
}