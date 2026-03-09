package com.example.gastoiq.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.databinding.ItemPresupuestoBinding
import com.example.gastoiq.model.Presupuesto

class PresupuestoAdapter(
    val lista: MutableList<Presupuesto>,
    private val categoriaNombres: Map<String, String>,
    private val categoriaColores: Map<String, String>
) : RecyclerView.Adapter<PresupuestoAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ItemPresupuestoBinding) :
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvCategoriaPresupuesto.text = categoriaNombres[item.categoriaId] ?: "Otro"
        holder.binding.tvMontoLimite.text = "Limite: $" + String.format("%.2f", item.montoLimite)
        holder.binding.tvMesPresupuesto.text = item.mes
        try {
            val color = categoriaColores[item.categoriaId] ?: "#9E9E9E"
            holder.binding.viewColorPresupuesto.setBackgroundColor(Color.parseColor(color))
        } catch (e: Exception) {
            holder.binding.viewColorPresupuesto.setBackgroundColor(Color.GRAY)
        }
    }
}
