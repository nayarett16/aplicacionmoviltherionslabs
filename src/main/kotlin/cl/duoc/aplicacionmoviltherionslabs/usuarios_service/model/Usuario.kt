package cl.duoc.aplicacionmoviltherionslabs.usuarios_service.model

import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false)
    val correo: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Column(nullable = false)
    val nombre: String = "",

    @Column(nullable = false)
    val rol: String = "",

    @Column(name = "area_entidad")
    val areaEntidad: String? = null
)