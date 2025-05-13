package com.alexmoreno.transportes.api

import com.alexmoreno.transportes.modelos.Marca
import com.alexmoreno.transportes.modelos.Modelo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object MarcasClient {

    private val urlClient = "${ApiClient.BASE_URL}/marcas"

    fun obtenerMarcas(callback: (Boolean, List<Marca>?, String?) -> Unit) {
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
                        val marcas = mutableListOf<Marca>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val modelos = mutableListOf<Modelo>()
                            val modelosJson = obj.optJSONArray("modelos")
                            if (modelosJson != null) {
                                for (j in 0 until modelosJson.length()) {
                                    val m = modelosJson.getJSONObject(j)
                                    modelos.add(Modelo(
                                        id = m.getLong("id"),
                                        nombre = m.getString("nombre"),
                                        descripcion = m.getString("descripcion"),
                                        precioBase = m.getDouble("precioBase"),
                                        marcaId = m.getLong("marcaId"),
                                        colorId = m.getLong("colorId"),
                                        precioTotal = m.getDouble("precioTotal")
                                    ))
                                }
                            }
                            marcas.add(Marca(
                                id = obj.getLong("id"),
                                nombre = obj.getString("nombre"),
                                descripcion = obj.getString("descripcion"),
                                imagen = obj.getString("imagen"),
                                anioFundacion = obj.getInt("anioFundacion"),
                                modelos = modelos
                            ))
                        }
                        callback(true, marcas, null)
                    } catch (e: Exception) {
                        callback(false, null, "Error al parsear JSON: ${e.message}")
                    }
                } else {
                    callback(false, null, "Error al obtener marcas")
                }
            }
        })
    }

    fun obtenerMarcaPorId(
        id: Long,
        callback: (success: Boolean, marca: Marca?, error: String?) -> Unit
    ) {
        val token = ApiClient.obtenerToken() ?: return callback(false, null, "Token no disponible")

        val url = "$urlClient/$id"

        val request = Request.Builder()
            .url(url)
            .get()
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
                        val obj = JSONObject(body)

                        val modelos = mutableListOf<Modelo>()
                        val modelosJson = obj.optJSONArray("modelos")
                        if (modelosJson != null) {
                            for (j in 0 until modelosJson.length()) {
                                val m = modelosJson.getJSONObject(j)
                                modelos.add(
                                    Modelo(
                                        id = m.getLong("id"),
                                        nombre = m.getString("nombre"),
                                        descripcion = m.getString("descripcion"),
                                        precioBase = m.getDouble("precioBase"),
                                        marcaId = m.getLong("marcaId"),
                                        colorId = m.getLong("colorId"),
                                        precioTotal = m.getDouble("precioTotal")
                                    )
                                )
                            }
                        }

                        val marca = Marca(
                            id = obj.getLong("id"),
                            nombre = obj.getString("nombre"),
                            descripcion = obj.getString("descripcion"),
                            imagen = obj.getString("imagen"),
                            anioFundacion = obj.getInt("anioFundacion"),
                            modelos = modelos
                        )

                        callback(true, marca, null)
                    } catch (e: Exception) {
                        callback(false, null, "Error al parsear JSON: ${e.message}")
                    }
                } else {
                    callback(false, null, "Error al obtener marca: ${response.message}")
                }
            }
        })
    }

    fun crearMarca(
        nombre: String,
        descripcion: String,
        imagen: String,
        anioFundacion: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")
        val body = JSONObject().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("imagen", imagen)
            put("anioFundacion", anioFundacion)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(urlClient)
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "crear marca"))
    }

    fun actualizarMarca(
        id: Long,
        nombre: String,
        descripcion: String,
        imagen: String,
        anioFundacion: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")
        val body = JSONObject().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("imagen", imagen)
            put("anioFundacion", anioFundacion)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$urlClient/$id")
            .put(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "actualizar marca"))
    }

    fun eliminarMarca(id: Long, callback: (Boolean, String?) -> Unit) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")

        val request = Request.Builder()
            .url("$urlClient/$id")
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "eliminar marca"))
    }

    private fun callbackEnBool(callback: (Boolean, String?) -> Unit, operacion: String): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Error de red al $operacion: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful, if (response.isSuccessful) null else "Error al $operacion")
            }
        }
    }
}
