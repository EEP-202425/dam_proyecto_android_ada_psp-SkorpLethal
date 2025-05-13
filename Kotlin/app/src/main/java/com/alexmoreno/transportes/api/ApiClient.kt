package com.alexmoreno.transportes.api

import android.content.Context
import okhttp3.OkHttpClient

object ApiClient {
    private lateinit var appContext: Context

    val Auth = AuthClient
    val Marcas = MarcasClient
    val Modelos = ModelosClient
    val Colores = ColoresClient

    val client = OkHttpClient()
    const val BASE_URL = "http://10.0.2.2:8080/api"

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun obtenerToken(): String? {
        val prefs = appContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return prefs.getString("token", null)
    }
    fun borrarToken(){
        appContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).edit().remove("token").apply()
    }
}
