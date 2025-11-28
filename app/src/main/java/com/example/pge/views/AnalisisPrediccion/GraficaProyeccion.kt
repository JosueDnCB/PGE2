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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pge.models.analisisprediccion.DatoGrafica
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.nativeCanvas


@Composable
fun GraficaProyeccionInteractiva(
    datos: List<DatoGrafica>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) return

    // Colores basados en la imagen de referencia
    val colorReal = Color(0xFF1565C0) // Azul sólido
    val colorPrediccion = Color(0xFF2E7D32) // Verde oscuro
    val colorRango = Color(0xFFE8F5E9) // Verde muy claro para el área
    val colorGrid = Color(0xFFEEEEEE)

    var puntoSeleccionado by remember { mutableStateOf<DatoGrafica?>(null) }

    // Escala Y (Max Value)
    val maxValor = datos.maxOf {
        val maxRango = it.rangoMax ?: 0.0
        maxOf(it.totalCosto, maxRango)
    }.toFloat() * 1.15f // 15% margen arriba

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
                        val stepX = size.width / (datos.size - 1)
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, datos.size - 1)
                        puntoSeleccionado = datos[index]
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (datos.size - 1)

            fun getY(valor: Double): Float {
                return height - ((valor.toFloat() / maxValor) * height)
            }

            // Líneas de fondo
            for (i in 0..4) {
                val y = height * (i / 4f)
                drawLine(
                    color = colorGrid,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // ÁREA DE CONFIANZA
            val pathRango = Path()
            val predicciones = datos.filter { it.tipo == "prediccion" }

            if (predicciones.isNotEmpty()) {
                predicciones.forEachIndexed { idx, d ->
                    val realIndex = datos.indexOf(d)
                    val x = realIndex * stepX
                    // Si es el primero, intentamos conectar visualmente con el último real
                    if (idx == 0 && realIndex > 0) {
                        val prevX = (realIndex - 1) * stepX
                        // Usamos el valor real anterior como punto de partida aproximado
                        val prevY = getY(datos[realIndex - 1].totalCosto)
                        pathRango.moveTo(prevX, prevY)
                        pathRango.lineTo(x, getY(d.rangoMax ?: d.totalCosto))
                    } else if (idx == 0) {
                        pathRango.moveTo(x, getY(d.rangoMax ?: d.totalCosto))
                    } else {
                        // Curva suave hacia el siguiente punto máximo
                        val prevD = predicciones[idx - 1]
                        val prevRealIndex = datos.indexOf(prevD)
                        val prevX = prevRealIndex * stepX
                        val prevY = getY(prevD.rangoMax ?: prevD.totalCosto)

                        val curY = getY(d.rangoMax ?: d.totalCosto)
                        // Bezier simple
                        pathRango.cubicTo(
                            (prevX + x) / 2, prevY,
                            (prevX + x) / 2, curY,
                            x, curY
                        )
                    }
                }
                // Regreso por el mínimo
                for (i in predicciones.indices.reversed()) {
                    val d = predicciones[i]
                    val realIndex = datos.indexOf(d)
                    val x = realIndex * stepX
                    val yMin = getY(d.rangoMin ?: d.totalCosto)

                    if (i == predicciones.lastIndex) {
                        pathRango.lineTo(x, yMin)
                    } else {
                        val prevD = predicciones[i + 1] // El punto anterior en la reversa es el siguiente en la lista original
                        val prevRealIndex = datos.indexOf(prevD)
                        val prevX = prevRealIndex * stepX
                        val prevY = getY(prevD.rangoMin ?: prevD.totalCosto)

                        pathRango.cubicTo(
                            (prevX + x) / 2, prevY,
                            (prevX + x) / 2, yMin,
                            x, yMin
                        )
                    }
                }
                pathRango.close()
                drawPath(pathRango, colorRango)
            }

            // LÍNEAS PRINCIPALES
            val pathReal = Path()
            val pathPred = Path()
            var lastPoint: Offset? = null

            datos.forEachIndexed { i, d ->
                val x = i * stepX
                val y = getY(d.totalCosto)
                val currentPoint = Offset(x, y)

                if (i == 0) {
                    pathReal.moveTo(x, y)
                    lastPoint = currentPoint
                } else {
                    val prevPoint = lastPoint!!
                    // Punto de control para curva Bezier (mitad del camino)
                    val controlX1 = (prevPoint.x + currentPoint.x) / 2
                    val controlY1 = prevPoint.y
                    val controlX2 = (prevPoint.x + currentPoint.x) / 2
                    val controlY2 = currentPoint.y

                    if (d.tipo == "real") {
                        pathReal.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    } else {
                        // Si es el primer punto de predicción, nos movemos al último punto real
                        if (pathPred.isEmpty) {
                            pathPred.moveTo(prevPoint.x, prevPoint.y)
                        }
                        pathPred.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    }
                    lastPoint = currentPoint
                }
            }

            // Dibujar Path Real
            drawPath(
                pathReal,
                color = colorReal,
                style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Dibujar Path Predicción (Punteado)
            drawPath(
                pathPred,
                color = colorPrediccion,
                style = Stroke(
                    width = 5f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // PUNTOS Y ETIQUETAS
            datos.forEachIndexed { i, d ->
                val x = i * stepX
                val y = getY(d.totalCosto)

                // Puntos solo en cambios importantes o espaciados
                val color = if(d.tipo == "real") colorReal else colorPrediccion

                // Dibujamos puntos sólidos con borde blanco
                drawCircle(color, radius = 6f, center = Offset(x, y))
                drawCircle(Color.White, radius = 3f, center = Offset(x, y))

                // Etiquetas Mes
                if (i == 0 || i == datos.lastIndex || i % 4 == 0) {
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
                val x = index * stepX
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
            TooltipProyeccion(dato, Modifier.align(Alignment.TopCenter))
        }
    }
}

// Tooltip auxiliar
@Composable
fun TooltipProyeccion(dato: DatoGrafica, modifier: Modifier) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply { maximumFractionDigits = 0 }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dato.obtenerEtiquetaFecha(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)

            val color = if(dato.tipo=="prediccion") Color(0xFF2E7D32) else Color(0xFF1A237E)
            val label = if(dato.tipo=="prediccion") "Estimado" else "Real"

            Text("$label: ${currencyFormat.format(dato.totalCosto)}", color = color, fontWeight = FontWeight.Bold)

            if(dato.rangoMin != null) {
                Text(
                    "Rango: ${currencyFormat.format(dato.rangoMin)} - ${currencyFormat.format(dato.rangoMax)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
// TOOLTIP (Ventana flotante)
@Composable
fun TooltipInfo(dato: DatoGrafica, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dato.obtenerEtiquetaFecha(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            val colorTexto = if(dato.tipo == "prediccion") Color(0xFF2E7D32) else Color(0xFF1A237E)
            val etiqueta = if(dato.tipo == "prediccion") "Predicción" else "Gasto Real"

            Text(
                text = "$etiqueta: $${String.format("%,.0f", dato.totalCosto)}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorTexto,
                fontWeight = FontWeight.Bold
            )

            if (dato.tipo == "prediccion" && dato.rangoMin != null && dato.rangoMax != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rango: $${String.format("%,.0f", dato.rangoMin)} - $${String.format("%,.0f", dato.rangoMax)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}