package cl.duoc.aplicacionmoviltherionslabs.data

import cl.duoc.aplicacionmoviltherionslabs.model.RecursoModel
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient

object RepositorioRecursos {

    val inventarioInicial = listOf(
        RecursoModel("cta_01", "Micrófonos Inalámbricos", "CTA", stockTotal = 8, stockDisponible = 5, unidadMedida = "equipos"),
        RecursoModel("cta_02", "Consola de Amplificación", "CTA", stockTotal = 3, stockDisponible = 2, unidadMedida = "sistemas"),
        RecursoModel("cta_03", "Proyector HDMI Alta Definición", "CTA", stockTotal = 5, stockDisponible = 4, unidadMedida = "unidades"),
        RecursoModel("com_01", "Cámara Fotográfica Réflex", "COM", stockTotal = 3, stockDisponible = 2, unidadMedida = "unidades"),
        RecursoModel("com_02", "Pendones Institucionales Duoc UC", "COM", stockTotal = 6, stockDisponible = 6, unidadMedida = "unidades"),
        RecursoModel("sg_01", "Sillas Plegables Auditorio", "SERVICIOS", stockTotal = 150, stockDisponible = 120, unidadMedida = "sillas"),
        RecursoModel("sg_02", "Mesas Ejecutivas para Exposición", "SERVICIOS", stockTotal = 20, stockDisponible = 15, unidadMedida = "mesas"),
        RecursoModel("sg_03", "Toldos Provisionales (3x3m)", "SERVICIOS", stockTotal = 6, stockDisponible = 4, unidadMedida = "unidades"),
        RecursoModel("port_01", "Credenciales Visita Externa", "PORTERIA", stockTotal = 100, stockDisponible = 85, unidadMedida = "tarjetas"),
        RecursoModel("port_02", "Vallas Papales de Contención", "PORTERIA", stockTotal = 12, stockDisponible = 10, unidadMedida = "módulos"),
        RecursoModel("ext_01", "Galardones y Regalos Institucionales", "EXTENSION", stockTotal = 25, stockDisponible = 20, unidadMedida = "unidades"),
        RecursoModel("ext_02", "Set Mantelería Protocolar", "EXTENSION", stockTotal = 8, stockDisponible = 6, unidadMedida = "sets")
    )

    // Lista dinámica que se actualiza con la nube si hay conexión
    private var cacheRecursos: List<RecursoModel> = inventarioInicial

    // Nueva función asíncrona para sincronizar con el Gateway en Render
    suspend fun sincronizarRecursosDesdeNube(): List<RecursoModel> {
        return try {
            val response = RetrofitClient.apiService.obtenerRecursos()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                cacheRecursos = response.body()!!
            }
            cacheRecursos
        } catch (e: Exception) {
            // Si el microservicio aún no está arriba o falla la red, usa el respaldo local
            cacheRecursos
        }
    }

    fun obtenerTodosLosRecursos(): List<RecursoModel> = cacheRecursos

    fun obtenerRecursosPorArea(area: String): List<RecursoModel> {
        return cacheRecursos.filter { it.area.equals(area, ignoreCase = true) }
    }
}