package com.example.pge.views.Dashboard

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pge.models.EvolucionItem
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


val ChartBlue = Color(0xFF2E5BFF) // Azul (Consumo)
val ChartGreen = Color(0xFF4CAF50) // Verde (Costo)
val GridColor = Color(0xFFEEEEEE)

@Composable
fun EvolutionChartCard(
    dataPoints: List<EvolucionItem>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp), // Un poco más de altura para que respire
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Evolución Consumo y Costo - 12 Meses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gráfica
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DualAxisLineChart(data = dataPoints)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LeyendaItem(color = ChartBlue, text = "Consumo (kWh)")
                Spacer(modifier = Modifier.width(24.dp))
                LeyendaItem(color = ChartGreen, text = "Costo ($)")
            }
        }
    }
}

@Composable
fun LeyendaItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun DualAxisLineChart(data: List<EvolucionItem>) {
    // Cálculos de Escalas (Maximos)
    // Usamos toFloat() porque Canvas dibuja con Floats, pero tu modelo tiene Doubles.
    val maxConsumo = (data.maxOfOrNull { it.total_consumo }?.toFloat() ?: 100f) * 1.1f
    val maxCosto = (data.maxOfOrNull { it.total_costo }?.toFloat() ?: 100f) * 1.1f

    // Estado para interacción
    var selectedItem by remember { mutableStateOf<EvolucionItem?>(null) }

    // Formateadores
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply { maximumFractionDigits = 0 }
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX"))

    // Configuración de Paint para texto nativo
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            textSize = density.run { 10.sp.toPx() }
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, end = 40.dp, bottom = 20.dp) // Padding lateral para textos de ejes
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (data.size - 1)
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, data.size - 1)
                        selectedItem = data[index]
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (data.size - 1)
            val stepY = height / 4 // 4 divisiones

            // GRILLA Y EJES
            for (i in 0..4) {
                val y = height - (i * stepY)
                val fraction = i / 4f

                // Línea punteada
                drawLine(
                    color = GridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                // Texto Eje Izquierdo (Consumo)
                val valConsumo = maxConsumo * fraction
                val labelConsumo = "${(valConsumo / 1000).toInt()}k"
                textPaint.textAlign = Paint.Align.RIGHT
                textPaint.color = android.graphics.Color.parseColor("#2E5BFF")
                drawContext.canvas.nativeCanvas.drawText(labelConsumo, -15f, y + 10f, textPaint)

                // Texto Eje Derecho (Costo)
                val valCosto = maxCosto * fraction
                val labelCosto = "$${(valCosto / 1000).toInt()}k"
                textPaint.textAlign = Paint.Align.LEFT
                textPaint.color = android.graphics.Color.parseColor("#4CAF50")
                drawContext.canvas.nativeCanvas.drawText(labelCosto, width + 15f, y + 10f, textPaint)
            }

            // Líneas
            val pathConsumo = Path()
            val pathCosto = Path()

            data.forEachIndexed { i, item ->
                val x = i * stepX
                // Convertimos Double a Float para dibujar
                val yConsumo = height - (item.total_consumo.toFloat() / maxConsumo * height)
                val yCosto = height - (item.total_costo.toFloat() / maxCosto * height)

                if (i == 0) {
                    pathConsumo.moveTo(x, yConsumo)
                    pathCosto.moveTo(x, yCosto)
                } else {
                    pathConsumo.lineTo(x, yConsumo)
                    pathCosto.lineTo(x, yCosto)
                }
            }

            // Dibujar Líneas
            drawPath(pathConsumo, ChartBlue, style = Stroke(width = 6f))
            drawPath(pathCosto, ChartGreen, style = Stroke(width = 6f))

            // PUNTOS Y EJE X (Meses)
            data.forEachIndexed { i, item ->
                val x = i * stepX
                val yConsumo = height - (item.total_consumo.toFloat() / maxConsumo * height)
                val yCosto = height - (item.total_costo.toFloat() / maxCosto * height)

                // Puntos
                drawCircle(ChartBlue, radius = 9f, center = Offset(x, yConsumo))
                drawCircle(Color.White, radius = 5f, center = Offset(x, yConsumo))

                drawCircle(ChartGreen, radius = 9f, center = Offset(x, yCosto))
                drawCircle(Color.White, radius = 5f, center = Offset(x, yCosto))

                // Texto Meses (Abajo)
                val mesNombre = obtenerNombreMesCorto(item.mes)
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.color = android.graphics.Color.GRAY
                drawContext.canvas.nativeCanvas.drawText(mesNombre, x, height + 40f, textPaint)
            }

            // SELECCIÓN
            selectedItem?.let { selected ->
                val index = data.indexOf(selected)
                val x = index * stepX

                // Línea vertical
                drawLine(
                    color = Color.Black.copy(alpha = 0.3f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f
                )

                // Resaltar Puntos
                val ySelConsumo = height - (selected.total_consumo.toFloat() / maxConsumo * height)
                val ySelCosto = height - (selected.total_costo.toFloat() / maxCosto * height)

                drawCircle(ChartBlue, radius = 14f, center = Offset(x, ySelConsumo))
                drawCircle(ChartGreen, radius = 14f, center = Offset(x, ySelCosto))
            }
        }

        // TOOLTIP
        selectedItem?.let { item ->
            ChartTooltip(
                item = item,
                currencyFormat = currencyFormat,
                numberFormat = numberFormat,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ChartTooltip(
    item: EvolucionItem,
    currencyFormat: NumberFormat,
    numberFormat: NumberFormat,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${obtenerNombreMesLargo(item.mes)} ${item.año}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Consumo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(ChartBlue, RoundedCornerShape(50)))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Consumo: ${numberFormat.format(item.total_consumo)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChartBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            // Costo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(ChartGreen, RoundedCornerShape(50)))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Costo: ${currencyFormat.format(item.total_costo)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChartGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helpers para nombres de meses
fun obtenerNombreMesCorto(mes: Int): String {
    val meses = listOf("", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    return if (mes in 1..12) meses[mes] else ""
}

fun obtenerNombreMesLargo(mes: Int): String {
    val meses = listOf("", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    return if (mes in 1..12) meses[mes] else ""
}