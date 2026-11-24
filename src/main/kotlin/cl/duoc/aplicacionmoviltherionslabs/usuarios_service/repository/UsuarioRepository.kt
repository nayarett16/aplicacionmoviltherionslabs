package cl.duoc.aplicacionmoviltherionslabs.usuarios_service.repository

import cl.duoc.aplicacionmoviltherionslabs.usuarios_service.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository : JpaRepository<Usuario, Int> {
    fun findByCorreoAndPassword(correo: String, password: String): Usuario?
}