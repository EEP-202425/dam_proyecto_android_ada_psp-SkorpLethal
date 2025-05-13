package com.alexmoreno.transportes.api

import com.alexmoreno.transportes.modelos.Modelo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object ModelosClient {

    private val urlClient = "${ApiClient.BASE_URL}/modelos"

    fun obtenerModeloPorId(id: Long, callback: (Boolean, Modelo?, String?) -> Unit) {
        val token = ApiClient.obtenerToken() ?: return callback(false, null, "Token no disponible")

        val request = Request.Builder()
            .url("$urlClient/$id")
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
                        val modelo = Modelo(
                            id = obj.getLong("id"),
                            nombre = obj.getString("nombre"),
                            descripcion = obj.getString("descripcion"),
                            precioBase = obj.getDouble("precioBase"),
                            marcaId = obj.getLong("marcaId"),
                            colorId = obj.getLong("colorId"),
                            precioTotal = obj.getDouble("precioTotal")
                        )
                        callback(true, modelo, null)
                    } catch (e: Exception) {
                        callback(false, null, "Error al parsear JSON: ${e.message}")
                    }
                } else {
                    callback(false, null, "Error al obtener modelo")
                }
            }
        })
    }

    fun crearModelo(
        idMarca: Long,
        idColor: Long,
        nombre: String,
        descripcion: String,
        precioBase: Double,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")

        val body = JSONObject().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precioBase", precioBase)
            put("marca", JSONObject().put("id", idMarca))
            put("color", JSONObject().put("id", idColor))
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(urlClient)
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "crear modelo"))
    }

    fun actualizarModelo(
        id: Long,
        nombre: String,
        descripcion: String,
        precioBase: Double,
        idColor: Long,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")

        val body = JSONObject().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precioBase", precioBase)
            put("color", JSONObject().put("id", idColor))
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$urlClient/$id")
            .put(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "actualizar modelo"))
    }

    fun eliminarModelo(id: Long, callback: (Boolean, String?) -> Unit) {
        val token = ApiClient.obtenerToken() ?: return callback(false, "Token no disponible")

        val request = Request.Builder()
            .url("$urlClient/$id")
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        ApiClient.client.newCall(request).enqueue(callbackEnBool(callback, "eliminar modelo"))
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
