package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId"]),
        Index(value = ["categoriaId"])
    ]
)
@Parcelize
data class Gasto(
    @PrimaryKey
    val id: String,
    val monto: Double,
    val descripcion: String,
    val fecha: String,
    val notas: String = "",
    @SerializedName("usuario_id")
    val usuarioId: String,
    @SerializedName("categoria_id")
    val categoriaId: String,
    val sincronizado: Boolean = false
) : Parcelable
