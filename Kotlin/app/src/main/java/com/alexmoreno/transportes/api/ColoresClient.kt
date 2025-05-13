package com.alexmoreno.transportes.api

import com.alexmoreno.transportes.modelos.Color
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

object ColoresClient {

    val urlClient = "${ApiClient.BASE_URL}/colores"

    fun obtenerColores(callback: (success: Boolean, colores: List<Color>?, error: String?) -> Unit) {
        val token = ApiClient.obtenerToken()

        if (token == null) {
            callback(false, null, "Token no disponible")
            return
        }

        val request = Request.Builder()
            .url(urlClient)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val jsonArray = JSONArray(body)
                        val colores = mutableListOf<Color>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val color = Color(
                                id = obj.getLong("id"),
                                descripcion = obj.getString("descripcion"),
                                precio = obj.getDouble("precio")
                            )
                            colores.add(color)
                        }

                        callback(true, colores, null)
                    } catch (e: Exception) {
                        callback(false, null, "Error al parsear JSON: ${e.message}")
                    }
                } else {
                    callback(false, null, "Error al obtener colores")
                }
            }
        })
    }



}
