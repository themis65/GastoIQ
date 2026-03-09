package com.example.gastoiq

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gastoiq.databinding.ActivityAddEditBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Gasto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddEditGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
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

        val db = (application as GastoIQApp).database

        // Cargar categorias desde la tabla categorias
        lifecycleScope.launch {
            categorias = db.categoriaDao().getAllList()
            val nombres = categorias.map { it.nombre }
            val adapter = ArrayAdapter(this@AddEditGastoActivity, android.R.layout.simple_spinner_item, nombres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategoria.adapter = adapter

            // Si estamos editando, rellenar campos
            if (intent.hasExtra("EXTRA_GASTO_ID")) {
                editandoId = intent.getStringExtra("EXTRA_GASTO_ID")
                binding.tvTitulo.text = "Editar Gasto"
                binding.etMonto.setText(intent.getDoubleExtra("EXTRA_GASTO_MONTO", 0.0).toString())
                binding.etDescripcion.setText(intent.getStringExtra("EXTRA_GASTO_DESC"))
                binding.etNotas.setText(intent.getStringExtra("EXTRA_GASTO_NOTAS"))
                val catId = intent.getStringExtra("EXTRA_GASTO_CAT_ID")
                val pos = categorias.indexOfFirst { it.id == catId }
                if (pos >= 0) binding.spCategoria.setSelection(pos)
            }
        }

        binding.btnGuardar.setOnClickListener { guardarGasto() }
    }

    private fun guardarGasto() {
        val montoTexto = binding.etMonto.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        val notas = binding.etNotas.text.toString()

        if (montoTexto.isBlank() || descripcion.isBlank()) {
            Toast.makeText(this, "Monto y descripcion son necesarios", Toast.LENGTH_SHORT).show()
            return
        }
        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Monto invalido", Toast.LENGTH_SHORT).show()
            return
        }
        if (categorias.isEmpty()) {
            Toast.makeText(this, "Cargando categorias...", Toast.LENGTH_SHORT).show()
            return
        }

        val catIndex = binding.spCategoria.selectedItemPosition
        val categoriaSeleccionada = categorias[catIndex]
        val db = (application as GastoIQApp).database
        val fechaFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        lifecycleScope.launch {
            val usuario = db.usuarioDao().getUsuario()
            val userId = usuario?.id ?: "user_default"

            val gasto = Gasto(
                id = editandoId ?: UUID.randomUUID().toString(),
                monto = monto,
                descripcion = descripcion,
                fecha = if (editandoId != null) (intent.getStringExtra("EXTRA_GASTO_FECHA") ?: fechaFormat.format(Date())) else fechaFormat.format(Date()),
                notas = notas,
                usuarioId = userId,
                categoriaId = categoriaSeleccionada.id
            )

            if (editandoId == null) {
                db.gastoDao().insert(gasto)
                Toast.makeText(this@AddEditGastoActivity, "Gasto registrado", Toast.LENGTH_SHORT).show()
            } else {
                db.gastoDao().update(gasto)
                Toast.makeText(this@AddEditGastoActivity, "Gasto actualizado", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
