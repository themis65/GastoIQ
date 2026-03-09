package com.example.gastoiq

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.adapter.PresupuestoAdapter
import com.example.gastoiq.databinding.ActivityPresupuestosBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Presupuesto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PresupuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestosBinding
    private lateinit var adapter: PresupuestoAdapter
    private var categorias: List<Categoria> = emptyList()

    private val db by lazy(LazyThreadSafetyMode.NONE) {
        (application as GastoIQApp).database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPresupuestosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarInsets()
        configurarHeader()
        cargarDatos()
        configurarBotonAgregar()
        setupSwipeToDelete()
    }

    private fun aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }
    }

    private fun configurarHeader() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatos() {
        val mesActual = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

        lifecycleScope.launch {
            try {
                categorias = withContext(Dispatchers.IO) {
                    db.categoriaDao().getAllList()
                }

                configurarSpinnerCategorias()
                configurarRecycler(mesActual)

            } catch (e: Exception) {
                Toast.makeText(
                    this@PresupuestosActivity,
                    "Error al cargar datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun configurarSpinnerCategorias() {
        val nombres = categorias.map { it.nombre }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombres
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spCategoriaPresupuesto.adapter = spinnerAdapter
    }

    private fun configurarRecycler(mesActual: String) {
        val nombresMap = categorias.associate { it.id to it.nombre }
        val coloresMap = categorias.associate { it.id to it.color }

        adapter = PresupuestoAdapter(
            mutableListOf(),
            nombresMap,
            coloresMap
        )

        binding.rvPresupuestos.adapter = adapter
        binding.rvPresupuestos.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            db.presupuestoDao().getByMes(mesActual).collect { lista ->
                adapter.actualizarLista(lista)
            }
        }
    }

    private fun configurarBotonAgregar() {
        binding.btnAgregarPresupuesto.setOnClickListener {
            agregarPresupuesto()
        }
    }

    private fun agregarPresupuesto() {
        val montoTexto = binding.etMontoPresupuesto.text?.toString()?.trim().orEmpty()
        val monto = montoTexto.toDoubleOrNull()

        if (monto == null || monto <= 0.0 || categorias.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val mesActual = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val categoriaSeleccionada = categorias[binding.spCategoriaPresupuesto.selectedItemPosition]

        lifecycleScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    db.usuarioDao().getUsuario()
                }

                val presupuesto = Presupuesto(
                    id = UUID.randomUUID().toString(),
                    montoLimite = monto,
                    mes = mesActual,
                    usuarioId = usuario?.id ?: "user_default",
                    categoriaId = categoriaSeleccionada.id
                )

                withContext(Dispatchers.IO) {
                    db.presupuestoDao().insert(presupuesto)
                }

                binding.etMontoPresupuesto.text?.clear()

                Toast.makeText(
                    this@PresupuestosActivity,
                    "Presupuesto agregado",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@PresupuestosActivity,
                    "No se pudo guardar el presupuesto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (!::adapter.isInitialized) return

                val presupuesto = adapter.lista[viewHolder.adapterPosition]

                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            db.presupuestoDao().delete(presupuesto)
                        }

                        Toast.makeText(
                            this@PresupuestosActivity,
                            "Presupuesto eliminado",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@PresupuestosActivity,
                            "No se pudo eliminar el presupuesto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvPresupuestos)
    }
}