package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastoiq.model.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE isDeleted = 0 ORDER BY localId ASC")
    suspend fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun getUsuario(): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE isSynced = 0")
    suspend fun getPendingSync(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): Usuario?

    @Query("UPDATE usuarios SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDelete(localId: Int, updatedAt: Long)
}