package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.gastoiq.model.Presupuesto
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {
    @Query("SELECT * FROM presupuestos WHERE mes = :mes ORDER BY montoLimite DESC")
    fun getByMes(mes: String): Flow<List<Presupuesto>>

    @Query("SELECT * FROM presupuestos WHERE id = :id")
    suspend fun getById(id: String): Presupuesto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(presupuesto: Presupuesto)

    @Update
    suspend fun update(presupuesto: Presupuesto)

    @Delete
    suspend fun delete(presupuesto: Presupuesto): Int

    @Query("SELECT * FROM presupuestos")
    suspend fun getAllList(): List<Presupuesto>
}
