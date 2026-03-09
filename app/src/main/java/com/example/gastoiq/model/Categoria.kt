package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
@Parcelize
data class Categoria(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val color: String,
    val icono: String
) : Parcelable
