package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "metas_ahorro",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"])]
)
@Parcelize
data class MetaAhorro(
    @PrimaryKey
    val id: String,
    val nombre: String,
    @SerializedName("monto_objetivo")
    val montoObjetivo: Double,
    @SerializedName("monto_actual")
    val montoActual: Double = 0.0,
    @SerializedName("fecha_limite")
    val fechaLimite: String,
    @SerializedName("usuario_id")
    val usuarioId: String
) : Parcelable
