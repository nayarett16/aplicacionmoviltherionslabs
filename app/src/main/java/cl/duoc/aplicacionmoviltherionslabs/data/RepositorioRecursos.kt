
package cl.duoc.aplicacionmoviltherionslabs.data

import cl.duoc.aplicacionmoviltherionslabs.model.RecursoModel

object RepositorioRecursos {

    val inventarioInicial = listOf(
        // 1. CTA / Servicios Digitales (Soporte Técnico y Audiovisual)
        RecursoModel("cta_01", "Micrófonos Inalámbricos", "CTA", stockTotal = 8, stockDisponible = 5, unidadMedida = "equipos"),
        RecursoModel("cta_02", "Consola de Amplificación", "CTA", stockTotal = 3, stockDisponible = 2, unidadMedida = "sistemas"),
        RecursoModel("cta_03", "Proyector HDMI Alta Definición", "CTA", stockTotal = 5, stockDisponible = 4, unidadMedida = "unidades"),

        // 2. Comunicaciones (Registro y Cobertura)
        RecursoModel("com_01", "Cámara Fotográfica Réflex", "COM", stockTotal = 3, stockDisponible = 2, unidadMedida = "unidades"),
        RecursoModel("com_02", "Pendones Institucionales Duoc UC", "COM", stockTotal = 6, stockDisponible = 6, unidadMedida = "unidades"),

        // 3. Servicios Generales y Auxiliaría (Mobiliario y Aseo)
        RecursoModel("sg_01", "Sillas Plegables Auditorio", "SERVICIOS", stockTotal = 150, stockDisponible = 120, unidadMedida = "sillas"),
        RecursoModel("sg_02", "Mesas Ejecutivas para Exposición", "SERVICIOS", stockTotal = 20, stockDisponible = 15, unidadMedida = "mesas"),
        RecursoModel("sg_03", "Toldos Provisionales (3x3m)", "SERVICIOS", stockTotal = 6, stockDisponible = 4, unidadMedida = "unidades"),

        // 4. Portería (Accesos y Seguridad)
        RecursoModel("port_01", "Credenciales Visita Externa", "PORTERIA", stockTotal = 100, stockDisponible = 85, unidadMedida = "tarjetas"),
        RecursoModel("port_02", "Vallas Papales de Contención", "PORTERIA", stockTotal = 12, stockDisponible = 10, unidadMedida = "módulos"),

        // 5. Extensión (Coordinación Logística)
        RecursoModel("ext_01", "Galardones y Regalos Institucionales", "EXTENSION", stockTotal = 25, stockDisponible = 20, unidadMedida = "unidades"),
        RecursoModel("ext_02", "Set Mantelería Protocolar", "EXTENSION", stockTotal = 8, stockDisponible = 6, unidadMedida = "sets")
    )

    // Catálogo completo visible para cualquier solicitante (Comunidad / Docentes / Alumnos)
    fun obtenerTodosLosRecursos(): List<RecursoModel> = inventarioInicial

    // Inventario filtrado por área para que la jefatura correspondiente administre/valide
    fun obtenerRecursosPorArea(area: String): List<RecursoModel> {
        return inventarioInicial.filter { it.area.equals(area, ignoreCase = true) }
    }
}
