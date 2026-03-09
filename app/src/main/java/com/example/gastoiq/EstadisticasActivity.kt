package com.example.gastoiq

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gastoiq.databinding.ActivityEstadisticasBinding
import com.example.gastoiq.ui.EstadisticasViewModel

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstadisticasBinding
    private val viewModel: EstadisticasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEstadisticasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        viewModel.totalMes.observe(this) { total ->
            binding.tvTotalGeneral.text = String.format("Total del mes: $%.2f", total)
        }

        viewModel.resumen.observe(this) { lista ->
            binding.layoutCategorias.removeAllViews()
            val total = lista.sumOf { it.total }
            if (total == 0.0) {
                val tv = TextView(this).apply {
                    text = "No hay gastos este mes"
                    gravity = Gravity.CENTER
                    setPadding(0, 48, 0, 48)
                    setTextColor(Color.GRAY)
                    textSize = 16f
                }
                binding.layoutCategorias.addView(tv)
                return@observe
            }

            lista.sortedByDescending { it.total }.forEach { item ->
                val percent = ((item.total / total) * 100).toInt()
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                val header = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

                val colorDot = android.view.View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 16 }
                    try { setBackgroundColor(Color.parseColor(item.categoriaColor)) } catch (_: Exception) {}
                }

                val tvName = TextView(this).apply {
                    text = item.categoriaNombre
                    textSize = 15f
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvAmount = TextView(this).apply {
                    text = String.format("$%.2f (%d%%)", item.total, percent)
                    textSize = 14f
                    setTextColor(Color.parseColor(item.categoriaColor))
                }

                header.addView(colorDot)
                header.addView(tvName)
                header.addView(tvAmount)

                val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = 8 }
                    max = 100
                    progress = percent
                    try {
                        progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(item.categoriaColor))
                    } catch (_: Exception) {}
                }

                row.addView(header)
                row.addView(progressBar)
                binding.layoutCategorias.addView(row)
            }
        }
    }
}
