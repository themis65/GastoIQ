package com.example.gastoiq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val remoteId: String? = null,
    val usuarioRemoteId: String,
    val nombre: String,
    val tipo: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)