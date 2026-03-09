package com.example.gastoiq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metas_ahorro")
data class MetaAhorro(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val remoteId: String? = null,
    val usuarioRemoteId: String,
    val nombre: String,
    val descripcion: String? = null,
    val montoObjetivo: Double,
    val montoActual: Double = 0.0,
    val fechaLimite: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)