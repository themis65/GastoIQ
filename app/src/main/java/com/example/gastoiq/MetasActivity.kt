package com.example.gastoiq

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.adapter.MetaAdapter
import com.example.gastoiq.databinding.ActivityMetasBinding
import com.example.gastoiq.ui.MetasViewModel
import com.example.gastoiq.utils.UiState
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MetasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMetasBinding
    private lateinit var adapter: MetaAdapter
    private val viewModel: MetasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMetasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = MetaAdapter(mutableListOf()) { meta, nuevoMonto ->
            viewModel.actualizarAhorro(meta, nuevoMonto)
        }
        binding.rvMetas.adapter = adapter
        binding.rvMetas.layoutManager = LinearLayoutManager(this)

        // Date picker para fecha limite
        binding.etFechaMeta.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                binding.etFechaMeta.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        observeData()
        setupSwipeToDelete()

        binding.btnAgregarMeta.setOnClickListener {
            val nombre = binding.etNombreMeta.text.toString()
            val montoTexto = binding.etMontoMeta.text.toString()
            val fecha = binding.etFechaMeta.text.toString()
            val monto = montoTexto.toDoubleOrNull()

            if (nombre.isBlank() || monto == null || monto <= 0 || fecha.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.crearMeta(nombre, monto, fecha)
            binding.etNombreMeta.text?.clear()
            binding.etMontoMeta.text?.clear()
            binding.etFechaMeta.text?.clear()
        }
    }

    private fun observeData() {
        viewModel.allMetas.observe(this) { lista ->
            adapter.actualizarLista(lista)
            binding.tvEmptyMetas.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
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
                val meta = adapter.lista[vh.adapterPosition]
                viewModel.eliminarMeta(meta)
                Snackbar.make(binding.root, "Meta eliminada", Snackbar.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvMetas)
    }
}
