package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastoiq.model.Gasto

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos WHERE isDeleted = 0 ORDER BY fecha DESC, localId DESC")
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
}