package com.example.gastoiq.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioRemote(
    val id: String? = null,
    val nombre: String,
    val correo: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val deleted: Boolean = false
)