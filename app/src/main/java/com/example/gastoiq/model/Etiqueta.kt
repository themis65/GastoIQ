package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "etiquetas")
@Parcelize
data class Etiqueta(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val color: String
) : Parcelable
