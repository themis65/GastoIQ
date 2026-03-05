package com.example.gastoiq.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gastoiq.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Demo: al tocar un mes, solo cambia el resaltado visual (simple)
        val monthButtons = listOf(
            binding.btnAbr, binding.btnMay, binding.btnJun,
            binding.btnJul, binding.btnAgo, binding.btnSep, binding.btnOct
        )

        monthButtons.forEach { btn ->
            btn.setOnClickListener {
                monthButtons.forEach { b ->
                    b.setStrokeColorResource(android.R.color.transparent)
                    b.setTextColor(getColor(com.example.gastoiq.R.color.text_secondary))
                }
                btn.setStrokeColorResource(com.example.gastoiq.R.color.accent)
                btn.setTextColor(getColor(com.example.gastoiq.R.color.text_primary))

                // Por ahora no hay data real
                binding.tvSummary.text = "0 gastos · Total: $0.00"
                binding.tvEmpty.text = "Sin gastos este mes"
            }
        }
    }
}