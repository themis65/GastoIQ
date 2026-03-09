package com.example.gastoiq.data

import android.util.Log
import com.example.gastoiq.model.*
import com.example.gastoiq.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao,
    private val gastoDao: GastoDao,
    private val presupuestoDao: PresupuestoDao,
    private val metaAhorroDao: MetaAhorroDao
) {
    // === USUARIO ===
    suspend fun getUsuario(): Usuario? = usuarioDao.getUsuario()
    suspend fun insertUsuario(usuario: Usuario) = usuarioDao.insert(usuario)

    // === CATEGORIAS ===
    fun getAllCategorias(): Flow<List<Categoria>> = categoriaDao.getAll()
    suspend fun getAllCategoriasList(): List<Categoria> = categoriaDao.getAllList()
    suspend fun getCategoriaById(id: String): Categoria? = categoriaDao.getById(id)
    suspend fun insertCategorias(categorias: List<Categoria>) = categoriaDao.insertAll(categorias)
    suspend fun countCategorias(): Int = categoriaDao.count()

    // === GASTOS ===
    val allGastosConCategoria: Flow<List<GastoConCategoria>> = gastoDao.getAllConCategoria()
    fun getAllGastos(): Flow<List<Gasto>> = gastoDao.getAll()
    suspend fun insertGasto(gasto: Gasto) = gastoDao.insert(gasto)
    suspend fun updateGasto(gasto: Gasto) = gastoDao.update(gasto)
    suspend fun deleteGasto(gasto: Gasto) = gastoDao.delete(gasto)
    suspend fun getTotalMes(mes: String): Double = gastoDao.getTotalMes(mes) ?: 0.0
    suspend fun getTotalPorCategoriaMes(catId: String, mes: String): Double =
        gastoDao.getTotalPorCategoriaMes(catId, mes) ?: 0.0

    // === PRESUPUESTOS ===
    fun getPresupuestosByMes(mes: String): Flow<List<Presupuesto>> = presupuestoDao.getByMes(mes)
    suspend fun insertPresupuesto(p: Presupuesto) = presupuestoDao.insert(p)
    suspend fun deletePresupuesto(p: Presupuesto) = presupuestoDao.delete(p)

    // === METAS DE AHORRO ===
    val allMetas: Flow<List<MetaAhorro>> = metaAhorroDao.getAll()
    suspend fun insertMeta(meta: MetaAhorro) = metaAhorroDao.insert(meta)
    suspend fun updateMeta(meta: MetaAhorro) = metaAhorroDao.update(meta)
    suspend fun deleteMeta(meta: MetaAhorro) = metaAhorroDao.delete(meta)

    // === SINCRONIZACION ===
    suspend fun sincronizarConNube(): Boolean {
        return try {
            val gastos = gastoDao.getGastosParaSincronizar()
            for (g in gastos) {
                val resp = RetrofitClient.apiService.subirGasto(g)
                if (!resp.isSuccessful) {
                    Log.e("AppRepository", "Error subiendo gasto: " + resp.errorBody()?.string())
                    return false
                }
            }
            val presupuestos = presupuestoDao.getParaSincronizar()
            for (p in presupuestos) {
                val resp = RetrofitClient.apiService.subirPresupuesto(p)
                if (!resp.isSuccessful) {
                    Log.e("AppRepository", "Error subiendo presupuesto")
                    return false
                }
            }
            val metas = metaAhorroDao.getParaSincronizar()
            for (m in metas) {
                val resp = RetrofitClient.apiService.subirMeta(m)
                if (!resp.isSuccessful) {
                    Log.e("AppRepository", "Error subiendo meta")
                    return false
                }
            }
            true
        } catch (e: Exception) {
            Log.e("AppRepository", "Error sincronizando: " + e.message)
            false
        }
    }
}
