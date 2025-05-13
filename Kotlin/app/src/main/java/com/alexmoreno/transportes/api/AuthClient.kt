package com.alexmoreno.transportes.api


import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object AuthClient {

    private val urlClient = "${ApiClient.BASE_URL}/auth"

    fun login(
        nombre: String,
        contrasenia: String,
        callback: (success: Boolean, token: String?, errorMessage: String?) -> Unit
    ) {
        val url = "$urlClient/login"

        val json = JSONObject().apply {
            put("nombre", nombre)
            put("contrasenia", contrasenia)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val token = JSONObject(responseBody).getString("token")
                        callback(true, token, null)
                    } catch (e: Exception) {
                        callback(false, null, "Error al procesar la respuesta")
                    }
                } else {
                    val errorMsg = try {
                        val errorJson = JSONObject(responseBody ?: "")
                        errorJson.optString("mensaje", "Error desconocido")
                    } catch (e: Exception) {
                        responseBody ?: "Error desconocido"
                    }

                    callback(false, null, errorMsg)
                }
            }
        })
    }

    fun validarToken(callback: (esValido: Boolean, errorMessage: String?) -> Unit) {
        val token = ApiClient.obtenerToken()

        if (token == null) {
            callback(false, "Token no disponible")
            return
        }

        val url = "$urlClient/validar-token"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody ?: "").optString("mensaje", "Token inválido o expirado")
                    } catch (e: Exception) {
                        responseBody ?: "Token inválido o expirado"
                    }
                    callback(false, errorMsg)
                }
            }
        })
    }

    fun registrarUsuario(
        nombre: String,
        contrasenia: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        val url = "$urlClient/registro"

        val json = JSONObject().apply {
            put("nombre", nombre)
            put("contrasenia", contrasenia)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody ?: "").optString("mensaje", "Error desconocido")
                    } catch (e: Exception) {
                        responseBody ?: "Error desconocido"
                    }
                    callback(false, errorMsg)
                }
            }
        })
    }
}
