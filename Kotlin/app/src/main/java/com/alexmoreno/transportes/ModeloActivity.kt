package com.alexmoreno.transportes

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alexmoreno.transportes.api.ApiClient
import com.alexmoreno.transportes.modelos.Color

/**
 * ModeloActivity
 *
 * Permite crear, editar o consultar un modelo de vehículo.
 * La interfaz y las acciones se adaptan según el modoConsulta (true = solo lectura).
 */
class ModeloActivity : AppCompatActivity() {

    private var idModelo: Long = -1L
    private var idMarca: Long = -1L
    private var modoConsulta: Boolean = true
    private var selectedColorId: Long = -1L
    private val colores = mutableListOf<Color>()

    // UI components
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextPrecioBase: EditText
    private lateinit var spinnerColor: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelo)

        // Obtener parámetros de entrada
        idModelo = intent.getLongExtra("id", -1L)
        idMarca = intent.getLongExtra("idMarca", -1L)
        modoConsulta = intent.getBooleanExtra("modoConsulta", true)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarModelo)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when {
            modoConsulta -> "Consulta Modelo"
            idModelo != -1L -> "Edición Modelo"
            else -> "Creación Modelo"
        }

        // Referencias UI
        editTextNombre = findViewById(R.id.editTextNombreModelo)
        editTextDescripcion = findViewById(R.id.editTextDescripcionModelo)
        editTextPrecioBase = findViewById(R.id.editTextPrecioModelo)
        spinnerColor = findViewById(R.id.spinnerColor)

        // Bloquear edición si es solo consulta
        if (modoConsulta) {
            editTextNombre.isEnabled = false
            editTextDescripcion.isEnabled = false
            editTextPrecioBase.isEnabled = false
            spinnerColor.isEnabled = false
        }

        // Obtener lista de colores y cargar modelo si es edición
        obtenerColores {
            if (idModelo != -1L) {
                ApiClient.Modelos.obtenerModeloPorId(idModelo) { success, modelo, error ->
                    runOnUiThread {
                        if (success && modelo != null) {
                            editTextNombre.setText(modelo.nombre)
                            editTextDescripcion.setText(modelo.descripcion)
                            editTextPrecioBase.setText(modelo.precioBase.toString())
                            idMarca = modelo.marcaId
                            selectedColorId = modelo.colorId

                            val index = colores.indexOfFirst { it.id == selectedColorId }
                            if (index != -1) spinnerColor.setSelection(index)
                        } else {
                            Toast.makeText(this, error ?: "Error al cargar modelo", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    /**
     * Menú superior con opciones de guardar, eliminar y cancelar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!modoConsulta) {
            menuInflater.inflate(R.menu.menu_modelo, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_guardar -> {
                guardarModelo()
                true
            }
            R.id.action_eliminar -> {
                eliminarModelo()
                true
            }
            R.id.action_cancelar, android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Guarda el modelo (creación o actualización)
     */
    private fun guardarModelo() {
        val nombre = editTextNombre.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val precioBase = editTextPrecioBase.text.toString().toDoubleOrNull()
        val selectedIndex = spinnerColor.selectedItemPosition

        // Validación de campos
        if (nombre.isEmpty() || descripcion.isEmpty() || precioBase == null || idMarca == -1L || selectedIndex == -1) {
            Toast.makeText(this, "Rellena todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val idColor = colores[selectedIndex].id

        if (idModelo != -1L) {
            // Actualizar modelo existente
            ApiClient.Modelos.actualizarModelo(idModelo, nombre, descripcion, precioBase, idColor) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Modelo actualizado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Crear nuevo modelo
            ApiClient.Modelos.crearModelo(idMarca, idColor, nombre, descripcion, precioBase) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Modelo creado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Error al crear", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Elimina el modelo si ya existe en el sistema
     */
    private fun eliminarModelo() {
        if (idModelo == -1L) {
            Toast.makeText(this, "No se puede eliminar: modelo no guardado aún", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar modelo")
            .setMessage("¿Seguro que deseas eliminar este modelo?")
            .setPositiveButton("Sí") { _, _ ->
                ApiClient.Modelos.eliminarModelo(idModelo) { success, error ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Modelo eliminado", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this, error ?: "Error al eliminar modelo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Carga los colores disponibles desde el backend y configura el spinner
     */
    private fun obtenerColores(onLoaded: () -> Unit) {
        ApiClient.Colores.obtenerColores { success, listaColores, error ->
            runOnUiThread {
                if (success && listaColores != null) {
                    colores.clear()
                    colores.addAll(listaColores)
                    val adapter = ArrayAdapter(
                        this,
                        R.layout.spinner_item, // layout para ítem seleccionado
                        colores.map { it.descripcion }
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // layout para dropdown
                    spinnerColor.adapter = adapter

                    onLoaded()
                } else {
                    Toast.makeText(this, error ?: "Error al obtener colores", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
