package com.example.gastoiq.data

import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Gasto
import com.example.gastoiq.model.MetaAhorro
import com.example.gastoiq.model.Presupuesto
import com.example.gastoiq.model.Usuario

class AppRepository(
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao,
    private val gastoDao: GastoDao,
    private val presupuestoDao: PresupuestoDao,
    private val metaAhorroDao: MetaAhorroDao,
    private val syncManager: SyncManager
) {

    suspend fun guardarUsuario(usuario: Usuario) {
        usuarioDao.insert(
            usuario.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    suspend fun guardarCategoria(categoria: Categoria) {
        categoriaDao.insert(
            categoria.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    suspend fun guardarGasto(gasto: Gasto) {
        gastoDao.insert(
            gasto.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    suspend fun guardarPresupuesto(presupuesto: Presupuesto) {
        presupuestoDao.insert(
            presupuesto.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    suspend fun guardarMeta(meta: MetaAhorro) {
        metaAhorroDao.insert(
            meta.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    suspend fun obtenerUsuarios(): List<Usuario> = usuarioDao.getAll()

    suspend fun obtenerCategorias(): List<Categoria> = categoriaDao.getAll()

    suspend fun obtenerGastos(): List<Gasto> = gastoDao.getAll()

    suspend fun obtenerPresupuestos(): List<Presupuesto> = presupuestoDao.getAll()

    suspend fun obtenerMetas(): List<MetaAhorro> = metaAhorroDao.getAll()

    suspend fun eliminarUsuario(localId: Int) {
        usuarioDao.softDelete(localId, System.currentTimeMillis())
    }

    suspend fun eliminarCategoria(localId: Int) {
        categoriaDao.softDelete(localId, System.currentTimeMillis())
    }

    suspend fun eliminarGasto(localId: Int) {
        gastoDao.softDelete(localId, System.currentTimeMillis())
    }

    suspend fun eliminarPresupuesto(localId: Int) {
        presupuestoDao.softDelete(localId, System.currentTimeMillis())
    }

    suspend fun eliminarMeta(localId: Int) {
        metaAhorroDao.softDelete(localId, System.currentTimeMillis())
    }

    suspend fun syncAll() {
        syncManager.syncAll()
    }
}