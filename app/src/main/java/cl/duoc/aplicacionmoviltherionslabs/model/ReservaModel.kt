package cl.duoc.aplicacionmoviltherionslabs.model

//estados de la reserva
enum class EstadoReserva  {
    PENDIENTE,
    APROBADA,
    RECHAZADA
}
// modelo que representa las salas y espacios
data class Sala(
    val codigo : String,
    val nombre:String,
    val tipo:String,
    val piso: Int,
    val capacidad: Int,
    val caracteristicas: List<String> = emptyList(),
    val disponibleAhora: Boolean=true
)
//modelo de bloques horarios
data class BloqueHorario(
    val id: String,
    val horaInicio: String,
    val horaFin: String,
    val disponible: Boolean,
    val asignatura: String?=null,
    val profesor: String?=null
)
//modelo para agregar la programacion del calendario
data class ReservaSemestral(
    val id: String,
    val salaCodigo: String,
    val asignatura: String,
    val profesor: String,
    val fecha: String,
    val mes :String,
    val diaSemana:String,
    val numeroSemana: Int,
    val horaInicio: String,
    val horaFin: String
)
data class SolicitudReserva(
    val id: String,
    val salaCodigo: String,
    val docenteNombre: String,
    val asignatura: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: EstadoReserva= EstadoReserva.PENDIENTE,
    val motivo: String?=null
)
