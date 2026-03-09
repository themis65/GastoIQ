package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
@Parcelize
data class Usuario(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val email: String,
    val fechaRegistro: String
) : Parcelable
