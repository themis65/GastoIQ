package com.example.gastoiq

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gastoiq.databinding.ActivityAddEditBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.ui.AddEditGastoViewModel
import com.example.gastoiq.utils.UiState
import com.google.android.material.chip.Chip

class AddEditGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private val viewModel: AddEditGastoViewModel by viewModels()
    private var categorias: List<Categoria> = emptyList()
    private var editandoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        editandoId = intent.getStringExtra("EXTRA_GASTO_ID")

        observeData()

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnGuardar.setOnClickListener { guardarGasto() }
    }

    private fun observeData() {
        viewModel.categorias.observe(this) { cats ->
            categorias = cats
            val nombres = cats.map { "${it.icono} ${it.nombre}" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategoria.adapter = adapter

            // Si estamos editando, rellenar campos
            if (editandoId != null) {
                binding.toolbar.title = "Editar Gasto"
                binding.etMonto.setText(intent.getDoubleExtra("EXTRA_GASTO_MONTO", 0.0).toString())
                binding.etDescripcion.setText(intent.getStringExtra("EXTRA_GASTO_DESC"))
                binding.etNotas.setText(intent.getStringExtra("EXTRA_GASTO_NOTAS"))
                val catId = intent.getStringExtra("EXTRA_GASTO_CAT_ID")
                val pos = categorias.indexOfFirst { it.id == catId }
                if (pos >= 0) binding.spCategoria.setSelection(pos)
                viewModel.cargarEtiquetasDeGasto(editandoId!!)
            }
        }

        viewModel.etiquetas.observe(this) { etiquetas ->
            binding.chipGroupEtiquetas.removeAllViews()
            etiquetas.forEach { et ->
                val chip = Chip(this).apply {
                    text = et.nombre
                    isCheckable = true
                    tag = et.id
                    setOnCheckedChangeListener { _, _ ->
                        viewModel.toggleEtiqueta(et.id)
                    }
                }
                binding.chipGroupEtiquetas.addView(chip)
            }
        }

        viewModel.etiquetasSeleccionadas.observe(this) { selected ->
            for (i in 0 until binding.chipGroupEtiquetas.childCount) {
                val chip = binding.chipGroupEtiquetas.getChildAt(i) as? Chip
                chip?.isChecked = selected.contains(chip?.tag as? String)
            }
        }

        viewModel.saveState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnGuardar.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.data, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGuardar.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is UiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGuardar.isEnabled = true
                }
            }
        }
    }

    private fun guardarGasto() {
        val montoTexto = binding.etMonto.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        val notas = binding.etNotas.text.toString()

        if (montoTexto.isBlank() || descripcion.isBlank()) {
            Toast.makeText(this, "Monto y descripción son necesarios", Toast.LENGTH_SHORT).show()
            return
        }
        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }
        if (categorias.isEmpty()) {
            Toast.makeText(this, "Cargando categorías...", Toast.LENGTH_SHORT).show()
            return
        }

        val catIndex = binding.spCategoria.selectedItemPosition
        val categoriaSeleccionada = categorias[catIndex]

        viewModel.guardarGasto(
            editandoId = editandoId,
            monto = monto,
            descripcion = descripcion,
            notas = notas,
            categoriaId = categoriaSeleccionada.id,
            fechaExistente = if (editandoId != null) intent.getStringExtra("EXTRA_GASTO_FECHA") else null
        )
    }
}
