package com.example.gastoiq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gastoiq.GastoIQApp
import com.example.gastoiq.data.AppRepository
import com.example.gastoiq.data.ResumenCategoria
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EstadisticasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository = (application as GastoIQApp).repository
    val mesActual: String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    private val _resumen = MutableLiveData<List<ResumenCategoria>>()
    val resumen: LiveData<List<ResumenCategoria>> = _resumen

    private val _totalMes = MutableLiveData(0.0)
    val totalMes: LiveData<Double> = _totalMes

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _resumen.value = repository.getResumenPorCategoria(mesActual)
            _totalMes.value = repository.getTotalMes(mesActual)
        }
    }
}
