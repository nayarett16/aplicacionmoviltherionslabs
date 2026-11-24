package cl.duoc.aplicacionmoviltherionslabs.usuarios_service.service

import cl.duoc.aplicacionmoviltherionslabs.usuarios_service.model.Usuario
import cl.duoc.aplicacionmoviltherionslabs.usuarios_service.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(private val usuarioRepository: UsuarioRepository) {

    fun login(correo: String, password: String): Usuario? {
        return usuarioRepository.findByCorreoAndPassword(correo, password)
    }

    fun listarTodos(): List<Usuario> {
        return usuarioRepository.findAll()
    }
}