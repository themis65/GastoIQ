package com.example.gastoiq

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gastoiq.adapter.PresupuestoAdapter
import com.example.gastoiq.databinding.ActivityPresupuestosBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Presupuesto
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PresupuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestosBinding
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

        val db = (application as GastoIQApp).database
        val mesActual = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

        lifecycleScope.launch {
            categorias = db.categoriaDao().getAllList()
            val nombres = categorias.map { it.nombre }
            binding.spCategoriaPresupuesto.adapter = ArrayAdapter(this@PresupuestosActivity, android.R.layout.simple_spinner_item, nombres).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            val nombresMap = categorias.associate { it.id to it.nombre }
            val coloresMap = categorias.associate { it.id to it.color }
            val adapter = PresupuestoAdapter(mutableListOf(), nombresMap, coloresMap)
            binding.rvPresupuestos.adapter = adapter
            binding.rvPresupuestos.layoutManager = LinearLayoutManager(this@PresupuestosActivity)

            db.presupuestoDao().getByMes(mesActual).collect { lista ->
                adapter.actualizarLista(lista)
            }
        }

        binding.btnAgregarPresupuesto.setOnClickListener {
            val montoTexto = binding.etMontoPresupuesto.text.toString()
            val monto = montoTexto.toDoubleOrNull()
            if (monto == null || monto <= 0 || categorias.isEmpty()) {
                Toast.makeText(this, "Ingresa un monto valido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cat = categorias[binding.spCategoriaPresupuesto.selectedItemPosition]
            lifecycleScope.launch {
                val usuario = db.usuarioDao().getUsuario()
                db.presupuestoDao().insert(Presupuesto(
                    id = UUID.randomUUID().toString(),
                    montoLimite = monto,
                    mes = mesActual,
                    usuarioId = usuario?.id ?: "user_default",
                    categoriaId = cat.id
                ))
                binding.etMontoPresupuesto.text?.clear()
                Toast.makeText(this@PresupuestosActivity, "Presupuesto agregado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
