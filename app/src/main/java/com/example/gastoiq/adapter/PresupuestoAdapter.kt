package com.example.gastoiq.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.databinding.ItemPresupuestoBinding
import com.example.gastoiq.model.Presupuesto

class PresupuestoAdapter(
    val lista: MutableList<Presupuesto>,
    private val categoriaNombres: Map<String, String>,
    private val categoriaColores: Map<String, String>,
    private val categoriaIconos: Map<String, String>
) : RecyclerView.Adapter<PresupuestoAdapter.ViewHolder>() {

    private var gastosPorCategoria: Map<String, Double> = emptyMap()

    inner class ViewHolder(val binding: ItemPresupuestoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresupuestoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nueva: List<Presupuesto>) {
        lista.clear()
        lista.addAll(nueva)
        notifyDataSetChanged()
    }

    fun actualizarGastos(gastos: Map<String, Double>) {
        gastosPorCategoria = gastos
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        val icono = categoriaIconos[item.categoriaId] ?: ""
        val nombre = categoriaNombres[item.categoriaId] ?: "Otro"
        val color = categoriaColores[item.categoriaId] ?: "#9E9E9E"
        val gastado = gastosPorCategoria[item.categoriaId] ?: 0.0
        val porcentaje = if (item.montoLimite > 0) ((gastado / item.montoLimite) * 100).toInt().coerceAtMost(100) else 0

        holder.binding.tvCategoriaPresupuesto.text = "$icono $nombre"
        holder.binding.tvMontoLimite.text = String.format("Límite: $%.2f", item.montoLimite)
        holder.binding.tvGastado.text = String.format("Gastado: $%.2f", gastado)
        holder.binding.progressPresupuesto.progress = porcentaje
        holder.binding.tvPorcentaje.text = "$porcentaje%"

        val progressColor = when {
            porcentaje >= 90 -> "#F44336"
            porcentaje >= 70 -> "#FF9800"
            else -> "#4CAF50"
        }

        try {
            holder.binding.viewColorPresupuesto.setBackgroundColor(Color.parseColor(color))
            holder.binding.progressPresupuesto.progressTintList = ColorStateList.valueOf(Color.parseColor(progressColor))
        } catch (_: Exception) {}
    }
}
