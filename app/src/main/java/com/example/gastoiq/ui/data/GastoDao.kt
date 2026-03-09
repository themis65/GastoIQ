package com.example.gastoiq.data

import androidx.room.*
import com.example.gastoiq.model.Gasto

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos WHERE isDeleted = 0 ORDER BY fecha DESC")
    suspend fun getAll(): List<Gasto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gasto: Gasto): Long

    @Update
    suspend fun update(gasto: Gasto)

    @Query("SELECT * FROM gastos WHERE isSynced = 0")
    suspend fun getPendingSync(): List<Gasto>

    @Query("SELECT * FROM gastos WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): Gasto?

    @Query("UPDATE gastos SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDelete(localId: Int, updatedAt: Long)

    @Query("DELETE FROM gastos")
    suspend fun deleteAll()
}