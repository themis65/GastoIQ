package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gastoiq.model.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun getUsuario(): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)
}
