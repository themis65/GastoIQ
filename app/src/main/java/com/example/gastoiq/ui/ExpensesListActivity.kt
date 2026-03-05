package com.example.appexamenfinal.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appexamenfinal.databinding.ActivityExpensesListBinding
import com.example.appexamenfinal.ui.adapter.ExpenseAdapter
import com.example.appexamenfinal.ui.model.ExpenseUiModel
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
            onEdit = { Toast.makeText(this, "Editar: ${it.description}", Toast.LENGTH_SHORT).show() },
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
    }
}