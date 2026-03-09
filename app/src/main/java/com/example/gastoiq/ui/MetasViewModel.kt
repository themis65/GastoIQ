package com.example.gastoiq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gastoiq.GastoIQApp
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.model.MetaAhorro
import com.example.gastoiq.utils.UiState
import kotlinx.coroutines.launch
import java.util.UUID

class MetasViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GastoIQApp
    private val repository: AppRepository = app.repository
    val allMetas: LiveData<List<MetaAhorro>> = repository.allMetas.asLiveData()

    private val _operationState = MutableLiveData<UiState<String>>(UiState.Idle)
    val operationState: LiveData<UiState<String>> = _operationState

    fun crearMeta(nombre: String, montoObjetivo: Double, fechaLimite: String) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            try {
                val usuario = repository.getUsuario()
                val meta = MetaAhorro(
                    id = UUID.randomUUID().toString(),
                    nombre = nombre,
                    montoObjetivo = montoObjetivo,
                    montoActual = 0.0,
                    fechaLimite = fechaLimite,
                    usuarioId = usuario?.id ?: app.deviceId
                )
                repository.insertMeta(meta)
                _operationState.value = UiState.Success("Meta creada")
            } catch (e: Exception) {
                _operationState.value = UiState.Error("Error al crear meta")
            }
        }
    }

    fun actualizarAhorro(meta: MetaAhorro, nuevoMonto: Double) {
        viewModelScope.launch {
            try {
                repository.updateMeta(meta.copy(montoActual = nuevoMonto))
            } catch (_: Exception) {}
        }
    }

    fun eliminarMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            repository.deleteMeta(meta)
        }
    }

    fun resetState() { _operationState.value = UiState.Idle }
}
