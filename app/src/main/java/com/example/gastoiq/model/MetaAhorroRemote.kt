package com.example.gastoiq.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaAhorroRemote(
    val id: String? = null,
    @SerialName("usuario_id")
    val usuarioId: String,
    val nombre: String,
    val descripcion: String? = null,
    @SerialName("monto_objetivo")
    val montoObjetivo: Double,
    @SerialName("monto_actual")
    val montoActual: Double = 0.0,
    @SerialName("fecha_limite")
    val fechaLimite: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val deleted: Boolean = false
)