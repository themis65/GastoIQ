package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastoiq.model.MetaAhorro

@Dao
interface MetaAhorroDao {

    @Query("SELECT * FROM metas_ahorro WHERE isDeleted = 0 ORDER BY localId DESC")
    suspend fun getAll(): List<MetaAhorro>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meta: MetaAhorro): Long

    @Update
    suspend fun update(meta: MetaAhorro)

    @Query("SELECT * FROM metas_ahorro WHERE isSynced = 0")
    suspend fun getPendingSync(): List<MetaAhorro>

    @Query("SELECT * FROM metas_ahorro WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): MetaAhorro?

    @Query("UPDATE metas_ahorro SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDelete(localId: Int, updatedAt: Long)
}