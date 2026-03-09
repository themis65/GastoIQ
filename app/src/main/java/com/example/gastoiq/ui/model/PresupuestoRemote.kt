package com.example.gastoiq.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresupuestoRemote(
    val id: String? = null,
    @SerialName("usuario_id")
    val usuarioId: String,
    @SerialName("categoria_id")
    val categoriaId: String? = null,
    val nombre: String,
    @SerialName("monto_limite")
    val montoLimite: Double,
    val gastado: Double = 0.0,
    val mes: Int,
    val anio: Int,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val deleted: Boolean = false
)