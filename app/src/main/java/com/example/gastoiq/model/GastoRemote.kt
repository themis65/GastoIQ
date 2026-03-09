package com.example.gastoiq.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GastoRemote(
    val id: String? = null,
    @SerialName("usuario_id")
    val usuarioId: String,
    @SerialName("categoria_id")
    val categoriaId: String? = null,
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val deleted: Boolean = false
)