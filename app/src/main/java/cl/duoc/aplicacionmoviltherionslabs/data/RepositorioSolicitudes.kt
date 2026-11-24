package cl.duoc.aplicacionmoviltherionslabs.data

import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudDto
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient

object RepositorioSolicitudes {

    private fun SolicitudDto.aModeloActividad(index: Int = 0): SolicitudActividad {
        val descSegura = descripcion.orEmpty()
        val tituloSeguro = titulo.orEmpty()
        val correoSeguro = usuarioCorreo.orEmpty()
        val estadoSeguro = estado.orEmpty()

        val partes = descSegura.split("|").map { it.trim() }

        val areaExtraida = partes.firstOrNull { it.startsWith("Área:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: "Sede Duoc UC"

        val fechaHoraStr = partes.firstOrNull { it.startsWith("Fecha:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: ""
        val tokensFecha = fechaHoraStr.split(" ").filter { it.isNotBlank() }
        val fechaExtraida = tokensFecha.getOrNull(0)
            ?: fechaCreacion?.take(10)
            ?: "Sin fecha"
        val horaExtraida = tokensFecha.getOrNull(1) ?: "08:00"

        val recursosTexto = partes.firstOrNull { it.startsWith("Recursos:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?: descSegura

        val listaRecursos = if (recursosTexto.isNotBlank()) {
            recursosTexto.split(",").map { it.trim() }.filter { it.isNotBlank() }
        } else {
            emptyList()
        }

        val estadoNormalizado = when (estadoSeguro.uppercase()) {
            "APROBADO", "APROBADA" -> "Aprobado"
            "RECHAZADO", "RECHAZADA" -> "Rechazado"
            else -> "Pendiente"
        }

        return SolicitudActividad(
            id = id?.toString() ?: "remota_${index}_${System.currentTimeMillis()}",
            nombreActividad = tituloSeguro.trim().ifBlank { "Actividad sin título" },
            responsable = correoSeguro.trim(),
            escuelaArea = areaExtraida,
            fechaTexto = fechaExtraida,
            horaTexto = horaExtraida,
            recursosSeleccionados = listaRecursos,
            estadoGeneral = estadoNormalizado,
            estadoComunicaciones = estadoNormalizado,
            estadoServiciosGenerales = estadoNormalizado,
            estadoCTA = estadoNormalizado,
            estadoPorteria = estadoNormalizado
        )
    }

    suspend fun obtenerSolicitudesNube(): List<SolicitudActividad> {
        return try {
            val response = RetrofitClient.solicitudesApi.obtenerTodas()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.mapIndexed { index, dto -> dto.aModeloActividad(index) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun guardarSolicitudNube(solicitud: SolicitudActividad): Boolean {
        return true
    }

    suspend fun actualizarEstadoSolicitudNube(solicitud: SolicitudActividad): Boolean {
        val idNumerico = solicitud.id.toLongOrNull() ?: return false
        return try {
            val resumenDescripcion =
                "Área: ${solicitud.escuelaArea} | Fecha: ${solicitud.fechaTexto} ${solicitud.horaTexto} | Recursos: ${solicitud.recursosSeleccionados.joinToString(", ")}"

            val dto = SolicitudDto(
                id = idNumerico,
                usuarioCorreo = solicitud.responsable,
                titulo = solicitud.nombreActividad,
                descripcion = resumenDescripcion,
                estado = solicitud.estadoGeneral.uppercase()
            )
            val response = RetrofitClient.solicitudesApi.actualizarEstado(
                id = idNumerico,
                solicitud = dto
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarSolicitudNube(id: String): Boolean {
        val idNumerico = id.toLongOrNull() ?: return false
        return try {
            val response = RetrofitClient.solicitudesApi.eliminarSolicitud(idNumerico)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}