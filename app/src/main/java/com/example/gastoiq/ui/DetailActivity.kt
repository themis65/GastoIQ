package com.example.gastoiq

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gastoiq.databinding.ActivityDetailBinding
import java.text.Normalizer
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        val monto = intent.getDoubleExtra("EXTRA_MONTO", 0.0)
        val desc = intent.getStringExtra("EXTRA_DESC") ?: ""
        val cat = intent.getStringExtra("EXTRA_CAT") ?: ""
        val color = intent.getStringExtra("EXTRA_COLOR") ?: "#9E9E9E"
        val fecha = intent.getStringExtra("EXTRA_FECHA") ?: ""
        val notas = intent.getStringExtra("EXTRA_NOTAS") ?: ""

        binding.tvDetalleMonto.text = "$" + String.format(Locale.US, "%.2f", monto)
        binding.tvDetalleDescripcion.text = desc.ifBlank { "Sin descripción" }
        binding.tvDetalleCategoria.text = "${emojiDeCategoria(cat)} ${cat.ifBlank { "Sin categoría" }}"
        binding.tvDetalleFecha.text = fecha.ifBlank { "Sin fecha" }
        binding.tvDetalleNotas.text = notas.ifBlank { "Sin notas." }

        try {
            binding.viewHeaderColor.setBackgroundColor(Color.parseColor(color))
        } catch (_: Exception) {
            binding.viewHeaderColor.setBackgroundColor(Color.parseColor("#9E9E9E"))
        }

        title = "Detalle"
    }

    private fun emojiDeCategoria(nombre: String): String {
        return when (normalizar(nombre)) {
            "comida", "alimentacion", "alimentos" -> "🍔"
            "compras" -> "🛒"
            "salud", "medicina" -> "💊"
            "transporte", "movilidad" -> "🚌"
            "ocio", "entretenimiento" -> "🎮"
            "educacion", "estudios" -> "📚"
            "servicios" -> "📱"
            "otros" -> "📦"
            else -> "🧾"
        }
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(
            texto.lowercase(Locale.getDefault()),
            Normalizer.Form.NFD
        ).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .trim()
    }
}