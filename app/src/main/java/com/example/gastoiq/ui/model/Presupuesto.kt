package com.example.gastoiq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presupuestos")
data class Presupuesto(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val remoteId: String? = null,
    val usuarioRemoteId: String,
    val categoriaRemoteId: String? = null,
    val nombre: String,
    val montoLimite: Double,
    val gastado: Double = 0.0,
    val mes: Int,
    val anio: Int,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)