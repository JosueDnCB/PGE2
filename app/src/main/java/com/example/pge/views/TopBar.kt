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
import com.example.pge.models.UserResponse
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
    usuarios: UserResponse?,             // 👈 recibimos el usuario real
    onShowLoginClick: () -> Unit
) {

    // Extraer email e inicial
    val userEmail = usuarios?.email ?: ""
    val userInitial = usuarios?.nombre?.firstOrNull()?.uppercase() ?: ""

    // Título dinámico
    val titleComposable: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = titulo ?: "PGE-QROO",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkText
            )
        }
    }

    // Ícono navegación si no está logueado
    val navIconComposable: @Composable () -> Unit = {
        if (!isLoggedIn) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Logo PGE",
                tint = PgeButtonText,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PgeGreenButton)
                    .padding(4.dp)
            )
        }
    }

    // Acciones en el lado derecho
    val actionsComposable: @Composable RowScope.() -> Unit = {

        if (isLoggedIn && usuarios != null) {

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Text(
                            text = "Admin",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Purple,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Avatar verde
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PgeGreenButton),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitial,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Email debajo
                Text(
                    text = userEmail,
                    fontSize = 12.sp,
                    color = DarkText
                )

            }

        } else {
            Button(
                onClick = onShowLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = PgeGreenButton),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    "Acceso servidores públicos",
                    fontSize = 12.sp,
                    color = PgeButtonText
                )
            }
        }
    }

    TopAppBar(
        title = titleComposable,
        navigationIcon = navIconComposable,
        actions = actionsComposable,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.drawBehind {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    )
}