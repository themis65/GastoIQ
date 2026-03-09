package com.example.gastoiq.data

import android.util.Log
import com.example.gastoiq.model.*
import com.example.gastoiq.network.SupabaseClient
import com.example.gastoiq.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao,
    private val gastoDao: GastoDao,
    private val presupuestoDao: PresupuestoDao,
    private val metaAhorroDao: MetaAhorroDao,
    private val etiquetaDao: EtiquetaDao,
    private val networkMonitor: NetworkMonitor
) {
    private val api = SupabaseClient.apiService

    val isOnline: Boolean get() = networkMonitor.isConnected
    val networkState: Flow<Boolean> = networkMonitor.networkState

    // === USUARIO ===
    suspend fun getUsuario(): Usuario? = usuarioDao.getUsuario()
    suspend fun insertUsuario(usuario: Usuario) = usuarioDao.insert(usuario)

    // === CATEGORIAS ===
    fun getAllCategorias(): Flow<List<Categoria>> = categoriaDao.getAll()
    suspend fun getAllCategoriasList(): List<Categoria> = categoriaDao.getAllList()
    suspend fun getCategoriaById(id: String): Categoria? = categoriaDao.getById(id)
    suspend fun insertCategorias(categorias: List<Categoria>) = categoriaDao.insertAll(categorias)
    suspend fun countCategorias(): Int = categoriaDao.count()

    // === ETIQUETAS ===
    fun getAllEtiquetas(): Flow<List<Etiqueta>> = etiquetaDao.getAll()
    suspend fun getAllEtiquetasList(): List<Etiqueta> = etiquetaDao.getAllList()
    suspend fun insertEtiquetas(etiquetas: List<Etiqueta>) = etiquetaDao.insertAll(etiquetas)
    suspend fun countEtiquetas(): Int = etiquetaDao.count()
    suspend fun getEtiquetasDeGasto(gastoId: String): List<Etiqueta> = etiquetaDao.getEtiquetasDeGasto(gastoId)
    suspend fun setEtiquetasDeGasto(gastoId: String, etiquetaIds: List<String>) {
        etiquetaDao.deleteEtiquetasDeGasto(gastoId)
        etiquetaIds.forEach { etId ->
            etiquetaDao.insertGastoEtiqueta(GastoEtiqueta(gastoId, etId))
        }
    }

    // === GASTOS (Local) ===
    val allGastosConCategoria: Flow<List<GastoConCategoria>> = gastoDao.getAllConCategoria()
    fun getAllGastos(): Flow<List<Gasto>> = gastoDao.getAll()
    suspend fun insertGasto(gasto: Gasto) {
        gastoDao.insert(gasto)
        if (isOnline) {
            try {
                api.insertGasto(gasto.copy(sincronizado = true))
                gastoDao.marcarSincronizado(gasto.id)
            } catch (e: Exception) {
                Log.e("AppRepository", "Error sync gasto: ${e.message}")
            }
        }
    }
    suspend fun updateGasto(gasto: Gasto) {
        gastoDao.update(gasto)
        if (isOnline) {
            try {
                api.updateGasto("eq.${gasto.id}", gasto)
            } catch (e: Exception) {
                Log.e("AppRepository", "Error sync update: ${e.message}")
            }
        }
    }
    suspend fun deleteGasto(gasto: Gasto) {
        gastoDao.delete(gasto)
        if (isOnline) {
            try { api.deleteGasto("eq.${gasto.id}") } catch (_: Exception) {}
        }
    }
    suspend fun getTotalMes(mes: String): Double = gastoDao.getTotalMes(mes) ?: 0.0
    suspend fun getTotalPorCategoriaMes(catId: String, mes: String): Double =
        gastoDao.getTotalPorCategoriaMes(catId, mes) ?: 0.0
    suspend fun getResumenPorCategoria(mes: String): List<ResumenCategoria> =
        gastoDao.getResumenPorCategoria(mes)

    // === PRESUPUESTOS ===
    fun getPresupuestosByMes(mes: String): Flow<List<Presupuesto>> = presupuestoDao.getByMes(mes)
    suspend fun insertPresupuesto(p: Presupuesto) {
        presupuestoDao.insert(p)
        if (isOnline) {
            try { api.insertPresupuesto(p) } catch (_: Exception) {}
        }
    }
    suspend fun deletePresupuesto(p: Presupuesto) {
        presupuestoDao.delete(p)
        if (isOnline) {
            try { api.deletePresupuesto("eq.${p.id}") } catch (_: Exception) {}
        }
    }

    // === METAS ===
    val allMetas: Flow<List<MetaAhorro>> = metaAhorroDao.getAll()
    suspend fun insertMeta(meta: MetaAhorro) {
        metaAhorroDao.insert(meta)
        if (isOnline) {
            try { api.insertMeta(meta) } catch (_: Exception) {}
        }
    }
    suspend fun updateMeta(meta: MetaAhorro) {
        metaAhorroDao.update(meta)
        if (isOnline) {
            try { api.updateMeta("eq.${meta.id}", meta) } catch (_: Exception) {}
        }
    }
    suspend fun deleteMeta(meta: MetaAhorro) {
        metaAhorroDao.delete(meta)
        if (isOnline) {
            try { api.deleteMeta("eq.${meta.id}") } catch (_: Exception) {}
        }
    }

    // === SINCRONIZACION COMPLETA ===
    suspend fun sincronizarTodo(): Boolean {
        if (!isOnline) return false
        return try {
            // Subir gastos no sincronizados
            val pendientes = gastoDao.getNoSincronizados()
            for (g in pendientes) {
                val resp = api.insertGasto(g.copy(sincronizado = true))
                if (resp.isSuccessful) {
                    gastoDao.marcarSincronizado(g.id)
                }
            }
            // Descargar datos remotos
            descargarDatosRemotos()
            true
        } catch (e: Exception) {
            Log.e("AppRepository", "Error sincronizando: ${e.message}")
            false
        }
    }

    private suspend fun descargarDatosRemotos() {
        try {
            val respGastos = api.getGastos()
            if (respGastos.isSuccessful) {
                respGastos.body()?.forEach { g ->
                    gastoDao.insert(g.copy(sincronizado = true))
                }
            }
            val respMetas = api.getMetas()
            if (respMetas.isSuccessful) {
                respMetas.body()?.forEach { m ->
                    metaAhorroDao.insert(m)
                }
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Error descargando: ${e.message}")
        }
    }
}
