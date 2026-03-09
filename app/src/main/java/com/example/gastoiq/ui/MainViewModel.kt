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
import com.example.gastoiq.utils.UiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GastoIQApp
    private val repository: AppRepository = app.repository
    private val _syncState = MutableLiveData<UiState<String>>(UiState.Idle)
    val syncState: LiveData<UiState<String>> = _syncState

    val isOnline: LiveData<Boolean> = repository.networkState.asLiveData()

    val allGastos: LiveData<List<GastoConCategoria>> = repository.allGastosConCategoria.asLiveData()

    val mesActual: String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    val totalMes: LiveData<Double> = repository.allGastosConCategoria.map { lista ->
        lista.filter { it.fecha.startsWith(mesActual) }.sumOf { it.monto }
    }.asLiveData()

    init {
        viewModelScope.launch {
            if (repository.countCategorias() == 0) {
                repository.insertCategorias(SeedData.categorias)
            }
            if (repository.countEtiquetas() == 0) {
                repository.insertEtiquetas(SeedData.etiquetas)
            }
            if (repository.getUsuario() == null) {
                repository.insertUsuario(SeedData.crearUsuario(app.deviceId))
            }
        }
    }

    fun deleteGasto(gasto: GastoConCategoria) = viewModelScope.launch {
        val gastoReal = Gasto(
            gasto.id, gasto.monto, gasto.descripcion, gasto.fecha,
            gasto.notas, gasto.usuarioId, gasto.categoriaId
        )
        repository.deleteGasto(gastoReal)
    }

    fun sincronizar() {
        viewModelScope.launch {
            _syncState.value = UiState.Loading
            val exito = repository.sincronizarTodo()
            _syncState.value = if (exito) {
                UiState.Success("Sincronización completada")
            } else {
                UiState.Error("Error al sincronizar. Verifica tu conexión.")
            }
        }
    }

    fun resetSyncState() {
        _syncState.value = UiState.Idle
    }
}
