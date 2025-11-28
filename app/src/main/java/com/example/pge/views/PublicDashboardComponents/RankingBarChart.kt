package com.example.pge.views.PublicDashboardComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.example.pge.models.PublicDashboard.RankingItem // Ajusta tu import
import com.example.pge.models.PublicDashboard.RespuestaComparativa
import java.text.NumberFormat
import java.util.Locale

// Colores
val BarBlue = Color(0xFF1E4E9E)
val BarRed = Color(0xFFC0392B)
val TooltipDark = Color(0xFF2C3E50) // Color oscuro estilo "burbuja"

val TooltipWhite = Color(0xFFFFFFFF) // Color oscuro estilo "burbuja"

@Composable
fun RankingBarChart(
    data: RespuestaComparativa?,
    modifier: Modifier = Modifier
) {
    // Validaciones iniciales
    if (data == null || data.series.isEmpty()) return

    // Transformamos los datos (RespuestaComparativa -> List<RankingItem>) dentro del componente
    val items = remember(data) {
        val valores = data.series.firstOrNull()?.datos ?: emptyList()
        data.ejeX.zip(valores) { nombre, valor ->
            RankingItem(nombre, valor)
        }
    }

    if (items.isEmpty()) return

    val maxValue = remember(items) { items.maxOfOrNull { it.total } ?: 1.0 }
    val averageValue = remember(items) { items.map { it.total }.average() }

    var selectedItemName by remember { mutableStateOf<String?>(null) }

    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TÍTULO DINÁMICO
            Text(
                text = data.titulo, // <--- AQUI YA FUNCIONA
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (selectedItemName == item.nombre) 1f else 0f)
                ) {
                    RankingBarRow(
                        index = index + 1,
                        label = item.nombre,
                        value = item.total,
                        max = maxValue,
                        average = averageValue,
                        onClick = {
                            selectedItemName = if (selectedItemName == item.nombre) null else item.nombre
                        }
                    )

                    androidx.compose.animation.AnimatedVisibility(
                        visible = selectedItemName == item.nombre,
                        enter = fadeIn() + scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-45).dp)
                    ) {
                        RankingTooltipBubble(
                            nombre = item.nombre,
                            valor = item.total,
                            numberFormat = numberFormat
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LeyendaPunto(BarBlue, "Bajo promedio")
                Spacer(modifier = Modifier.width(16.dp))
                LeyendaPunto(BarRed, "Sobre promedio")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Promedio Global: ${numberFormat.format(averageValue)} kWh",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RankingBarRow(
    index: Int,
    label: String,
    value: Double,
    max: Double,
    average: Double,
    onClick: () -> Unit
) {
    val barColor = if (value > average) BarRed else BarBlue
    val fillFraction = (value / max).toFloat()

    // Row clickeable
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 6.dp) // Un poco más de aire vertical
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Sin efecto ripple para que se vea más limpio
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // # Ranking
        Text(
            text = "#$index",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(28.dp)
        )

        // Nombre
        Text(
            text = label,
            modifier = Modifier
                .weight(0.35f)
                .padding(end = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Barra
        Box(
            modifier = Modifier
                .weight(0.65f)
                .height(20.dp), // Barra un poco más delgada para elegancia
            contentAlignment = Alignment.CenterStart
        ) {
            // Fondo barra
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF0F0F0))
            )

            // Barra color
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

// --- NUEVO DISEÑO DE TOOLTIP ESTILO BURBUJA ---
@Composable
fun RankingTooltipBubble(
    nombre: String,
    valor: Double,
    numberFormat: NumberFormat
) {
    Surface(
        color = TooltipWhite,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.widthIn(max = 200.dp) // Ancho máximo para que no ocupe toda la pantalla

    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${numberFormat.format(valor)} kWh",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Blue, // Amarillo para resaltar el dato
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LeyendaPunto(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}