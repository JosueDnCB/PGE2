package com.example.pge.views.PublicDashboardComponents

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.pge.models.PublicDashboard.RespuestaComparativa
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun BudgetVsExpenseCard(
    data: RespuestaComparativa?,
    modifier: Modifier = Modifier
) {
    // Validamos que haya datos
    if (data == null || data.series.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.titulo, // "Presupuesto vs Gasto Energético 2023"
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Área de la Gráfica
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                BudgetLineChart(data)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda Dinámica (Usa los colores y nombres del JSON)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                data.series.forEachIndexed { index, serie ->
                    val color = parseHexColor(serie.color) ?: Color.Gray

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = serie.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    // Espacio entre items de la leyenda (menos el último)
                    if (index < data.series.size - 1) {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetLineChart(data: RespuestaComparativa) {
    // 1. Calcular Escala Y (Máximo valor de TODAS las series)
    // Combinamos todos los datos para encontrar el pico más alto (el presupuesto o el gasto)
    val allValues = data.series.flatMap { it.datos }
    val maxY = (allValues.maxOrNull()?.toFloat() ?: 100f) * 1.1f // 10% de margen arriba

    // Estados de interacción
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Formateadores
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply { maximumFractionDigits = 0 }

    // Configuración Paint
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = density.run { 10.sp.toPx() }
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 45.dp, end = 20.dp, bottom = 30.dp) // Margen para ejes
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Detectar cuál trimestre se tocó
                        val stepX = size.width / (data.ejeX.size - 1)
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, data.ejeX.size - 1)
                        selectedIndex = index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (data.ejeX.size - 1)
            val stepY = height / 4

            // --- 1. DIBUJAR EJE Y (MONTO $) ---
            for (i in 0..4) {
                val y = height - (i * stepY)
                val fraction = i / 4f
                val value = maxY * fraction

                // Línea guía
                drawLine(
                    color = Color(0xFFEEEEEE),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                // Texto (ej. $500k)
                val labelVal = if (value >= 1000000) {
                    "$${String.format("%.1f", value / 1000000)}M" // Millones
                } else if (value >= 1000) {
                    "$${(value / 1000).toInt()}k" // Miles
                } else {
                    currencyFormat.format(value)
                }

                drawContext.canvas.nativeCanvas.drawText(
                    labelVal,
                    -15f,
                    y + 10f,
                    textPaint
                )
            }

            // --- 2. DIBUJAR LÍNEAS DE PRESUPUESTO Y GASTO ---
            data.series.forEach { serie ->
                val path = Path()
                val color = parseHexColor(serie.color) ?: Color.Blue

                serie.datos.forEachIndexed { i, valorDouble ->
                    val x = i * stepX
                    val y = height - (valorDouble.toFloat() / maxY * height)

                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                    // Puntos
                    drawCircle(color, radius = 8f, center = Offset(x, y))
                    drawCircle(Color.White, radius = 4f, center = Offset(x, y))
                }

                drawPath(path, color, style = Stroke(width = 6f))
            }

            // --- 3. EJE X (TRIMESTRES) ---
            data.ejeX.forEachIndexed { i, label ->
                val x = i * stepX
                textPaint.textAlign = Paint.Align.CENTER

                // Si el texto es muy largo "Trimestre 1", lo acortamos a "T1" si falta espacio
                val labelCorto = if(width < 600 && label.contains("Trimestre")) label.replace("Trimestre ", "T") else label

                drawContext.canvas.nativeCanvas.drawText(
                    labelCorto,
                    x,
                    height + 40f,
                    textPaint
                )
            }

            // --- 4. SELECCIÓN ---
            selectedIndex?.let { index ->
                val x = index * stepX
                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f
                )
            }
        }

        // --- 5. TOOLTIP ---
        selectedIndex?.let { index ->
            BudgetTooltip(
                trimestre = data.ejeX.getOrElse(index) { "" },
                seriesData = data.series.map {
                    Triple(it.nombre, it.datos.getOrElse(index) { 0.0 }, parseHexColor(it.color) ?: Color.Black)
                },
                currencyFormat = currencyFormat,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun BudgetTooltip(
    trimestre: String,
    seriesData: List<Triple<String, Double, Color>>,
    currencyFormat: NumberFormat,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = trimestre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            seriesData.forEach { (nombre, valor, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$nombre: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = currencyFormat.format(valor),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

// Función auxiliar para convertir HEX string ("#28a745") a Color de Compose
fun parseHexColor(hex: String?): Color? {
    if (hex.isNullOrEmpty()) return null
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        null // Si falla el parseo (ej. formato inválido), devolvemos null y usaremos un default
    }
}