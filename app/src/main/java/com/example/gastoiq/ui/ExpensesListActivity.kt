package com.example.gastoiq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gastoiq.ui.adapter.ExpenseAdapter
import com.example.gastoiq.ui.model.ExpenseUiModel
import com.example.gastoiq.databinding.ActivityExpensesListBinding
import com.example.gastoiq.ui.ExpenseFormActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ExpensesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpensesListBinding
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Gastos"

        adapter = ExpenseAdapter(
            onEdit = { item ->
                val i = Intent(this, ExpenseFormActivity::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("amount", item.amount)        // por ahora manda el string tal cual
                    putExtra("description", item.description)
                    putExtra("category", item.category)
                    putExtra("date", item.date)
                }
                startActivity(i)
            },
            onDelete = { item ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Eliminar gasto")
                    .setMessage("¿Seguro que deseas eliminar este gasto?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        Toast.makeText(this, "Eliminado (demo)", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        )

        binding.rvExpenses.adapter = adapter

        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Abrir formulario (pendiente)", Toast.LENGTH_SHORT).show()
        }

        adapter.submitList(
            listOf(
                ExpenseUiModel(1, "$ 12.50", "Comida", "2026-03-05", "Almuerzo"),
                ExpenseUiModel(2, "$ 5.00", "Transporte", "2026-03-04", "Bus"),
                ExpenseUiModel(3, "$ 20.00", "Hogar", "2026-03-03", "Limpieza")
            )
        )
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ExpenseFormActivity::class.java))
        }
    }
}