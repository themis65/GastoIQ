package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.gastoiq.model.Etiqueta
import com.example.gastoiq.model.GastoEtiqueta
import kotlinx.coroutines.flow.Flow

@Dao
interface EtiquetaDao {
    @Query("SELECT * FROM etiquetas ORDER BY nombre ASC")
    fun getAll(): Flow<List<Etiqueta>>

    @Query("SELECT * FROM etiquetas ORDER BY nombre ASC")
    suspend fun getAllList(): List<Etiqueta>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(etiqueta: Etiqueta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(etiquetas: List<Etiqueta>)

    @Delete
    suspend fun delete(etiqueta: Etiqueta): Int

    @Query("SELECT COUNT(*) FROM etiquetas")
    suspend fun count(): Int

    // Relacion N:M
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGastoEtiqueta(ge: GastoEtiqueta)

    @Query("""
        SELECT e.* FROM etiquetas e
        INNER JOIN gasto_etiqueta ge ON e.id = ge.etiquetaId
        WHERE ge.gastoId = :gastoId
    """)
    suspend fun getEtiquetasDeGasto(gastoId: String): List<Etiqueta>

    @Query("DELETE FROM gasto_etiqueta WHERE gastoId = :gastoId")
    suspend fun deleteEtiquetasDeGasto(gastoId: String)
}
