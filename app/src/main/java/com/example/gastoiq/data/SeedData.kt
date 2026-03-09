package com.example.gastoiq.data

import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Etiqueta
import com.example.gastoiq.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SeedData {

    val categorias = listOf(
        Categoria("cat_comida",     "Comida",       "#FF6B6B", "\uD83C\uDF54"),
        Categoria("cat_transporte", "Transporte",   "#4ECDC4", "\uD83D\uDE8C"),
        Categoria("cat_ocio",       "Ocio",         "#A78BFA", "\uD83C\uDFAE"),
        Categoria("cat_educacion",  "Educación",    "#FACC15", "\uD83D\uDCDA"),
        Categoria("cat_salud",      "Salud",        "#4CAF50", "\uD83D\uDC8A"),
        Categoria("cat_compras",    "Compras",      "#FF9800", "\uD83D\uDED2"),
        Categoria("cat_servicios",  "Servicios",    "#2196F3", "\uD83D\uDCF1"),
        Categoria("cat_otros",      "Otros",        "#9E9E9E", "\uD83D\uDCE6")
    )

    val etiquetas = listOf(
        Etiqueta("et_urgente",    "Urgente",      "#F44336"),
        Etiqueta("et_recurrente", "Recurrente",   "#FF9800"),
        Etiqueta("et_opcional",   "Opcional",     "#4CAF50"),
        Etiqueta("et_fijo",       "Fijo",         "#2196F3"),
        Etiqueta("et_variable",   "Variable",     "#9C27B0")
    )

    fun crearUsuario(deviceId: String): Usuario {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return Usuario(
            id = deviceId,
            nombre = "Usuario_${deviceId.take(6)}",
            email = "${deviceId.take(8)}@gastoiq.app",
            fechaRegistro = fecha
        )
    }
}