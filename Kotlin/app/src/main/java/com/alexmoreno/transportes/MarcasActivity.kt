package com.alexmoreno.transportes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.adaptadores.MarcaAdapter
import com.alexmoreno.transportes.api.ApiClient
import com.alexmoreno.transportes.modelos.Marca
import com.google.android.material.appbar.MaterialToolbar

/**
 * MarcasActivity
 *
 * Actividad principal tras login.
 * Muestra listado de marcas, permite buscar, añadir, editar y eliminar marcas.
 */
class MarcasActivity : AppCompatActivity() {

    private var listaOriginal = listOf<Marca>()
    private lateinit var adapter: MarcaAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var launcherAddMarca: ActivityResultLauncher<Intent>
    private lateinit var toolbar: MaterialToolbar

    private var ordenAscendenteNombre = true
    private var ordenAscendenteAnio = true
    private var ordenAscendenteModelos = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcas)

        // Inicialización de vistas
        recyclerView = findViewById(R.id.recyclerViewMarcas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Listado de Marcas"

        // Resultado para crear o editar marca
        launcherAddMarca = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            cargarMarcas()
        }

        // Inicializar adaptador con callbacks para clic, editar y eliminar
        adapter = MarcaAdapter(
            mutableListOf(),
            onItemClick = { irAMarca(it, true) },
            onEditarClick = { irAMarca(it, false) },
            onEliminarClick = { eliminarMarca(it) }
        )

        recyclerView.adapter = adapter
        cargarMarcas()
    }

    /**
     * Carga las marcas desde la API REST y actualiza la lista.
     */
    private fun cargarMarcas() {
        val token = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token != null) {
            ApiClient.Marcas.obtenerMarcas { success, marcas, error ->
                runOnUiThread {
                    if (success && marcas != null) {
                        listaOriginal = marcas
                        adapter.actualizarLista(listaOriginal)
                    } else {
                        Toast.makeText(this, error ?: "Error al cargar marcas", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "No hay token de sesión", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lanza la MarcaActivity en modo consulta o edición.
     */
    private fun irAMarca(marca: Marca, modoConsulta: Boolean) {
        val intent = Intent(this, MarcaActivity::class.java).apply {
            putExtra("id", marca.id)
            putExtra("modoConsulta", modoConsulta)
        }
        launcherAddMarca.launch(intent)
    }

    /**
     * Muestra un diálogo de confirmación para eliminar una marca.
     */
    private fun eliminarMarca(marca: Marca) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar marca")
            .setMessage("¿Seguro que quieres eliminar esta marca?")
            .setPositiveButton("Sí") { _, _ ->
                ApiClient.Marcas.eliminarMarca(marca.id) { success, error ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Marca eliminada", Toast.LENGTH_SHORT).show()
                            cargarMarcas()
                        } else {
                            Toast.makeText(this, error ?: "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Configura el menú de opciones (buscar, añadir, logout...).
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_marcas, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        // Configuración del buscador
        searchView?.apply {
            queryHint = "Buscar marcas..."
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    val texto = newText?.lowercase()?.trim() ?: ""
                    val filtradas = listaOriginal.filter {
                        it.nombre.lowercase().contains(texto)
                    }
                    adapter.actualizarLista(filtradas)
                    return true
                }
            })
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.sort_nombre)?.title =
            "Nombre ${if (ordenAscendenteNombre) "↑" else "↓"}"

        menu?.findItem(R.id.sort_anio)?.title =
            "Año ${if (ordenAscendenteAnio) "↑" else "↓"}"

        menu?.findItem(R.id.sort_modelos)?.title =
            "Nº Modelos ${if (ordenAscendenteModelos) "↑" else "↓"}"

        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Maneja las acciones del menú superior.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_marca -> {
                val intent = Intent(this, MarcaActivity::class.java).apply {
                    putExtra("id", -1L)
                    putExtra("modoConsulta", false)
                }
                launcherAddMarca.launch(intent)
                return true
            }

            R.id.action_edit_perfil -> {
                Toast.makeText(this, "Editar perfil (no implementado)", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.action_logout -> {
                logout()
                return true
            }

            R.id.sort_nombre -> {
                val ordenado = if (ordenAscendenteNombre) {
                    listaOriginal.sortedBy { it.nombre.lowercase() }
                } else {
                    listaOriginal.sortedByDescending { it.nombre.lowercase() }
                }
                ordenAscendenteNombre = !ordenAscendenteNombre
                adapter.actualizarLista(ordenado)
                invalidateOptionsMenu()
                return true
            }

            R.id.sort_anio -> {
                val ordenado = if (ordenAscendenteAnio) {
                    listaOriginal.sortedBy { it.anioFundacion }
                } else {
                    listaOriginal.sortedByDescending { it.anioFundacion }
                }
                ordenAscendenteAnio = !ordenAscendenteAnio
                adapter.actualizarLista(ordenado)
                invalidateOptionsMenu()
                return true
            }

            R.id.sort_modelos -> {
                val ordenado = if (ordenAscendenteModelos) {
                    listaOriginal.sortedBy { it.modelos.count() }
                } else {
                    listaOriginal.sortedByDescending { it.modelos.count() }
                }
                ordenAscendenteModelos = !ordenAscendenteModelos
                adapter.actualizarLista(ordenado)
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Elimina el token de sesión y vuelve a MainActivity.
     */
    private fun logout() {
        ApiClient.borrarToken();
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
    }
}
