package com.example.gastoiq.data

import com.example.gastoiq.model.isoToMillis
import com.example.gastoiq.model.toLocal
import com.example.gastoiq.model.toRemote
import java.time.Instant

class SyncManager(
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao,
    private val gastoDao: GastoDao,
    private val presupuestoDao: PresupuestoDao,
    private val metaAhorroDao: MetaAhorroDao,
    private val remote: SupabaseRemoteDataSource,
    private val syncPreferences: SyncPreferences
) {

    suspend fun syncUsuarios() {
        val pendientes = usuarioDao.getPendingSync()
        for (usuario in pendientes) {
            val remoto = remote.upsertUsuario(usuario.toRemote())
            usuarioDao.update(
                usuario.copy(
                    remoteId = remoto.id,
                    updatedAt = remoto.updatedAt.isoToMillis(),
                    isSynced = true
                )
            )
        }

        val remotos = remote.fetchUsuarios()
        for (usuarioRemoto in remotos) {
            val local = usuarioRemoto.id?.let { usuarioDao.getByRemoteId(it) }
            if (local == null) {
                usuarioDao.insert(usuarioRemoto.toLocal())
            } else if (usuarioRemoto.updatedAt.isoToMillis() > local.updatedAt) {
                usuarioDao.update(usuarioRemoto.toLocal().copy(localId = local.localId))
            }
        }

        syncPreferences.setLastUsuarioSync(Instant.now().toString())
    }

    suspend fun syncCategorias() {
        val pendientes = categoriaDao.getPendingSync()
        for (categoria in pendientes) {
            val remoto = remote.upsertCategoria(categoria.toRemote())
            categoriaDao.update(
                categoria.copy(
                    remoteId = remoto.id,
                    updatedAt = remoto.updatedAt.isoToMillis(),
                    isSynced = true
                )
            )
        }

        val remotas = remote.fetchCategorias()
        for (categoriaRemota in remotas) {
            val local = categoriaRemota.id?.let { categoriaDao.getByRemoteId(it) }
            if (local == null) {
                categoriaDao.insert(categoriaRemota.toLocal())
            } else if (categoriaRemota.updatedAt.isoToMillis() > local.updatedAt) {
                categoriaDao.update(categoriaRemota.toLocal().copy(localId = local.localId))
            }
        }

        syncPreferences.setLastCategoriaSync(Instant.now().toString())
    }

    suspend fun syncGastos() {
        val pendientes = gastoDao.getPendingSync()
        for (gasto in pendientes) {
            val remoto = remote.upsertGasto(gasto.toRemote())
            gastoDao.update(
                gasto.copy(
                    remoteId = remoto.id,
                    updatedAt = remoto.updatedAt.isoToMillis(),
                    isSynced = true
                )
            )
        }

        val remotos = remote.fetchGastos()
        for (gastoRemoto in remotos) {
            val local = gastoRemoto.id?.let { gastoDao.getByRemoteId(it) }
            if (local == null) {
                gastoDao.insert(gastoRemoto.toLocal())
            } else if (gastoRemoto.updatedAt.isoToMillis() > local.updatedAt) {
                gastoDao.update(gastoRemoto.toLocal().copy(localId = local.localId))
            }
        }

        syncPreferences.setLastGastoSync(Instant.now().toString())
    }

    suspend fun syncPresupuestos() {
        val pendientes = presupuestoDao.getPendingSync()
        for (presupuesto in pendientes) {
            val remoto = remote.upsertPresupuesto(presupuesto.toRemote())
            presupuestoDao.update(
                presupuesto.copy(
                    remoteId = remoto.id,
                    updatedAt = remoto.updatedAt.isoToMillis(),
                    isSynced = true
                )
            )
        }

        val remotos = remote.fetchPresupuestos()
        for (presupuestoRemoto in remotos) {
            val local = presupuestoRemoto.id?.let { presupuestoDao.getByRemoteId(it) }
            if (local == null) {
                presupuestoDao.insert(presupuestoRemoto.toLocal())
            } else if (presupuestoRemoto.updatedAt.isoToMillis() > local.updatedAt) {
                presupuestoDao.update(presupuestoRemoto.toLocal().copy(localId = local.localId))
            }
        }

        syncPreferences.setLastPresupuestoSync(Instant.now().toString())
    }

    suspend fun syncMetas() {
        val pendientes = metaAhorroDao.getPendingSync()
        for (meta in pendientes) {
            val remoto = remote.upsertMeta(meta.toRemote())
            metaAhorroDao.update(
                meta.copy(
                    remoteId = remoto.id,
                    updatedAt = remoto.updatedAt.isoToMillis(),
                    isSynced = true
                )
            )
        }

        val remotos = remote.fetchMetas()
        for (metaRemota in remotos) {
            val local = metaRemota.id?.let { metaAhorroDao.getByRemoteId(it) }
            if (local == null) {
                metaAhorroDao.insert(metaRemota.toLocal())
            } else if (metaRemota.updatedAt.isoToMillis() > local.updatedAt) {
                metaAhorroDao.update(metaRemota.toLocal().copy(localId = local.localId))
            }
        }

        syncPreferences.setLastMetaSync(Instant.now().toString())
    }

    suspend fun syncAll() {
        syncUsuarios()
        syncCategorias()
        syncPresupuestos()
        syncMetas()
        syncGastos()
    }
}