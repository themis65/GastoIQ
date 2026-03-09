package com.example.gastoiq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gastoiq.GastoIQApp
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.data.SeedData
import com.example.gastoiq.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    private val _syncStatus = MutableLiveData<String>()
    val syncStatus: LiveData<String> = _syncStatus

    val allGastos: LiveData<List<GastoConCategoria>>

    init {
        val db = (application as GastoIQApp).database
        repository = AppRepository(
            db.usuarioDao(),
            db.categoriaDao(),
            db.gastoDao(),
            db.presupuestoDao(),
            db.metaAhorroDao()
        )
        allGastos = repository.allGastosConCategoria.asLiveData()

        // Insertar datos iniciales si no existen
        viewModelScope.launch {
            if (repository.countCategorias() == 0) {
                repository.insertCategorias(SeedData.categorias)
            }
            if (repository.getUsuario() == null) {
                repository.insertUsuario(SeedData.crearUsuarioDefault())
            }
        }
    }

    fun deleteGasto(gasto: GastoConCategoria) = viewModelScope.launch {
        val gastoReal = Gasto(gasto.id, gasto.monto, gasto.descripcion, gasto.fecha, gasto.notas, gasto.usuarioId, gasto.categoriaId)
        repository.deleteGasto(gastoReal)
    }

    fun iniciarSincronizacion() {
        viewModelScope.launch {
            _syncStatus.value = "CARGANDO"
            val exito = repository.sincronizarConNube()
            _syncStatus.value = if (exito) "EXITO" else "ERROR"
        }
    }
}
