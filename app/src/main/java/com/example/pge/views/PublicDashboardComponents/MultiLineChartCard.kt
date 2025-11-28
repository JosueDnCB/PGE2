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
import com.example.pge.models.PublicDashboard.SerieGrafica
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

// Paleta de colores para asignar automáticamente a las series
val ChartColors = listOf(
    Color(0xFF2E5BFF), // Azul
    Color(0xFF00C853), // Verde
    Color(0xFFFFC107), // Ambar
    Color(0xFFFF3D00), // Rojo Naranja
    Color(0xFFE040FB), // Violeta
    Color(0xFF00BCD4)  // Cyan
)

@Composable
fun MultiLineChartCard(
    data: RespuestaComparativa?,
    isCurrency: Boolean = false // Para saber si formatear como dinero ($) o número (kWh)
) {
    if (data == null || data.series.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp), // Altura generosa para la leyenda
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = data.titulo,
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
                MultiLineChart(
                    series = data.series,
                    labelsX = data.ejeX,
                    isCurrency = isCurrency
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda (Nombres de las series)
            FlowRowLeyenda(series = data.series)
        }
    }
}

@Composable
fun FlowRowLeyenda(series: List<SerieGrafica>) {
    // Usamos FlowRow (si tienes Compose actualizado) o un Column simple envuelto
    // Aquí simularemos un FlowRow simple con filas
    Column(modifier = Modifier.fillMaxWidth()) {
        val chunkedSeries = series.chunked(2) // Agrupar de 2 en 2 para la leyenda
        chunkedSeries.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                rowItems.forEachIndexed { colIndex, item ->
                    val globalIndex = (rowIndex * 2) + colIndex
                    val color = ChartColors[globalIndex % ChartColors.size]

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultiLineChart(
    series: List<SerieGrafica>,
    labelsX: List<String>,
    isCurrency: Boolean
) {
    // 1. Calcular Máximo Global (para la escala Y)
    val allValues = series.flatMap { it.datos }
    if (allValues.isEmpty()) return

    val maxY = (allValues.maxOrNull()?.toFloat() ?: 100f) * 1.1f // 10% margen arriba

    // Formateadores
    val locale = Locale("es", "MX")
    val numberFormat = if (isCurrency) NumberFormat.getCurrencyInstance(locale).apply { maximumFractionDigits = 0 }
    else NumberFormat.getNumberInstance(locale)

    // Estados de interacción
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Configuración de Texto (Paint)
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
                .padding(start = 40.dp, end = 10.dp, bottom = 20.dp) // Espacio para eje Y e X
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (labelsX.size - 1)
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, labelsX.size - 1)
                        selectedIndex = index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (labelsX.size - 1)
            val stepY = height / 4

            // --- 1. DIBUJAR GRILLA Y EJE Y ---
            for (i in 0..4) {
                val y = height - (i * stepY)
                val fraction = i / 4f
                val value = maxY * fraction

                // Línea horizontal
                drawLine(
                    color = Color(0xFFEEEEEE),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f
                )

                // Texto del eje Y
                val labelY = if (value >= 1000) {
                    val kValue = value / 1000
                    if (isCurrency) "$${kValue.toInt()}k" else "${kValue.toInt()}k"
                } else {
                    numberFormat.format(value)
                }

                drawContext.canvas.nativeCanvas.drawText(
                    labelY,
                    -15f,
                    y + 10f,
                    textPaint
                )
            }

            // --- 2. DIBUJAR LÍNEAS Y PUNTOS ---
            series.forEachIndexed { serieIndex, serie ->
                val color = ChartColors[serieIndex % ChartColors.size]
                val path = Path()

                serie.datos.forEachIndexed { i, valorDouble ->
                    val valor = valorDouble.toFloat()
                    val x = i * stepX
                    val y = height - (valor / maxY * height)

                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                    // Puntos
                    drawCircle(color, radius = 6f, center = Offset(x, y))
                    // Centro blanco del punto
                    drawCircle(Color.White, radius = 3f, center = Offset(x, y))
                }

                drawPath(path, color, style = Stroke(width = 5f))
            }

            // --- 3. EJE X (ETIQUETAS) ---
            // Dibujamos solo algunas etiquetas si son muchas
            labelsX.forEachIndexed { i, label ->
                // Dibujar si es par o si hay pocos datos, para no encimar
                if (labelsX.size <= 6 || i % 2 == 0) {
                    val x = i * stepX
                    textPaint.textAlign = Paint.Align.CENTER
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        x,
                        height + 40f,
                        textPaint
                    )
                }
            }

            // --- 4. INDICADOR DE SELECCIÓN ---
            selectedIndex?.let { index ->
                val x = index * stepX
                // Línea vertical
                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            }
        }

        // --- 5. TOOLTIP (Ventana flotante) ---
        selectedIndex?.let { index ->
            // Datos del mes seleccionado
            val mes = labelsX.getOrElse(index) { "" }
            val valoresDelMes = series.mapIndexed { i, serie ->
                Triple(serie.nombre, serie.datos.getOrElse(index) { 0.0 }, ChartColors[i % ChartColors.size])
            }

            MultiTooltip(
                mes = mes,
                valores = valoresDelMes,
                numberFormat = numberFormat,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun MultiTooltip(
    mes: String,
    valores: List<Triple<String, Double, Color>>,
    numberFormat: NumberFormat,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(top = 0.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = mes, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            valores.forEach { (nombre, valor, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${nombre.take(15)}..: ${numberFormat.format(valor)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}