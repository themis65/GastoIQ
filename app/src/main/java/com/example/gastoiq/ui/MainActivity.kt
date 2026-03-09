package com.example.gastoiq

import android.content.Intent
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GastoAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSwipeToDelete()

        viewModel.allGastos.observe(this) { lista ->
            adapter.actualizarLista(lista)
            val total = lista.sumOf { it.monto }
            binding.tvTotalMes.text = "Total del mes: $" + String.format("%.2f", total)
        }

        viewModel.syncStatus.observe(this) { status ->
            when (status) {
                "CARGANDO" -> { binding.btnSincronizar.isEnabled = false; binding.btnSincronizar.text = "Subiendo..." }
                "EXITO" -> { binding.btnSincronizar.isEnabled = true; binding.btnSincronizar.text = "Subir"; Toast.makeText(this, "Sincronizacion exitosa", Toast.LENGTH_SHORT).show() }
                "ERROR" -> { binding.btnSincronizar.isEnabled = true; binding.btnSincronizar.text = "Subir"; Toast.makeText(this, "Error al sincronizar", Toast.LENGTH_SHORT).show() }
            }
        }

        binding.btnSincronizar.setOnClickListener { viewModel.iniciarSincronizacion() }
        binding.btnPresupuestos.setOnClickListener { startActivity(Intent(this, PresupuestosActivity::class.java)) }
        binding.btnMetas.setOnClickListener { startActivity(Intent(this, MetasActivity::class.java)) }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }
    }

    private fun setupRecyclerView() {
        adapter = GastoAdapter(mutableListOf()) { item ->
            val intent = Intent(this, AddEditGastoActivity::class.java)
            intent.putExtra("EXTRA_GASTO_ID", item.id)
            intent.putExtra("EXTRA_GASTO_MONTO", item.monto)
            intent.putExtra("EXTRA_GASTO_DESC", item.descripcion)
            intent.putExtra("EXTRA_GASTO_FECHA", item.fecha)
            intent.putExtra("EXTRA_GASTO_NOTAS", item.notas)
            intent.putExtra("EXTRA_GASTO_CAT_ID", item.categoriaId)
            intent.putExtra("EXTRA_GASTO_USER_ID", item.usuarioId)
            startActivity(intent)
        }
        binding.rvGastos.adapter = adapter
        binding.rvGastos.layoutManager = LinearLayoutManager(this)
        binding.fabAgregar.setOnClickListener { startActivity(Intent(this, AddEditGastoActivity::class.java)) }
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val item = adapter.listaGastos[vh.adapterPosition]
                viewModel.deleteGasto(item)
                Toast.makeText(this@MainActivity, "Gasto eliminado", Toast.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvGastos)
    }
}
