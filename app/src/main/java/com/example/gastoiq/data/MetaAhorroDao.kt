package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.gastoiq.model.MetaAhorro
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaAhorroDao {
    @Query("SELECT * FROM metas_ahorro ORDER BY fechaLimite ASC")
    fun getAll(): Flow<List<MetaAhorro>>

    @Query("SELECT * FROM metas_ahorro WHERE id = :id")
    suspend fun getById(id: String): MetaAhorro?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meta: MetaAhorro)

    @Update
    suspend fun update(meta: MetaAhorro)

    @Delete
    suspend fun delete(meta: MetaAhorro): Int

    @Query("SELECT * FROM metas_ahorro")
    suspend fun getAllList(): List<MetaAhorro>
}
