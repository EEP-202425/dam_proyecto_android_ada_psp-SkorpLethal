package com.alexmoreno.transportes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.adaptadores.ModeloAdapter
import com.alexmoreno.transportes.api.ApiClient
import com.alexmoreno.transportes.modelos.Modelo
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class MarcaActivity : AppCompatActivity() {

    private var modelosOriginales = listOf<Modelo>()
    private lateinit var adapter: ModeloAdapter

    private lateinit var launcherAddModelo: ActivityResultLauncher<Intent>

    private var idMarca: Long = -1L
    private var modoConsulta: Boolean = true

    private lateinit var recyclerModelos: RecyclerView
    private lateinit var inputNombre: TextInputEditText
    private lateinit var inputDescripcion: TextInputEditText
    private lateinit var inputImagen: TextInputEditText
    private lateinit var inputAnio: TextInputEditText
    private lateinit var toolbar: MaterialToolbar



    private var ordenAscNombre = true
    private var ordenAscPrecio = true
    private var ordenAscTotal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marca)

        inputNombre = findViewById(R.id.editTextNombreMarca)
        inputDescripcion = findViewById(R.id.editTextDescripcionMarca)
        inputImagen = findViewById(R.id.editTextImagenMarca)
        inputAnio = findViewById(R.id.editTextAnioFundacion)
        recyclerModelos = findViewById(R.id.recyclerViewModelos)
        toolbar = findViewById(R.id.toolbarMarca)

        idMarca = intent.getLongExtra("id", -1L)
        modoConsulta = intent.getBooleanExtra("modoConsulta", true)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when {
            modoConsulta -> "Consulta Marca"
            idMarca != -1L -> "Editar Marca"
            else -> "Nueva Marca"
        }


        if (idMarca != -1L) cargarMarca()

        if (modoConsulta) {
            inputNombre.isEnabled = false
            inputDescripcion.isEnabled = false
            inputImagen.isEnabled = false
            inputAnio.isEnabled = false
        }

        launcherAddModelo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) cargarModelos()
        }

        adapter =  ModeloAdapter(
            mutableListOf(),
            modoConsulta,
            onItemClick = { irAModelo(it, true) },
            onEditarClick = { irAModelo(it, false) },
            onEliminarClick = { eliminarModelo(it) }
        )

        recyclerModelos.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (modoConsulta) return false
        menuInflater.inflate(R.menu.menu_marca, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home, R.id.action_cancelar -> {
                finish()
                return true
            }
            R.id.action_guardar -> guardarMarca()
            R.id.action_eliminar -> eliminarMarca()
            R.id.action_nuevo_modelo -> crearModelo()
            R.id.sort_nombre -> {
                val sorted = if (ordenAscNombre) modelosOriginales.sortedBy { it.nombre.lowercase() }
                else modelosOriginales.sortedByDescending { it.nombre.lowercase() }

                ordenAscNombre = !ordenAscNombre
                adapter.actualizarLista(sorted)
                invalidateOptionsMenu()
                return true
            }
            R.id.sort_precioBase -> {
                val sorted = if (ordenAscPrecio) modelosOriginales.sortedBy { it.precioBase }
                else modelosOriginales.sortedByDescending { it.precioBase }

                ordenAscPrecio = !ordenAscPrecio
                adapter.actualizarLista(sorted)
                invalidateOptionsMenu()
                return true
            }
            R.id.sort_precioTotal -> {
                val sorted = if (ordenAscTotal) modelosOriginales.sortedBy { it.precioBase * 1.21 }
                else modelosOriginales.sortedByDescending { it.precioBase * 1.21 }

                ordenAscTotal = !ordenAscTotal
                adapter.actualizarLista(sorted)
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.sort_nombre)?.title = "Nombre ${if (ordenAscNombre) "↑" else "↓"}"
        menu?.findItem(R.id.sort_precioBase)?.title = "Precio Base ${if (ordenAscPrecio) "↑" else "↓"}"
        menu?.findItem(R.id.sort_precioTotal)?.title = "Precio Total ${if (ordenAscTotal) "↑" else "↓"}"
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Guarda una nueva marca o actualiza una existente.
     */
    private fun guardarMarca(): Boolean {
        if (modoConsulta) return true

        val nombre = inputNombre.text.toString().trim()
        val descripcion = inputDescripcion.text.toString().trim()
        val imagen = inputImagen.text.toString().trim()
        val anioTexto = inputAnio.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || imagen.isEmpty() || anioTexto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return true
        }

        val anioFundacion = anioTexto.toIntOrNull()
        if (anioFundacion == null) {
            Toast.makeText(this, "Año de fundación inválido", Toast.LENGTH_SHORT).show()
            return true
        }

        val existe = idMarca != -1L

        if (existe) {
            // Actualizar marca
            ApiClient.Marcas.actualizarMarca(idMarca, nombre, descripcion, imagen, anioFundacion) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Marca actualizada", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Error al actualizar marca", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Crear nueva marca
            ApiClient.Marcas.crearMarca(nombre, descripcion, imagen, anioFundacion) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Marca creada", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Error al crear marca", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return true
    }

    /**
     * Muestra diálogo para confirmar la eliminación de la marca.
     */
    private fun eliminarMarca(): Boolean {
        if (!modoConsulta && idMarca != -1L) {
            AlertDialog.Builder(this)
                .setTitle("Eliminar marca")
                .setMessage("¿Seguro que quieres eliminar esta marca?")
                .setPositiveButton("Sí") { _, _ ->
                    ApiClient.Marcas.eliminarMarca(idMarca) { success, error ->
                        runOnUiThread {
                            if (success) {
                                Toast.makeText(this, "Marca eliminada", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(this, error ?: "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        return true
    }

    /**
     * Lanza la creación de un nuevo modelo para esta marca.
     */
    private fun crearModelo(): Boolean {
        if (!modoConsulta && idMarca != -1L) {
            val intent = Intent(this, ModeloActivity::class.java).apply {
                putExtra("idMarca", idMarca)
                putExtra("modoConsulta", false)
            }
            launcherAddModelo.launch(intent)
        }
        return true
    }

    private fun cargarMarca() {
        ApiClient.Marcas.obtenerMarcaPorId(idMarca) { success, marca, error ->
            runOnUiThread {
                if (success && marca != null) {
                    inputNombre.setText(marca.nombre)
                    inputDescripcion.setText(marca.descripcion)
                    inputImagen.setText(marca.imagen)
                    inputAnio.setText(marca.anioFundacion.toString())

                    recyclerModelos.layoutManager = LinearLayoutManager(this)
                    cargarModelosEnAdapter(marca.modelos)
                } else {
                    Toast.makeText(this, error ?: "Error al cargar marca", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarModelos() {
        ApiClient.Marcas.obtenerMarcaPorId(idMarca) { success, marca, error ->
            runOnUiThread {
                if (success && marca != null) {
                    cargarModelosEnAdapter(marca.modelos)
                } else {
                    Toast.makeText(this, error ?: "Error al recargar modelos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarModelosEnAdapter(modelos: List<Modelo>) {
        modelosOriginales = modelos
        adapter.actualizarLista(modelos)
        recyclerModelos.adapter = adapter
    }

    private fun irAModelo(modelo: Modelo, modoConsulta: Boolean) {
        val intent = Intent(this, ModeloActivity::class.java).apply {
            putExtra("id", modelo.id)
            putExtra("modoConsulta", modoConsulta)
        }
        launcherAddModelo.launch(intent)
    }

    private fun eliminarModelo(modelo: Modelo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar modelo")
            .setMessage("¿Eliminar modelo '${modelo.nombre}'?")
            .setPositiveButton("Sí") { _, _ ->
                ApiClient.Modelos.eliminarModelo(modelo.id) { success, error ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Modelo eliminado", Toast.LENGTH_SHORT).show()
                            cargarModelos()
                        } else {
                            Toast.makeText(this, error ?: "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
