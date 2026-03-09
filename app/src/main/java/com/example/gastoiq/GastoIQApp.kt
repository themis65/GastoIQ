package com.example.gastoiq

import android.app.Application
import android.provider.Settings
import com.example.gastoiq.data.AppDatabase
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.utils.NetworkMonitor

class GastoIQApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val networkMonitor: NetworkMonitor by lazy { NetworkMonitor(this) }

    /** ID unico por dispositivo + instalacion de la app */
    val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    val repository: AppRepository by lazy {
        AppRepository(
            database.usuarioDao(),
            database.categoriaDao(),
            database.gastoDao(),
            database.presupuestoDao(),
            database.metaAhorroDao(),
            database.etiquetaDao(),
            networkMonitor
        )
    }
}