package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GastoConCategoria(
    val id: String,
    val monto: Double,
    val descripcion: String,
    val fecha: String,
    val notas: String,
    val usuarioId: String,
    val categoriaId: String,
    val categoriaNombre: String,
    val categoriaColor: String,
    val categoriaIcono: String
) : Parcelable
