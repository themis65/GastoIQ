package com.example.gastoiq

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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID

class MetasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMetasBinding
    private lateinit var adapter: MetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMetasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }

        val db = (application as GastoIQApp).database
        adapter = MetaAdapter(mutableListOf())
        binding.rvMetas.adapter = adapter
        binding.rvMetas.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            db.metaAhorroDao().getAll().collect { lista ->
                adapter.actualizarLista(lista)
            }
        }

        // Swipe to delete
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val meta = adapter.lista[vh.adapterPosition]
                lifecycleScope.launch { db.metaAhorroDao().delete(meta) }
                Toast.makeText(this@MetasActivity, "Meta eliminada", Toast.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvMetas)

        binding.btnAgregarMeta.setOnClickListener {
            val nombre = binding.etNombreMeta.text.toString()
            val montoTexto = binding.etMontoMeta.text.toString()
            val fecha = binding.etFechaMeta.text.toString()
            val monto = montoTexto.toDoubleOrNull()

            if (nombre.isBlank() || monto == null || monto <= 0 || fecha.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = db.usuarioDao().getUsuario()
                db.metaAhorroDao().insert(MetaAhorro(
                    id = UUID.randomUUID().toString(),
                    nombre = nombre,
                    montoObjetivo = monto,
                    montoActual = 0.0,
                    fechaLimite = fecha,
                    usuarioId = usuario?.id ?: "user_default"
                ))
                binding.etNombreMeta.text?.clear()
                binding.etMontoMeta.text?.clear()
                binding.etFechaMeta.text?.clear()
                Toast.makeText(this@MetasActivity, "Meta creada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
