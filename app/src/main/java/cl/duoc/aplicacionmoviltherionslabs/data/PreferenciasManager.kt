package cl.duoc.aplicacionmoviltherionslabs.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

class PreferenciasManager(context: Context) {

    private val prefs = context.getSharedPreferences("duoc_app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun guardarSolicitudes(lista: List<SolicitudActividad>) {
        val json = gson.toJson(lista)
        prefs.edit().putString("KEY_SOLICITUDES", json).apply()
    }

    fun cargarSolicitudes(): List<SolicitudActividad> {
        val json = prefs.getString("KEY_SOLICITUDES", null) ?: return emptyList()
        val type = object : TypeToken<List<SolicitudActividad>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}