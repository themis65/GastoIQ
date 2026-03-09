package com.example.gastoiq.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

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
        val usuarioId: String,
        val categoriaId: String
) : Parcelable
