package com.example.gastoiq

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gastoiq.databinding.ActivityDetailBinding
import com.example.gastoiq.model.GastoConCategoria

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

        // Recibir datos por extras simples
        val monto = intent.getDoubleExtra("EXTRA_MONTO", 0.0)
        val desc = intent.getStringExtra("EXTRA_DESC") ?: ""
        val cat = intent.getStringExtra("EXTRA_CAT") ?: ""
        val color = intent.getStringExtra("EXTRA_COLOR") ?: "#9E9E9E"
        val fecha = intent.getStringExtra("EXTRA_FECHA") ?: ""
        val notas = intent.getStringExtra("EXTRA_NOTAS") ?: ""

        binding.tvDetalleMonto.text = "$" + String.format("%.2f", monto)
        binding.tvDetalleDescripcion.text = desc
        binding.tvDetalleCategoria.text = cat
        binding.tvDetalleFecha.text = fecha
        binding.tvDetalleNotas.text = if (notas.isNotBlank()) notas else "Sin notas."
        try { binding.viewHeaderColor.setBackgroundColor(Color.parseColor(color)) } catch (e: Exception) {}
        title = "Detalle: $desc"
    }
}
