package com.example.gastoiq.data

import android.content.Context

class SyncPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    fun getLastUsuarioSync(): String? = prefs.getString("last_usuario_sync", null)
    fun setLastUsuarioSync(value: String) = prefs.edit().putString("last_usuario_sync", value).apply()

    fun getLastCategoriaSync(): String? = prefs.getString("last_categoria_sync", null)
    fun setLastCategoriaSync(value: String) = prefs.edit().putString("last_categoria_sync", value).apply()

    fun getLastGastoSync(): String? = prefs.getString("last_gasto_sync", null)
    fun setLastGastoSync(value: String) = prefs.edit().putString("last_gasto_sync", value).apply()

    fun getLastPresupuestoSync(): String? = prefs.getString("last_presupuesto_sync", null)
    fun setLastPresupuestoSync(value: String) = prefs.edit().putString("last_presupuesto_sync", value).apply()

    fun getLastMetaSync(): String? = prefs.getString("last_meta_sync", null)
    fun setLastMetaSync(value: String) = prefs.edit().putString("last_meta_sync", value).apply()
}