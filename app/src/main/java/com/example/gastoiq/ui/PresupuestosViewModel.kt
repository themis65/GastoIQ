package com.example.gastoiq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gastoiq.GastoIQApp
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Presupuesto
import com.example.gastoiq.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PresupuestosViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GastoIQApp
    private val repository: AppRepository = app.repository
    val mesActual: String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    val presupuestos: LiveData<List<Presupuesto>> =
        repository.getPresupuestosByMes(mesActual).asLiveData()

    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> = _categorias

    private val _operationState = MutableLiveData<UiState<String>>(UiState.Idle)
    val operationState: LiveData<UiState<String>> = _operationState

    // Map de gastos por categoria para mostrar progreso
    private val _gastosPorCategoria = MutableLiveData<Map<String, Double>>()
    val gastosPorCategoria: LiveData<Map<String, Double>> = _gastosPorCategoria

    init {
        cargarCategorias()
        cargarGastosPorCategoria()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            _categorias.value = repository.getAllCategoriasList()
        }
    }

    private fun cargarGastosPorCategoria() {
        viewModelScope.launch {
            val resumen = repository.getResumenPorCategoria(mesActual)
            _gastosPorCategoria.value = resumen.associate { it.categoriaId to it.total }
        }
    }

    fun agregarPresupuesto(monto: Double, categoriaId: String) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            try {
                val usuario = repository.getUsuario()
                repository.insertPresupuesto(Presupuesto(
                    id = UUID.randomUUID().toString(),
                    montoLimite = monto,
                    mes = mesActual,
                    usuarioId = usuario?.id ?: app.deviceId,
                    categoriaId = categoriaId
                ))
                _operationState.value = UiState.Success("Presupuesto agregado")
                cargarGastosPorCategoria()
            } catch (e: Exception) {
                _operationState.value = UiState.Error("Error al agregar presupuesto")
            }
        }
    }

    fun eliminarPresupuesto(p: Presupuesto) {
        viewModelScope.launch { repository.deletePresupuesto(p) }
    }

    fun resetState() { _operationState.value = UiState.Idle }
}
