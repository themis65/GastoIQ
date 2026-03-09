package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastoiq.model.Presupuesto

@Dao
interface PresupuestoDao {

    @Query("SELECT * FROM presupuestos WHERE isDeleted = 0 ORDER BY anio DESC, mes DESC, localId DESC")
    suspend fun getAll(): List<Presupuesto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(presupuesto: Presupuesto): Long

    @Update
    suspend fun update(presupuesto: Presupuesto)

    @Query("SELECT * FROM presupuestos WHERE isSynced = 0")
    suspend fun getPendingSync(): List<Presupuesto>

    @Query("SELECT * FROM presupuestos WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): Presupuesto?

    @Query("UPDATE presupuestos SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDelete(localId: Int, updatedAt: Long)
}