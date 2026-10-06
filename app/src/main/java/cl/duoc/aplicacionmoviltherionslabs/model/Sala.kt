package cl.duoc.aplicacionmoviltherionslabs.model

data class Sala(
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val piso: Int,
    val capacidad: Int,
    val caracteristicas: List<String>,
    val disponibleAhora: Boolean=true

)