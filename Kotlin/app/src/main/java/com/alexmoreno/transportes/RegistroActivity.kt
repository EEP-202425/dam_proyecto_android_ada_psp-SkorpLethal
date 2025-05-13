package com.alexmoreno.transportes

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.alexmoreno.transportes.api.ApiClient

/**
 * RegistroActivity
 * Pantalla para registrar un nuevo usuario.
 */
class RegistroActivity : ComponentActivity() {

    // Referencias a los elementos de la vista
    private lateinit var inputNombre: EditText
    private lateinit var inputContrasena: EditText
    private lateinit var inputRepetirContrasena: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var linkVolverLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializamos las vistas
        inicializarVista()

        // Evento: Volver al LoginActivity
        linkVolverLogin.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // Evento: Registrar usuario
        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    /**
     * Asocia los elementos visuales a sus variables.
     */
    private fun inicializarVista() {
        inputNombre = findViewById(R.id.textNombre)
        inputContrasena = findViewById(R.id.editTextPassword)
        inputRepetirContrasena = findViewById(R.id.editTextPasswordRepeat)
        btnRegistrar = findViewById(R.id.buttonLogin)
        linkVolverLogin = findViewById(R.id.textViewLogin)
    }

    /**
     * Valida los campos y realiza el registro de usuario usando la API.
     */
    private fun registrarUsuario() {
        val nombre = inputNombre.text.toString().trim()
        val contrasena = inputContrasena.text.toString().trim()
        val repetir = inputRepetirContrasena.text.toString().trim()

        // Validaciones básicas
        when {
            nombre.isEmpty() || contrasena.isEmpty() || repetir.isEmpty() -> {
                mostrarMensaje("Completa todos los campos")
                return
            }
            contrasena != repetir -> {
                mostrarMensaje("Las contraseñas no coinciden")
                return
            }
        }

        // Llamada a la API de registro
        ApiClient.Auth.registrarUsuario(nombre, contrasena) { success, errorMessage ->
            runOnUiThread {
                if (success) {
                    mostrarMensaje("Usuario registrado correctamente")
                    val resultIntent = Intent().apply {
                        putExtra("nombre", nombre)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    mostrarMensaje(errorMessage ?: "Error al registrar")
                }
            }
        }
    }

    /**
     * Muestra un mensaje corto por pantalla.
     */
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
