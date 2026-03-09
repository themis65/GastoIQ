package com.example.gastoiq.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.databinding.ItemExpenseBinding
import com.example.gastoiq.model.GastoConCategoria

class GastoAdapter(
    val listaGastos: MutableList<GastoConCategoria>,
    private val onItemSelected: (GastoConCategoria) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    inner class GastoViewHolder(var binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GastoViewHolder(binding)
    }

    override fun getItemCount(): Int = listaGastos.size

    fun actualizarLista(nuevaLista: List<GastoConCategoria>) {
        listaGastos.clear()
        listaGastos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val item = listaGastos[position]
        holder.binding.tvDescripcion.text = item.descripcion
        holder.binding.tvCategoria.text = item.categoriaNombre
        holder.binding.tvMonto.text = "-$" + String.format("%.2f", item.monto)

        try {
            holder.binding.viewCategoryColor.setBackgroundColor(Color.parseColor(item.categoriaColor))
        } catch (e: Exception) {
            holder.binding.viewCategoryColor.setBackgroundColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener { onItemSelected(item) }
    }
}
