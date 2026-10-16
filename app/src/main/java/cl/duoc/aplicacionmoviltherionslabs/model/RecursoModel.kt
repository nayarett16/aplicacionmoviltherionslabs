package cl.duoc.aplicacionmoviltherionslabs.model

/**
 * Modelo que representa un recurso/insumo administrado por las 5 áreas operativas.
 * Solicitables por toda la comunidad (Alumnos, Docentes, Colaboradores).
 */
data class RecursoModel(
    val id: String,
    val nombre: String,
    val area: String, // CTA, COM, SERVICIOS, PORTERIA, EXTENSION
    val stockTotal: Int,
    val stockDisponible: Int,
    val unidadMedida: String = "unidades",
    val estaDisponible: Boolean = stockDisponible > 0
)