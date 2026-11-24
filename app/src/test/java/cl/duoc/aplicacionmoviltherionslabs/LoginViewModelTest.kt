package cl.duoc.aplicacionmoviltherionslabs

import cl.duoc.aplicacionmoviltherionslabs.network.LoginRequest
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient
import cl.duoc.aplicacionmoviltherionslabs.network.UsuariosApiService
import cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import android.util.Patterns
import java.util.regex.Pattern
import io.mockk.mockkStatic

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `cuando email esta vacio muestra error de correo obligatorio`() {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("demo1234")
        viewModel.onLoginClicked()

        assertEquals("El correo es obligatorio", viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `cuando password tiene menos de 6 caracteres muestra error`() {
        viewModel.onEmailChanged("comunidad@duocuc.cl")
        viewModel.onPasswordChanged("123")
        viewModel.onLoginClicked()

        assertEquals("Debe tener al menos 6 caracteres", viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `togglePasswordVisibility invierte visibilidad correctamente`() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `login exitoso con microservicio mockeado marca isSuccess true`() = runTest {
        val mockApi = mockk<UsuariosApiService>()
        mockkObject(RetrofitClient)
        every { RetrofitClient.usuariosApi } returns mockApi
        coEvery { mockApi.login(any<LoginRequest>()) } returns Response.success(mockk(relaxed = true))

        viewModel.onEmailChanged("comunidad@duocuc.cl")
        viewModel.onPasswordChanged("demo1234")
        viewModel.onLoginClicked()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login con fallo de red usa cuenta de respaldo local`() = runTest {
        val mockApi = mockk<UsuariosApiService>()
        mockkObject(RetrofitClient)
        every { RetrofitClient.usuariosApi } returns mockApi
        coEvery { mockApi.login(any<LoginRequest>()) } throws RuntimeException("Sin conexión")

        viewModel.onEmailChanged("comunidad@duocuc.cl")
        viewModel.onPasswordChanged("demo1234")
        viewModel.onLoginClicked()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `cerrarSesion reinicia el estado del formulario`() {
        viewModel.onEmailChanged("comunidad@duocuc.cl")
        viewModel.onPasswordChanged("demo1234")
        viewModel.cerrarSesion()

        assertEquals("", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.password)
        assertFalse(viewModel.uiState.value.isSuccess)
    }
}