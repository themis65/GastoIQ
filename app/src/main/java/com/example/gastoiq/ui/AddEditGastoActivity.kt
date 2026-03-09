package com.example.gastoiq

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import android.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import com.example.gastoiq.databinding.ActivityAddEditBinding
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Gasto
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddEditGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding

    private var categorias: List<Categoria> = emptyList()
    private var editandoId: String? = null
    private var categoriaSeleccionadaId: String? = null

    private val db by lazy(LazyThreadSafetyMode.NONE) {
        (application as GastoIQApp).database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarInsets()
        configurarPantalla()
        configurarEventos()
        cargarCategorias()
    }

    private fun aplicarInsets() {
        val root = binding.root
        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                initialLeft + bars.left,
                initialTop + bars.top,
                initialRight + bars.right,
                initialBottom + bars.bottom
            )
            insets
        }
    }

    private fun configurarPantalla() {
        val esEdicion = intent.hasExtra("EXTRA_GASTO_ID")
        binding.tvTitulo.text = if (esEdicion) "Editar gasto" else "Nuevo gasto"
        binding.btnGuardar.text = if (esEdicion) "Guardar cambios" else "Registrar gasto"
    }

    private fun configurarEventos() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etMonto.doAfterTextChanged {
            actualizarEstadoBoton()
        }

        binding.btnGuardar.setOnClickListener {
            guardarGasto()
        }
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                categorias = withContext(Dispatchers.IO) {
                    db.categoriaDao().getAllList()
                }

                renderizarCategorias()

                if (intent.hasExtra("EXTRA_GASTO_ID")) {
                    cargarDatosEdicion()
                }

                actualizarEstadoBoton()

            } catch (e: Exception) {
                Toast.makeText(
                    this@AddEditGastoActivity,
                    "Error al cargar categorías",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun renderizarCategorias() {
        binding.gridCategorias.removeAllViews()

        categorias.forEach { categoria ->
            val card = crearCardCategoria(categoria)
            binding.gridCategorias.addView(card)
        }
    }

    private fun crearCardCategoria(categoria: Categoria): MaterialCardView {
        val card = MaterialCardView(this).apply {
            tag = categoria.id
            radius = 18.dp.toFloat()
            strokeWidth = 1.dp
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.card))
            strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
            isClickable = true
            isFocusable = true

            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 110.dp
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(0, 0, 12.dp, 12.dp)
            }
        }

        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val tvEmoji = TextView(this).apply {
            text = obtenerEmojiCategoria(categoria.nombre)
            textSize = 26f
            gravity = Gravity.CENTER
        }

        val tvNombre = TextView(this).apply {
            text = categoria.nombre
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            setPadding(8.dp, 10.dp, 8.dp, 0)
        }

        contenido.addView(tvEmoji)
        contenido.addView(tvNombre)
        card.addView(contenido)

        card.setOnClickListener {
            categoriaSeleccionadaId = categoria.id
            actualizarSeleccionCategorias()
            actualizarEstadoBoton()
        }

        return card
    }

    private fun actualizarSeleccionCategorias() {
        for (i in 0 until binding.gridCategorias.childCount) {
            val card = binding.gridCategorias.getChildAt(i) as? MaterialCardView ?: continue
            val cardCategoriaId = card.tag as? String ?: continue

            val selected = cardCategoriaId == categoriaSeleccionadaId
            val contenido = card.getChildAt(0) as LinearLayout
            val tvNombre = contenido.getChildAt(1) as TextView

            if (selected) {
                card.setCardBackgroundColor(
                    applyAlpha(ContextCompat.getColor(this, R.color.accent), 0.15f)
                )
                card.strokeColor = ContextCompat.getColor(this, R.color.accent)
                card.strokeWidth = 2.dp
                tvNombre.setTextColor(ContextCompat.getColor(this, R.color.accent))
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card))
                card.strokeColor = ContextCompat.getColor(this, R.color.card_stroke)
                card.strokeWidth = 1.dp
                tvNombre.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }

    private fun cargarDatosEdicion() {
        editandoId = intent.getStringExtra("EXTRA_GASTO_ID")

        val monto = intent.getDoubleExtra("EXTRA_GASTO_MONTO", 0.0)
        val descripcion = intent.getStringExtra("EXTRA_GASTO_DESC").orEmpty()
        val notas = intent.getStringExtra("EXTRA_GASTO_NOTAS").orEmpty()
        val categoriaId = intent.getStringExtra("EXTRA_GASTO_CAT_ID")

        binding.etMonto.setText(formatearMonto(monto))
        binding.etDescripcion.setText(
            if (descripcion == "Sin descripción") "" else descripcion
        )
        binding.etNotas.setText(notas)

        categoriaSeleccionadaId = categoriaId
        actualizarSeleccionCategorias()
    }

    private fun guardarGasto() {
        val montoTexto = binding.etMonto.text?.toString()?.trim().orEmpty()
        val descripcionIngresada = binding.etDescripcion.text?.toString()?.trim().orEmpty()
        val notas = binding.etNotas.text?.toString()?.trim().orEmpty()

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaId = categoriaSeleccionadaId
        if (categoriaId.isNullOrBlank()) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        val categoria = categorias.firstOrNull { it.id == categoriaId }
        if (categoria == null) {
            Toast.makeText(this, "Categoría inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val descripcionFinal = descripcionIngresada.ifBlank { "Sin descripción" }
        val fechaFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        lifecycleScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    db.usuarioDao().getUsuario()
                }

                val userId = usuario?.id ?: "user_default"

                val gasto = Gasto(
                    id = editandoId ?: UUID.randomUUID().toString(),
                    monto = monto,
                    descripcion = descripcionFinal,
                    fecha = if (editandoId != null) {
                        intent.getStringExtra("EXTRA_GASTO_FECHA")
                            ?: fechaFormat.format(Date())
                    } else {
                        fechaFormat.format(Date())
                    },
                    notas = notas,
                    usuarioId = userId,
                    categoriaId = categoria.id
                )

                withContext(Dispatchers.IO) {
                    if (editandoId == null) {
                        db.gastoDao().insert(gasto)
                    } else {
                        db.gastoDao().update(gasto)
                    }
                }

                Toast.makeText(
                    this@AddEditGastoActivity,
                    if (editandoId == null) "Gasto registrado" else "Gasto actualizado",
                    Toast.LENGTH_SHORT
                ).show()

                setResult(RESULT_OK)
                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@AddEditGastoActivity,
                    "No se pudo guardar el gasto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun actualizarEstadoBoton() {
        val montoValido = binding.etMonto.text?.toString()?.trim()?.toDoubleOrNull()?.let { it > 0 } == true
        val categoriaElegida = !categoriaSeleccionadaId.isNullOrBlank()
        val habilitado = montoValido && categoriaElegida

        binding.btnGuardar.isEnabled = habilitado
        binding.btnGuardar.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (habilitado) R.color.accent else R.color.card
            )
        )
        binding.btnGuardar.setTextColor(
            ContextCompat.getColor(
                this,
                if (habilitado) R.color.text_primary else R.color.text_secondary
            )
        )
    }

    private fun obtenerEmojiCategoria(nombre: String): String {
        return when (normalizar(nombre)) {
            "comida", "alimentacion", "alimentos" -> "🍔"
            "transporte", "movilidad" -> "🚌"
            "ocio", "entretenimiento" -> "🎮"
            "educacion", "estudios" -> "📚"
            "salud", "medicina" -> "💊"
            "compras" -> "🛒"
            "servicios" -> "📱"
            "otros" -> "📦"
            else -> "🧾"
        }
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .trim()
    }

    private fun formatearMonto(monto: Double): String {
        return BigDecimal.valueOf(monto).stripTrailingZeros().toPlainString()
    }

    private fun applyAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}