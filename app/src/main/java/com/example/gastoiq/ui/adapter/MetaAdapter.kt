package com.example.gastoiq.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.databinding.ItemMetaBinding
import com.example.gastoiq.model.MetaAhorro

class MetaAdapter(
    val lista: MutableList<MetaAhorro>
) : RecyclerView.Adapter<MetaAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ItemMetaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMetaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nueva: List<MetaAhorro>) {
        lista.clear()
        lista.addAll(nueva)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvNombreMeta.text = item.nombre
        holder.binding.tvObjetivoMeta.text = "Objetivo: $" + String.format("%.2f", item.montoObjetivo)
        holder.binding.tvActualMeta.text = "Ahorrado: $" + String.format("%.2f", item.montoActual)
        holder.binding.tvFechaMeta.text = "Fecha limite: " + item.fechaLimite

        val porcentaje = if (item.montoObjetivo > 0) ((item.montoActual / item.montoObjetivo) * 100).toInt() else 0
        holder.binding.progressMeta.progress = porcentaje.coerceAtMost(100)
        holder.binding.tvPorcentajeMeta.text = "$porcentaje%"
    }
}
