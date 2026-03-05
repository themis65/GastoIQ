package com.example.gastoiq.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gastoiq.R
import com.example.gastoiq.databinding.ActivityExpenseFormBinding

class ExpenseFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseFormBinding

    private var editingId: Long? = null
    private var selectedCategory: String = "Comida"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Extras (modo editar)
        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }
        val amount = intent.getStringExtra("amount")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")

        if (editingId != null) {
            binding.toolbar.setTitle("Editar gasto")
            binding.etAmount.setText(amount ?: "")
            binding.etDescription.setText(description ?: "")
            selectedCategory = category ?: "Comida"
        } else {
            binding.toolbar.setTitle("Nuevo gasto")
        }

        setupCategorySelection(selectedCategory)

        binding.btnSave.setOnClickListener {
            val amountText = binding.etAmount.text?.toString()?.trim().orEmpty()
            val descText = binding.etDescription.text?.toString()?.trim().orEmpty()

            if (amountText.isEmpty()) {
                binding.tilAmount.error = "Ingresa un monto"
                return@setOnClickListener
            } else binding.tilAmount.error = null


            Toast.makeText(
                this,
                if (editingId != null)
                    "Guardado (editar): $selectedCategory"
                else
                    "Guardado (nuevo): $selectedCategory",
                Toast.LENGTH_SHORT
            ).show()

            // Aquí luego se guarda la data con DB: insert/update

            finish()
        }
    }

    private fun setupCategorySelection(initial: String) {
        val items = listOf(
            binding.catFood to "Comida",
            binding.catTransport to "Transporte",
            binding.catLeisure to "Ocio",
            binding.catEducation to "Educación",
            binding.catHealth to "Salud",
            binding.catShopping to "Compras",
            binding.catServices to "Servicios",
            binding.catOther to "Otros"
        )

        fun select(name: String) {
            selectedCategory = name
            items.forEach { (view, label) ->
                val isSelected = label == name
                view.setBackgroundResource(
                    if (isSelected) R.drawable.bg_category_card_selected
                    else R.drawable.bg_category_card
                )

                // 2do hijo = TextView del nombre
                val tv = view.getChildAt(1) as? TextView
                tv?.setTextColor(
                    getColor(if (isSelected) R.color.text_primary else R.color.text_secondary)
                )
            }
        }

        items.forEach { (view, label) ->
            view.setOnClickListener { select(label) }
        }

        select(initial)
    }
}