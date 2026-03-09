package com.example.gastoiq.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.gastoiq.model.Gasto
import com.example.gastoiq.model.GastoConCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("""
        SELECT g.id, g.monto, g.descripcion, g.fecha, g.notas, g.usuarioId, g.categoriaId,
               c.nombre AS categoriaNombre, c.color AS categoriaColor
        FROM gastos g
        INNER JOIN categorias c ON g.categoriaId = c.id
        ORDER BY g.fecha DESC
    """)
    fun getAllConCategoria(): Flow<List<GastoConCategoria>>

    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    fun getAll(): Flow<List<Gasto>>

    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun getById(id: String): Gasto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gasto: Gasto)

    @Update
    suspend fun update(gasto: Gasto)

    @Delete
    suspend fun delete(gasto: Gasto): Int

    @Query("SELECT * FROM gastos")
    suspend fun getGastosParaSincronizar(): List<Gasto>

    @Query("SELECT SUM(monto) FROM gastos WHERE substr(fecha, 1, 7) = :mes")
    suspend fun getTotalMes(mes: String): Double?

    @Query("""
        SELECT SUM(g.monto) FROM gastos g
        WHERE g.categoriaId = :categoriaId AND substr(g.fecha, 1, 7) = :mes
    """)
    suspend fun getTotalPorCategoriaMes(categoriaId: String, mes: String): Double?
}
