package cl.duoc.aplicacionmoviltherionslabs.usuarios_service.controller

import cl.duoc.aplicacionmoviltherionslabs.usuarios_service.model.Usuario
import cl.duoc.aplicacionmoviltherionslabs.usuarios_service.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginRequest(val correo: String, val password: String)

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = ["*"])
class UsuarioController(private val usuarioService: UsuarioService) {

    @PostMapping("/login")
    fun iniciarSesion(@RequestBody request: LoginRequest): ResponseEntity<Usuario> {
        val usuario = usuarioService.login(request.correo, request.password)
        return if (usuario != null) {
            ResponseEntity.ok(usuario)
        } else {
            ResponseEntity.status(401).build()
        }
    }

    @GetMapping
    fun obtenerUsuarios(): List<Usuario> {
        return usuarioService.listarTodos()
    }
}