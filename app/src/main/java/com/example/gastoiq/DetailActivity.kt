package com.example.gastoiq

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gastoiq.databinding.ActivityDetailBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        val monto = intent.getDoubleExtra("EXTRA_MONTO", 0.0)
        val desc = intent.getStringExtra("EXTRA_DESC") ?: ""
        val cat = intent.getStringExtra("EXTRA_CAT") ?: ""
        val color = intent.getStringExtra("EXTRA_COLOR") ?: "#9E9E9E"
        val icono = intent.getStringExtra("EXTRA_ICONO") ?: ""
        val fecha = intent.getStringExtra("EXTRA_FECHA") ?: ""
        val notas = intent.getStringExtra("EXTRA_NOTAS") ?: ""
        val gastoId = intent.getStringExtra("EXTRA_ID") ?: ""

        binding.tvDetalleMonto.text = String.format("$%.2f", monto)
        binding.tvDetalleDescripcion.text = desc
        binding.tvDetalleCategoria.text = "$icono $cat"
        binding.tvDetalleFecha.text = fecha
        binding.tvDetalleNotas.text = if (notas.isNotBlank()) notas else "Sin notas."
        try { binding.viewHeaderColor.setBackgroundColor(Color.parseColor(color)) } catch (_: Exception) {}
        binding.toolbar.title = desc
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Cargar etiquetas del gasto
        if (gastoId.isNotBlank()) {
            val repo = (application as GastoIQApp).repository
            lifecycleScope.launch {
                val etiquetas = repo.getEtiquetasDeGasto(gastoId)
                etiquetas.forEach { et ->
                    val chip = Chip(this@DetailActivity).apply {
                        text = et.nombre
                        isClickable = false
                        try { chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor(et.color)) } catch (_: Exception) {}
                        setTextColor(Color.WHITE)
                    }
                    binding.chipGroupEtiquetas.addView(chip)
                }
            }
        }
    }
}
