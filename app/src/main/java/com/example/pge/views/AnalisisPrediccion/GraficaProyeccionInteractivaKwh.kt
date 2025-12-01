package com.example.pge.views.AnalisisPrediccion

import androidx.compose.foundation.Canvas
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
import com.example.pge.models.analisisprediccion.DatoGrafica
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun GraficaProyeccionInteractivaKwh(
    datos: List<DatoGrafica>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) return

    val colorReal = Color(0xFF1565C0) // Azul
    val colorPrediccion = Color(0xFF2E7D32) // Verde
    val colorGrid = Color(0xFFEEEEEE)
    val colorPunto = Color.White

    var puntoSeleccionado by remember { mutableStateOf<DatoGrafica?>(null) }

    // Escala Y basada en kWh (No en costo)
    // Usamos coerceAtLeast para evitar divisiones por cero si los datos vienen vacíos o en 0
    val maxValor = datos.maxOf { it.totalKwh }.toFloat().coerceAtLeast(1f) * 1.15f

    val density = LocalDensity.current
    val textPaint = remember(density) {
        android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = density.run { 10.sp.toPx() }
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
    }

    Box(modifier = modifier.fillMaxWidth().height(320.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 40.dp, start = 10.dp, end = 10.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val divisor = (datos.size - 1).coerceAtLeast(1)
                        val stepX = size.width / divisor
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, datos.size - 1)
                        puntoSeleccionado = datos[index]
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val stepX = if (datos.size > 1) width / (datos.size - 1) else width / 2

            // Función Helper para kWh
            fun getY(valorKwh: Double): Float {
                return height - ((valorKwh.toFloat() / maxValor) * height)
            }

            //  GRID
            for (i in 0..4) {
                val y = height * (i / 4f)
                drawLine(
                    color = colorGrid,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // (NOTA: Omitimos dibujar el Área de Rango aquí porque el JSON
            // solo trae rangos de COSTO ($), no de KWH. Graficar pesos sobre kwh sería incorrecto).

            // LÍNEAS PRINCIPALES
            val pathReal = Path()
            val pathPred = Path()
            var lastPoint: Offset? = null

            datos.forEachIndexed { i, d ->
                val x = if (datos.size > 1) i * stepX else stepX
                val y = getY(d.totalKwh) // <--- USAMOS KWH
                val currentPoint = Offset(x, y)

                if (i == 0) {
                    if (d.tipo == "real") pathReal.moveTo(x, y) else pathPred.moveTo(x, y)
                    lastPoint = currentPoint
                } else {
                    val prevPoint = lastPoint!!
                    val controlX1 = (prevPoint.x + currentPoint.x) / 2
                    val controlY1 = prevPoint.y
                    val controlX2 = (prevPoint.x + currentPoint.x) / 2
                    val controlY2 = currentPoint.y

                    if (d.tipo == "real") {
                        if(pathReal.isEmpty) pathReal.moveTo(prevPoint.x, prevPoint.y)
                        pathReal.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    } else {
                        // Conexión suave entre Real y Predicción
                        if (pathPred.isEmpty) pathPred.moveTo(prevPoint.x, prevPoint.y)
                        pathPred.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    }
                    lastPoint = currentPoint
                }
            }

            // Dibujar Path Real (Sólido Azul)
            drawPath(pathReal, color = colorReal, style = Stroke(width = 5f, cap = StrokeCap.Round))

            // Dibujar Path Predicción (Punteado Verde)
            drawPath(
                pathPred,
                color = colorPrediccion,
                style = Stroke(width = 5f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
            )

            // PUNTOS Y ETIQUETAS
            datos.forEachIndexed { i, d ->
                val x = if (datos.size > 1) i * stepX else stepX
                val y = getY(d.totalKwh)

                val color = if(d.tipo == "real") colorReal else colorPrediccion

                drawCircle(color, radius = 6f, center = Offset(x, y))
                drawCircle(colorPunto, radius = 3f, center = Offset(x, y))

                // Etiquetas Mes
                if (datos.size < 6 || i == 0 || i == datos.lastIndex || i % 4 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        d.obtenerEtiquetaFecha(),
                        x,
                        height + 40f,
                        textPaint
                    )
                }
            }

            // SELECCIÓN
            puntoSeleccionado?.let { sel ->
                val index = datos.indexOf(sel)
                val x = if (datos.size > 1) index * stepX else stepX

                drawLine(
                    color = Color.Gray,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )
            }
        }

        // TOOLTIP
        puntoSeleccionado?.let { dato ->
            TooltipProyeccionKwh(dato, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun TooltipProyeccionKwh(dato: DatoGrafica, modifier: Modifier) {
    // Formateador Numérico (No moneda)
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "MX")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dato.obtenerEtiquetaFecha(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)

            val color = if(dato.tipo=="prediccion") Color(0xFF2E7D32) else Color(0xFF1565C0)
            val label = if(dato.tipo=="prediccion") "Estimado" else "Real"

            // Muestra kWh en lugar de dinero
            Text(
                text = "$label: ${numberFormat.format(dato.totalKwh)} kWh",
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}