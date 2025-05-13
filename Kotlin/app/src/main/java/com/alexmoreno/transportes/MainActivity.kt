package com.alexmoreno.transportes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.alexmoreno.transportes.api.ApiClient

/**
 * MainActivity
 * Punto de entrada de la aplicación. Verifica si el usuario ya tiene un token válido.
 * - Si es así, accede directamente a MarcasActivity.
 * - Si no, lanza la pantalla de login.
 */
class MainActivity : ComponentActivity() {

    // Lanzador para iniciar LoginActivity y capturar resultado
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el cliente de la API con el contexto global
        ApiClient.init(applicationContext)
        val token = ApiClient.obtenerToken()

        if (token != null) {
            // Si hay token, validarlo contra la API

            ApiClient.Auth.validarToken { esValido, errorMessage ->
                runOnUiThread {
                    if (esValido) {
                        navegarAMarcas()
                    } else {
                        mostrarMensaje(errorMessage ?: "Token inválido o expirado")
                        lanzarLogin()
                    }
                }
            }
        } else {
            // Si no hay token, lanzar login directamente
            lanzarLogin()
        }
    }

    /**
     * Muestra la actividad de login y espera resultado.
     */
    private fun lanzarLogin() {
        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Si el login o registro fue exitoso, navega a Marcas
                navegarAMarcas()
            } else {
                // El usuario canceló o hubo fallo → cerrar app
                finish()
            }
        }

        loginLauncher.launch(Intent(this, LoginActivity::class.java))
    }

    /**
     * Lanza la pantalla principal de gestión de marcas.
     */
    private fun navegarAMarcas() {
        startActivity(Intent(this, MarcasActivity::class.java))
        finish()
    }

    /**
     * Muestra un mensaje breve en pantalla.
     */
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
