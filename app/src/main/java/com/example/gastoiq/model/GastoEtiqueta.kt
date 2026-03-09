package com.example.gastoiq.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "gasto_etiqueta",
    primaryKeys = ["gastoId", "etiquetaId"],
    foreignKeys = [
        ForeignKey(
            entity = Gasto::class,
            parentColumns = ["id"],
            childColumns = ["gastoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Etiqueta::class,
            parentColumns = ["id"],
            childColumns = ["etiquetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["gastoId"]),
        Index(value = ["etiquetaId"])
    ]
)
data class GastoEtiqueta(
    val gastoId: String,
    val etiquetaId: String
)
