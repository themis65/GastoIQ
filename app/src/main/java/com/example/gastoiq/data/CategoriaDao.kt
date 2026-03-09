package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gastoiq.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun getAllList(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getById(id: String): Categoria?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<Categoria>)

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun count(): Int
}
