package com.alexmoreno.transportes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.alexmoreno.transportes.api.ApiClient
import androidx.core.content.edit

/**
 * LoginActivity
 * Pantalla de inicio de sesión para acceder a la aplicación.
 */
class LoginActivity : ComponentActivity() {

    // Lanzador para el registro de nuevos usuarios
    private lateinit var registroLauncher: ActivityResultLauncher<Intent>

    // Referencias a los campos de texto y botones
    private lateinit var inputNombre: EditText
    private lateinit var inputContrasenia: EditText
    private lateinit var btnLogin: Button
    private lateinit var linkRegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        inicializarVista()

        // Configurar evento para botón de login
        btnLogin.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val contrasenia = inputContrasenia.text.toString().trim()

            if (nombre.isNotEmpty() && contrasenia.isNotEmpty()) {
                login(nombre, contrasenia)
            } else {
                mostrarMensaje("Completa todos los campos")
            }
        }

        // Configurar launcher para registro
        registroLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mostrarMensaje("Registro exitoso, ahora inicia sesión")

                // Autocompleta el nombre si se pasa como extra
                val nombre = result.data?.getStringExtra("nombre")
                inputNombre.setText(nombre)
                inputContrasenia.setText("")
            }
        }

        // Redirigir a la pantalla de registro
        linkRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)


            registroLauncher.launch(intent)
        }
    }

    /**
     * Inicializa las vistas de la interfaz.
     */
    private fun inicializarVista() {
        inputNombre = findViewById(R.id.textNombre)
        inputContrasenia = findViewById(R.id.editTextPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        linkRegistro = findViewById(R.id.textViewRegistro)
    }

    /**
     * Realiza la autenticación del usuario.
     */
    private fun login(nombre: String, contrasenia: String) {
        ApiClient.Auth.login(nombre, contrasenia) { success, token, errorMessage ->
            runOnUiThread {
                if (success && token != null) {
                    // Guardar token en preferencias para futuras llamadas autenticadas
                    val prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    prefs.edit { putString("token", token) }

                    // Finalizar con resultado OK
                    setResult(RESULT_OK)
                    finish()
                } else {
                    mostrarMensaje(errorMessage ?: "Error desconocido")
                }
            }
        }
    }

    /**
     * Muestra un mensaje corto en pantalla.
     */
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
