package com.example.gastoiq.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gastoiq.databinding.ActivityExpenseFormBinding
import java.util.Calendar

class ExpenseFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseFormBinding

    private var selectedDate: String = "-"
    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Categorías demo (luego se llenará desde DB)
        val categories = listOf("Comida", "Transporte", "Hogar", "Salud", "Otros")
        binding.actvCategory.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        )

        // Si viene en modo editar
        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }
        val amount = intent.getStringExtra("amount")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")
        val date = intent.getStringExtra("date")

        if (editingId != null) {
            binding.toolbar.title = "Editar gasto"
            binding.etAmount.setText(amount ?: "")
            binding.etDescription.setText(description ?: "")
            binding.actvCategory.setText(category ?: "", false)
            selectedDate = date ?: "-"
            binding.tvDate.text = "Fecha: $selectedDate"
        } else {
            binding.toolbar.title = "Nuevo gasto"
        }

        binding.btnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val month = (m + 1).toString().padStart(2, '0')
                    val day = d.toString().padStart(2, '0')
                    selectedDate = "$y-$month-$day"
                    binding.tvDate.text = "Fecha: $selectedDate"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            val amountText = binding.etAmount.text?.toString()?.trim().orEmpty()
            val descText = binding.etDescription.text?.toString()?.trim().orEmpty()
            val catText = binding.actvCategory.text?.toString()?.trim().orEmpty()

            if (amountText.isEmpty()) {
                binding.tilAmount.error = "Ingresa un monto"
                return@setOnClickListener
            } else binding.tilAmount.error = null

            if (catText.isEmpty()) {
                binding.tilCategory.error = "Selecciona una categoría"
                return@setOnClickListener
            } else binding.tilCategory.error = null

            Toast.makeText(
                this,
                if (editingId != null) "Guardado (editar - demo)" else "Guardado (nuevo - demo)",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}