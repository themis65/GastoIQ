package com.example.gastoiq.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriaRemote(
    val id: String? = null,
    @SerialName("usuario_id")
    val usuarioId: String,
    val nombre: String,
    val tipo: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val deleted: Boolean = false
)