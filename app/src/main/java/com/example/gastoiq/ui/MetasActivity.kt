package com.example.gastoiq

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gastoiq.adapter.MetaAdapter
import com.example.gastoiq.databinding.ActivityMetasBinding
import com.example.gastoiq.model.MetaAhorro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class MetasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMetasBinding
    private lateinit var adapter: MetaAdapter

    private val db by lazy(LazyThreadSafetyMode.NONE) {
        (application as GastoIQApp).database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMetasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarInsets()
        configurarHeader()
        configurarRecycler()
        observarMetas()
        configurarSwipeToDelete()
        configurarFecha()
        configurarBotonCrear()
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

    private fun configurarRecycler() {
        adapter = MetaAdapter(mutableListOf())
        binding.rvMetas.adapter = adapter
        binding.rvMetas.layoutManager = LinearLayoutManager(this)
    }

    private fun observarMetas() {
        lifecycleScope.launch {
            db.metaAhorroDao().getAll().collect { lista ->
                adapter.actualizarLista(lista)
            }
        }
    }

    private fun configurarSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                r: RecyclerView,
                v: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val meta = adapter.lista[vh.adapterPosition]

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.metaAhorroDao().delete(meta)
                    }
                }

                Toast.makeText(this@MetasActivity, "Meta eliminada", Toast.LENGTH_SHORT).show()
            }
        }

        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvMetas)
    }

    private fun configurarFecha() {
        binding.etFechaMeta.setOnClickListener {
            mostrarDatePicker()
        }

        binding.etFechaMeta.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mostrarDatePicker()
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fecha = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etFechaMeta.setText(formato.format(fecha.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun configurarBotonCrear() {
        binding.btnAgregarMeta.setOnClickListener {
            crearMeta()
        }
    }

    private fun crearMeta() {
        val nombre = binding.etNombreMeta.text?.toString()?.trim().orEmpty()
        val montoTexto = binding.etMontoMeta.text?.toString()?.trim().orEmpty()
        val fecha = binding.etFechaMeta.text?.toString()?.trim().orEmpty()
        val monto = montoTexto.toDoubleOrNull()

        if (nombre.isBlank() || monto == null || monto <= 0.0 || fecha.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    db.usuarioDao().getUsuario()
                }

                val nuevaMeta = MetaAhorro(
                    id = UUID.randomUUID().toString(),
                    nombre = nombre,
                    montoObjetivo = monto,
                    montoActual = 0.0,
                    fechaLimite = fecha,
                    usuarioId = usuario?.id ?: "user_default"
                )

                withContext(Dispatchers.IO) {
                    db.metaAhorroDao().insert(nuevaMeta)
                }

                limpiarFormulario()

                Toast.makeText(
                    this@MetasActivity,
                    "Meta creada",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@MetasActivity,
                    "No se pudo crear la meta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun limpiarFormulario() {
        binding.etNombreMeta.text?.clear()
        binding.etMontoMeta.text?.clear()
        binding.etFechaMeta.text?.clear()
    }
}