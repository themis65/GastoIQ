package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "usuarios")
@Parcelize
data class Usuario(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val email: String,
    @SerializedName("fecha_registro")
    val fechaRegistro: String
) : Parcelable
