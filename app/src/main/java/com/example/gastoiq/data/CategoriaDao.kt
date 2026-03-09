package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastoiq.model.Categoria

@Dao
interface CategoriaDao {

    @Query("SELECT * FROM categorias WHERE isDeleted = 0 ORDER BY nombre ASC")
    suspend fun getAll(): List<Categoria>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria): Long

    @Update
    suspend fun update(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE isSynced = 0")
    suspend fun getPendingSync(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): Categoria?

    @Query("UPDATE categorias SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDelete(localId: Int, updatedAt: Long)
}