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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.adapter.PresupuestoAdapter
import com.example.gastoiq.databinding.ActivityPresupuestosBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.ui.PresupuestosViewModel
import com.example.gastoiq.utils.UiState
import com.google.android.material.snackbar.Snackbar

class PresupuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestosBinding
    private lateinit var adapter: PresupuestoAdapter
    private val viewModel: PresupuestosViewModel by viewModels()
    private var categorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPresupuestosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = PresupuestoAdapter(mutableListOf(), emptyMap(), emptyMap(), emptyMap())
        binding.rvPresupuestos.adapter = adapter
        binding.rvPresupuestos.layoutManager = LinearLayoutManager(this)

        observeData()
        setupSwipeToDelete()

        binding.btnAgregarPresupuesto.setOnClickListener {
            val montoTexto = binding.etMontoPresupuesto.text.toString()
            val monto = montoTexto.toDoubleOrNull()
            if (monto == null || monto <= 0 || categorias.isEmpty()) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cat = categorias[binding.spCategoriaPresupuesto.selectedItemPosition]
            viewModel.agregarPresupuesto(monto, cat.id)
            binding.etMontoPresupuesto.text?.clear()
        }
    }

    private fun observeData() {
        viewModel.categorias.observe(this) { cats ->
            categorias = cats
            val nombres = cats.map { "${it.icono} ${it.nombre}" }
            binding.spCategoriaPresupuesto.adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, nombres
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            val nombresMap = cats.associate { it.id to it.nombre }
            val coloresMap = cats.associate { it.id to it.color }
            val iconosMap = cats.associate { it.id to it.icono }
            adapter = PresupuestoAdapter(mutableListOf(), nombresMap, coloresMap, iconosMap)
            binding.rvPresupuestos.adapter = adapter

            // Re-observe presupuestos with new adapter
            viewModel.presupuestos.observe(this) { lista ->
                adapter.actualizarLista(lista)
                // Actualizar gastos por cat
                viewModel.gastosPorCategoria.value?.let { gastos ->
                    adapter.actualizarGastos(gastos)
                }
                binding.tvEmptyPresupuestos.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.gastosPorCategoria.observe(this) { gastos ->
            adapter.actualizarGastos(gastos)
        }

        viewModel.operationState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    Snackbar.make(binding.root, state.data, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is UiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                else -> {}
            }
        }
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val item = adapter.lista[vh.adapterPosition]
                viewModel.eliminarPresupuesto(item)
                Snackbar.make(binding.root, "Presupuesto eliminado", Snackbar.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvPresupuestos)
    }
}
