package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "metas_ahorro",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class MetaAhorro(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val montoObjetivo: Double,
    val montoActual: Double = 0.0,
    val fechaLimite: String,
    val usuarioId: String
) : Parcelable
