package com.example.appexamenfinal.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appexamenfinal.databinding.ItemExpenseBinding
import com.example.appexamenfinal.ui.model.ExpenseUiModel

class ExpenseAdapter(
    private val onEdit: (ExpenseUiModel) -> Unit,
    private val onDelete: (ExpenseUiModel) -> Unit
) : ListAdapter<ExpenseUiModel, ExpenseAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExpenseUiModel) {
            binding.tvAmount.text = item.amount
            binding.tvCategoryDate.text = "${item.category} • ${item.date}"
            binding.tvDescription.text = item.description

            binding.btnEdit.setOnClickListener { onEdit(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ExpenseUiModel>() {
            override fun areItemsTheSame(oldItem: ExpenseUiModel, newItem: ExpenseUiModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ExpenseUiModel, newItem: ExpenseUiModel) =
                oldItem == newItem
        }
    }
}