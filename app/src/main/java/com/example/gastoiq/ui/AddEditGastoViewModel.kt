package com.example.gastoiq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gastoiq.GastoIQApp
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Etiqueta
import com.example.gastoiq.model.Gasto
import com.example.gastoiq.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddEditGastoViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GastoIQApp
    private val repository: AppRepository = app.repository
    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> = _categorias

    private val _etiquetas = MutableLiveData<List<Etiqueta>>()
    val etiquetas: LiveData<List<Etiqueta>> = _etiquetas

    private val _saveState = MutableLiveData<UiState<String>>(UiState.Idle)
    val saveState: LiveData<UiState<String>> = _saveState

    private val _etiquetasSeleccionadas = MutableLiveData<Set<String>>(emptySet())
    val etiquetasSeleccionadas: LiveData<Set<String>> = _etiquetasSeleccionadas

    init {
        viewModelScope.launch {
            _categorias.value = repository.getAllCategoriasList()
            _etiquetas.value = repository.getAllEtiquetasList()
        }
    }

    fun cargarEtiquetasDeGasto(gastoId: String) {
        viewModelScope.launch {
            val tags = repository.getEtiquetasDeGasto(gastoId)
            _etiquetasSeleccionadas.value = tags.map { it.id }.toSet()
        }
    }

    fun toggleEtiqueta(etiquetaId: String) {
        val current = _etiquetasSeleccionadas.value?.toMutableSet() ?: mutableSetOf()
        if (current.contains(etiquetaId)) current.remove(etiquetaId)
        else current.add(etiquetaId)
        _etiquetasSeleccionadas.value = current
    }

    fun guardarGasto(
        editandoId: String?,
        monto: Double,
        descripcion: String,
        notas: String,
        categoriaId: String,
        fechaExistente: String?
    ) {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                val usuario = repository.getUsuario()
                val userId = usuario?.id ?: app.deviceId
                val fechaFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val gasto = Gasto(
                    id = editandoId ?: UUID.randomUUID().toString(),
                    monto = monto,
                    descripcion = descripcion,
                    fecha = fechaExistente ?: fechaFormat.format(Date()),
                    notas = notas,
                    usuarioId = userId,
                    categoriaId = categoriaId
                )
                if (editandoId == null) {
                    repository.insertGasto(gasto)
                } else {
                    repository.updateGasto(gasto)
                }
                // Guardar etiquetas N:M
                repository.setEtiquetasDeGasto(gasto.id, _etiquetasSeleccionadas.value?.toList() ?: emptyList())
                _saveState.value = UiState.Success(if (editandoId == null) "Gasto registrado" else "Gasto actualizado")
            } catch (e: Exception) {
                _saveState.value = UiState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    fun resetState() { _saveState.value = UiState.Idle }
}
