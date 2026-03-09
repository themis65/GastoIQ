package com.example.gastoiq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val remoteId: String? = null,
    val usuarioRemoteId: String,
    val categoriaRemoteId: String? = null,
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)