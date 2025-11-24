package com.example.pge.views.AnalisisPrediccion

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pge.models.analisisprediccion.DatoGrafica
import kotlin.math.abs

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

    // Estado para el tooltip
    var puntoSeleccionado by remember { mutableStateOf<DatoGrafica?>(null) }
    var posicionTapX by remember { mutableStateOf(0f) }

    // Cálculos de Escala
    val maxCosto = datos.maxOf { it.rangoMax ?: it.totalCosto } * 1.1 // 10% margen arriba
    val minCosto = 0f // Empezar en 0 para referencia visual clara

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        posicionTapX = offset.x
                        // Encontrar el punto más cercano al toque en X
                        val anchoPaso = size.width / (datos.size - 1)
                        val index = (offset.x / anchoPaso).let { Math.round(it) }.coerceIn(0, datos.size - 1)
                        puntoSeleccionado = datos[index]
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val pasoX = width / (datos.size - 1)

            // Función auxiliar para mapear Costo -> Y Pixel
            fun getY(costo: Double): Float {
                val ratio = costo / maxCosto
                return (height - (ratio * height)).toFloat()
            }

            //  DIBUJAR ÁREA DE CONFIANZA (Solo para predicciones)
            val pathRango = Path()
            var primerPuntoRango = true

            // Encontramos donde empieza la predicción para conectar suavemente
            datos.forEachIndexed { i, d ->
                if (d.tipo == "prediccion" && d.rangoMin != null && d.rangoMax != null) {
                    val x = i * pasoX
                    val yMax = getY(d.rangoMax)

                    if (primerPuntoRango) {
                        pathRango.moveTo(x, yMax)
                        primerPuntoRango = false
                        // Conectar con el último punto real si existe
                        if(i > 0) {
                            val xPrev = (i-1) * pasoX
                            val yPrev = getY(datos[i-1].totalCosto)
                            pathRango.lineTo(xPrev, yPrev) // Hack visual para unir
                            pathRango.lineTo(x, yMax)
                        }
                    } else {
                        pathRango.lineTo(x, yMax)
                    }
                }
            }
            // Regresar por el minimo
            for (i in datos.indices.reversed()) {
                val d = datos[i]
                if (d.tipo == "prediccion" && d.rangoMin != null) {
                    val x = i * pasoX
                    val yMin = getY(d.rangoMin)
                    pathRango.lineTo(x, yMin)
                }
            }
            pathRango.close()
            drawPath(pathRango, colorRango)

            // DIBUJAR LÍNEAS (Real y Predicción)
            val pathReal = Path()
            val pathPred = Path()

            datos.forEachIndexed { i, d ->
                val x = i * pasoX
                val y = getY(d.totalCosto)

                if (i == 0) {
                    pathReal.moveTo(x, y)
                } else {
                    if (d.tipo == "real") {
                        pathReal.lineTo(x, y)
                        // Preparamos el inicio de la prediccion desde el ultimo real
                        pathPred.moveTo(x,y)
                    } else {
                        pathPred.lineTo(x, y)
                    }
                }

                // Dibujar Puntos
                val colorPuntoActual = if(d.tipo == "real") colorLineaReal else colorLineaPred
                drawCircle(colorPuntoActual, radius = 8f, center = Offset(x, y))
                drawCircle(colorPunto, radius = 4f, center = Offset(x, y)) // Centro blanco
            }

            drawPath(pathReal, color = colorLineaReal, style = Stroke(width = 5f))
            // Línea punteada para predicción
            drawPath(
                pathPred,
                color = colorLineaPred,
                style = Stroke(
                    width = 5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )

            // DIBUJAR LÍNEA VERTICAL DE SELECCIÓN
            puntoSeleccionado?.let { seleccionado ->
                val index = datos.indexOf(seleccionado)
                val x = index * pasoX
                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
        }

        // TOOLTIP (Ventana flotante)
        puntoSeleccionado?.let { dato ->
            TooltipInfo(
                dato = dato,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
            )
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