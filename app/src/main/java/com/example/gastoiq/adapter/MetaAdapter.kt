package com.example.gastoiq.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.databinding.ItemMetaBinding
import com.example.gastoiq.model.MetaAhorro

class MetaAdapter(
    val lista: MutableList<MetaAhorro>,
    private val onAhorroUpdate: (MetaAhorro, Double) -> Unit
) : RecyclerView.Adapter<MetaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMetaBinding) :
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
        val porcentaje = if (item.montoObjetivo > 0)
            ((item.montoActual / item.montoObjetivo) * 100).toInt().coerceAtMost(100) else 0

        holder.binding.tvNombreMeta.text = item.nombre
        holder.binding.tvObjetivoMeta.text = String.format("Objetivo: $%.2f", item.montoObjetivo)
        holder.binding.tvActualMeta.text = String.format("Ahorrado: $%.2f", item.montoActual)
        holder.binding.tvFechaMeta.text = item.fechaLimite
        holder.binding.progressMeta.progress = porcentaje
        holder.binding.tvPorcentajeMeta.text = "$porcentaje%"

        // Color del progress segun porcentaje
        val progressColor = when {
            porcentaje >= 100 -> "#4CAF50"
            porcentaje >= 50 -> "#FF9800"
            else -> "#F44336"
        }
        try {
            holder.binding.progressMeta.progressTintList = ColorStateList.valueOf(Color.parseColor(progressColor))
        } catch (_: Exception) {}

        // Boton para agregar ahorro
        holder.binding.btnAgregarAhorro.setOnClickListener {
            val montoTexto = holder.binding.etAgregarAhorro.text.toString()
            val monto = montoTexto.toDoubleOrNull()
            if (monto != null && monto > 0) {
                val nuevoTotal = item.montoActual + monto
                onAhorroUpdate(item, nuevoTotal)
                holder.binding.etAgregarAhorro.text?.clear()
            } else {
                Toast.makeText(holder.itemView.context, "Monto inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
