package com.example.pge.views.AnalisisPrediccion

import android.graphics.Paint
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

@Composable
fun GraficaProyeccionInteractiva(
    datos: List<DatoGrafica>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) return

    // Colores
    val colorLineaReal = Color(0xFF1A237E) // Azul oscuro
    val colorLineaPred = Color(0xFF2E7D32) // Verde
    val colorRango = Color(0xFF2E7D32).copy(alpha = 0.2f) // Verde clarito transparente
    val colorPunto = Color.White
    val gridColor = Color(0xFFEEEEEE)

    // Estado para el tooltip
    var puntoSeleccionado by remember { mutableStateOf<DatoGrafica?>(null) }

    // Cálculos de Escala
    // Usamos el maximo entre costo real, predicción y el tope del rango de error
    val maxValor = datos.maxOf {
        val maxRango = it.rangoMax ?: 0.0
        maxOf(it.totalCosto, maxRango)
    }.toFloat() * 1.1f

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = density.run { 10.sp.toPx() }
            textAlign = Paint.Align.CENTER
            // Usa la ruta completa:
            // android.graphics.Typeface
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


            // DIBUJAR RANGO (SOMBRA VERDE)

            val pathRango = Path()
            val predicciones = datos.filter { it.tipo == "prediccion" && it.rangoMin != null && it.rangoMax != null }

            if (predicciones.isNotEmpty()) {
                // Lado Superior
                predicciones.forEachIndexed { idx, d ->
                    val realIndex = datos.indexOf(d)
                    val x = realIndex * stepX
                    val yMax = getY(d.rangoMax!!)
                    if (idx == 0) pathRango.moveTo(x, yMax) else pathRango.lineTo(x, yMax)
                }
                // Lado Inferior (Regreso)
                for (i in predicciones.indices.reversed()) {
                    val d = predicciones[i]
                    val realIndex = datos.indexOf(d)
                    val x = realIndex * stepX
                    val yMin = getY(d.rangoMin!!)
                    pathRango.lineTo(x, yMin)
                }
                pathRango.close()
                drawPath(pathRango, colorRango)
            }


            // DIBUJAR LÍNEAS (CONECTADAS)

            val pathReal = Path()
            val pathPred = Path()

            // Variable para guardar la coordenada del último punto "real"
            var ultimoPuntoReal: Offset? = null

            datos.forEachIndexed { i, d ->
                val x = i * stepX
                val y = getY(d.totalCosto)
                val puntoActual = Offset(x, y)

                if (d.tipo == "real") {
                    if (i == 0) pathReal.moveTo(x, y) else pathReal.lineTo(x, y)
                    ultimoPuntoReal = puntoActual // Guardamos referencia
                } else {
                    // Es predicción
                    if (pathPred.isEmpty) {
                        // CRUCIAL: Si es el primer punto de predicción, nos movemos al último real
                        // y dibujamos una línea hasta el actual para cerrar el hueco.
                        ultimoPuntoReal?.let { last ->
                            pathPred.moveTo(last.x, last.y)
                            pathPred.lineTo(x, y)
                        } ?: pathPred.moveTo(x, y) // Si no hay datos reales previos
                    } else {
                        pathPred.lineTo(x, y)
                    }
                }
            }

            // Dibujamos Real (Sólida)
            drawPath(pathReal, color = colorLineaReal, style = Stroke(width = 5f))

            // Dibujamos Predicción (Punteada)
            drawPath(
                pathPred,
                color = colorLineaPred,
                style = Stroke(
                    width = 5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )


            // PUNTOS Y EJE X

            datos.forEachIndexed { i, d ->
                val x = i * stepX
                val y = getY(d.totalCosto)

                // Puntos (Azul o Verde)
                val color = if(d.tipo == "real") colorLineaReal else colorLineaPred
                drawCircle(color, radius = 5f, center = Offset(x, y))
                drawCircle(colorPunto, radius = 2.5f, center = Offset(x, y))

                // Etiquetas Eje X (Solo algunas para no encimar)
                // Dibujamos si es el primero, el último, o cada 3 meses
                if (i == 0 || i == datos.lastIndex || i % 3 == 0) {
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

                // Línea vertical
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
            TooltipProyeccion(
                dato = dato,
                modifier = Modifier.align(Alignment.TopCenter)
            )
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