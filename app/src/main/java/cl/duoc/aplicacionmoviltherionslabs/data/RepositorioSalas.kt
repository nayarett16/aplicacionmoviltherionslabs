package cl.duoc.aplicacionmoviltherionslabs.data

import cl.duoc.aplicacionmoviltherionslabs.model.Sala

object RoomRepository {

    // Base de datos de prueba ampliada para pruebas de estrés y filtros
    private val baseDeDatosSalas = listOf(
        // --- PISO 1 ---
        Sala(
            codigo = "AUD01",
            nombre = "Auditorio Principal Central",
            tipo = "Auditorio",
            piso = 1,
            capacidad = 150,
            caracteristicas = listOf("Proyector", "Climatización", "Videoconferencia", "Sistema de Audio"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "LC10",
            nombre = "Laboratorio Mac Design",
            tipo = "Laboratorio",
            piso = 1,
            capacidad = 20,
            caracteristicas = listOf("Computadores", "Climatización", "Proyector"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "MULTI01",
            nombre = "Sala Innovación y Co-Work",
            tipo = "Multipropósito",
            piso = 1,
            capacidad = 35,
            caracteristicas = listOf("Climatización", "Pantalla Táctil", "Videoconferencia"),
            disponibleAhora = false
        ),
        Sala(
            codigo = "EST01",
            nombre = "Sala de Estudio Colaborativo 1",
            tipo = "Sala de Estudio",
            piso = 1,
            capacidad = 15,
            caracteristicas = listOf("Climatización", "Pantalla Táctil"),
            disponibleAhora = true
        ),

        // --- PISO 2 ---
        Sala(
            codigo = "LC25",
            nombre = "Laboratorio Desarrollo Mobile",
            tipo = "Laboratorio",
            piso = 2,
            capacidad = 30,
            caracteristicas = listOf("Proyector", "Computadores", "Climatización"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "LC22",
            nombre = "Laboratorio Redes y Ciberseguridad",
            tipo = "Laboratorio",
            piso = 2,
            capacidad = 25,
            caracteristicas = listOf("Computadores", "Proyector", "Servidores Rack"),
            disponibleAhora = false
        ),
        Sala(
            codigo = "S201",
            nombre = "Sala Cátedra Estándar A",
            tipo = "Sala de clases",
            piso = 2,
            capacidad = 45,
            caracteristicas = listOf("Proyector", "Climatización"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "S202",
            nombre = "Sala Cátedra Estándar B",
            tipo = "Sala de clases",
            piso = 2,
            capacidad = 50,
            caracteristicas = listOf("Proyector"),
            disponibleAhora = true
        ),

        // --- PISO 3 ---
        Sala(
            codigo = "LC31",
            nombre = "Laboratorio Hardware y Robótica",
            tipo = "Laboratorio",
            piso = 3,
            capacidad = 28,
            caracteristicas = listOf("Computadores", "Proyector", "Climatización", "Pantalla Táctil"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "S301",
            nombre = "Sala Cátedra Magna",
            tipo = "Sala de clases",
            piso = 3,
            capacidad = 60,
            caracteristicas = listOf("Proyector", "Climatización", "Sistema de Audio"),
            disponibleAhora = false
        ),
        Sala(
            codigo = "TAL30",
            nombre = "Taller de Proyectos de Integración",
            tipo = "Taller",
            piso = 3,
            capacidad = 30,
            caracteristicas = listOf("Proyector", "Pantalla Táctil", "Climatización"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "EST02",
            nombre = "Sala de Estudio Silencioso",
            tipo = "Sala de Estudio",
            piso = 3,
            capacidad = 12,
            caracteristicas = listOf("Climatización"),
            disponibleAhora = true
        ),

        // --- PISO 4 ---
        Sala(
            codigo = "AUD02",
            nombre = "Mini Auditorio de Postgrados",
            tipo = "Auditorio",
            piso = 4,
            capacidad = 80,
            caracteristicas = listOf("Proyector", "Climatización", "Videoconferencia", "Sistema de Audio"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "LC40",
            nombre = "Laboratorio de Inteligencia Artificial",
            tipo = "Laboratorio",
            piso = 4,
            capacidad = 22,
            caracteristicas = listOf("Computadores", "Climatización", "Videoconferencia", "Proyector"),
            disponibleAhora = true
        ),
        Sala(
            codigo = "S405",
            nombre = "Sala Cátedra Ejecutiva",
            tipo = "Sala de clases",
            piso = 4,
            capacidad = 40,
            caracteristicas = listOf("Proyector", "Climatización", "Videoconferencia"),
            disponibleAhora = false
        )
    )

    // Consultar todas las salas
    fun obtenerSalas(): List<Sala> {
        return baseDeDatosSalas
    }

    // Buscar por código exacto o parcial
    fun buscarPorCodigo(codigo: String): Sala? {
        return baseDeDatosSalas.find { it.codigo.equals(codigo, ignoreCase = true) }
    }

    // Función lista para filtrar (ideal para la pantalla de búsqueda)
    fun filtrarSalas(
        tipo: String? = null,
        piso: Int? = null,
        soloDisponibles: Boolean = false,
        requiereComputadores: Boolean = false
    ): List<Sala> {
        return baseDeDatosSalas.filter { sala ->
            (tipo == null || sala.tipo.equals(tipo, ignoreCase = true)) &&
                    (piso == null || sala.piso == piso) &&
                    (!soloDisponibles || sala.disponibleAhora) &&
                    (!requiereComputadores || sala.caracteristicas.contains("Computadores"))
        }
    }
}