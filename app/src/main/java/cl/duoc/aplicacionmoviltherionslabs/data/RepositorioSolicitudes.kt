package cl.duoc.aplicacionmoviltherionslabs.data

import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient
import cl.duoc.aplicacionmoviltherionslabs.network.toDto
import cl.duoc.aplicacionmoviltherionslabs.network.toModel

object RepositorioSolicitudes {

    suspend fun obtenerSolicitudesNube(): List<SolicitudActividad> {
        return try {
            val response = RetrofitClient.apiService.obtenerSolicitudes()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { it.toModel() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun guardarSolicitudNube(solicitud: SolicitudActividad): Boolean {
        return try {
            val response = RetrofitClient.apiService.crearSolicitud(solicitud.toDto())
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarEstadoSolicitudNube(solicitud: SolicitudActividad): Boolean {
        return try {
            val response = RetrofitClient.apiService.actualizarSolicitud(
                id = solicitud.id,
                solicitud = solicitud.toDto()
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarSolicitudNube(id: String): Boolean {
        return try {
            val response = RetrofitClient.apiService.eliminarSolicitud(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}