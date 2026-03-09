package com.example.gastoiq

import android.content.Intent
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
import com.example.gastoiq.adapter.GastoAdapter
import com.example.gastoiq.databinding.ActivityMainBinding
import com.example.gastoiq.ui.MainViewModel
import com.example.gastoiq.utils.UiState
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GastoAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        setupRecyclerView()
        setupSwipeToDelete()
        observeData()

        binding.btnSincronizar.setOnClickListener { viewModel.sincronizar() }
        binding.btnPresupuestos.setOnClickListener {
            startActivity(Intent(this, PresupuestosActivity::class.java))
        }
        binding.btnMetas.setOnClickListener {
            startActivity(Intent(this, MetasActivity::class.java))
        }
        binding.btnEstadisticas.setOnClickListener {
            startActivity(Intent(this, EstadisticasActivity::class.java))
        }
        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, AddEditGastoActivity::class.java))
        }
    }

    private fun observeData() {
        viewModel.allGastos.observe(this) { lista ->
            adapter.actualizarLista(lista)
            binding.tvEmptyState.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            binding.rvGastos.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.totalMes.observe(this) { total ->
            binding.tvTotalMes.text = String.format("$%.2f", total)
        }

        viewModel.isOnline.observe(this) { online ->
            binding.chipConexion.apply {
                text = if (online) "En línea" else "Sin conexión"
                setChipIconResource(
                    if (online) android.R.drawable.presence_online
                    else android.R.drawable.presence_offline
                )
            }
        }

        viewModel.syncState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressSync.visibility = View.VISIBLE
                    binding.btnSincronizar.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressSync.visibility = View.GONE
                    binding.btnSincronizar.isEnabled = true
                    Snackbar.make(binding.root, state.data, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetSyncState()
                }
                is UiState.Error -> {
                    binding.progressSync.visibility = View.GONE
                    binding.btnSincronizar.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.red_alert))
                        .show()
                    viewModel.resetSyncState()
                }
                is UiState.Idle -> {
                    binding.progressSync.visibility = View.GONE
                    binding.btnSincronizar.isEnabled = true
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = GastoAdapter(mutableListOf(),
            onItemClick = { item ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("EXTRA_MONTO", item.monto)
                    putExtra("EXTRA_DESC", item.descripcion)
                    putExtra("EXTRA_CAT", item.categoriaNombre)
                    putExtra("EXTRA_COLOR", item.categoriaColor)
                    putExtra("EXTRA_ICONO", item.categoriaIcono)
                    putExtra("EXTRA_FECHA", item.fecha)
                    putExtra("EXTRA_NOTAS", item.notas)
                    putExtra("EXTRA_ID", item.id)
                }
                startActivity(intent)
            },
            onEditClick = { item ->
                val intent = Intent(this, AddEditGastoActivity::class.java).apply {
                    putExtra("EXTRA_GASTO_ID", item.id)
                    putExtra("EXTRA_GASTO_MONTO", item.monto)
                    putExtra("EXTRA_GASTO_DESC", item.descripcion)
                    putExtra("EXTRA_GASTO_FECHA", item.fecha)
                    putExtra("EXTRA_GASTO_NOTAS", item.notas)
                    putExtra("EXTRA_GASTO_CAT_ID", item.categoriaId)
                }
                startActivity(intent)
            }
        )
        binding.rvGastos.adapter = adapter
        binding.rvGastos.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val item = adapter.listaGastos[vh.adapterPosition]
                viewModel.deleteGasto(item)
                Snackbar.make(binding.root, "Gasto eliminado", Snackbar.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvGastos)
    }
}
