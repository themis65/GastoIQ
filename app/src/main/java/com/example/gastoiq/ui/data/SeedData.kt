package com.example.gastoiq.data

import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SeedData {

    val categorias = listOf(
        Categoria("cat_comida",     "Comida",       "#FF6B6B", "🍔"),
        Categoria("cat_transporte", "Transporte",   "#4ECDC4", "🚌"),
        Categoria("cat_ocio",       "Ocio",         "#A78BFA", "🎮"),
        Categoria("cat_educacion",  "Educacion",    "#FACC15", "📚"),
        Categoria("cat_salud",      "Salud",        "#4CAF50", "💊"),
        Categoria("cat_compras",    "Compras",      "#FF9800", "🛒"),
        Categoria("cat_servicios",  "Servicios",    "#2196F3", "📱"),
        Categoria("cat_otros",      "Otros",        "#9E9E9E", "📦")
    )

    fun crearUsuarioDefault(): Usuario {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return Usuario(
            id = "user_default",
            nombre = "Estudiante",
            email = "estudiante@correo.com",
            fechaRegistro = fecha
        )
    }
}
