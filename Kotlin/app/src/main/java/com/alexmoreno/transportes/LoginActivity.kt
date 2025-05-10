package com.alexmoreno.transportes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import org.json.JSONObject
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class LoginActivity : ComponentActivity() {

    private val cliente = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textNombre = findViewById<EditText>(R.id.textNombre)
        val textContrasenia = findViewById<EditText>(R.id.editTextPassword)
        val botonLogin = findViewById<Button>(R.id.buttonLogin)

        botonLogin.setOnClickListener{
            val nombre = textNombre.text.toString().trim()
            val contrasenia = textContrasenia.text.toString().trim()

            if (nombre.isNotEmpty()&& contrasenia.isNotEmpty()){
                login(nombre, contrasenia)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(nombre: String, contrasenia: String){
        val url = "http://10.0.2.2:8080/api/auth/login"

        val json = JSONObject()
        json.put("nombre", nombre)
        json.put("contrasenia", contrasenia)

        val cuerpo = json.toString().toRequestBody()
        val request = Request.Builder()
            .url(url)
            .post(cuerpo)
            .addHeader("Content-Type", "application/json")
            .build()

        cliente.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread{
                    Toast.makeText(this@LoginActivity,"Error de red",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response){
                val responseBody = response.body?.string()

                runOnUiThread{
                    if (response.isSuccessful && responseBody!=null){
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val token = jsonResponse.getString("token")

                            val prefs = getSharedPreferences(
                                "MyAppPrefs",
                                Context.MODE_PRIVATE
                            )
                            prefs.edit().putString("token", token).apply()

                            Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT)
                                .show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                        } catch (e:Exception){
                            Toast.makeText(this@LoginActivity,"Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                        }
                        } else {
                            Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}