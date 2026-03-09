package com.example.gastoiq

import android.app.Application
import com.example.gastoiq.data.AppDatabase

class GastoIQApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
