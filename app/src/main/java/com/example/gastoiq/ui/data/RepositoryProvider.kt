package com.example.gastoiq.data

import android.content.Context

object RepositoryProvider {

    @Volatile
    private var repository: AppRepository? = null

    fun getRepository(context: Context): AppRepository {
        return repository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            val remote = SupabaseRemoteDataSource()
            val prefs = SyncPreferences(context.applicationContext)

            val syncManager = SyncManager(
                usuarioDao = db.usuarioDao(),
                categoriaDao = db.categoriaDao(),
                gastoDao = db.gastoDao(),
                presupuestoDao = db.presupuestoDao(),
                metaAhorroDao = db.metaAhorroDao(),
                remote = remote,
                syncPreferences = prefs
            )

            val repo = AppRepository(
                usuarioDao = db.usuarioDao(),
                categoriaDao = db.categoriaDao(),
                gastoDao = db.gastoDao(),
                presupuestoDao = db.presupuestoDao(),
                metaAhorroDao = db.metaAhorroDao(),
                syncManager = syncManager
            )

            repository = repo
            repo
        }
    }
}