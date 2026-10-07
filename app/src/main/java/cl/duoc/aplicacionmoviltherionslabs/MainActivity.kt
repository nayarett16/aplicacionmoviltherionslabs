package cl.duoc.aplicacionmoviltherionslabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.EspaciosScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.LoginScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.NuevaSolicitudScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.ProfesorHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.AplicacionMovilTherionsLabsTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionMovilTherionsLabsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color= MaterialTheme.colorScheme.background
                ){
                    var pantallaActual by remember { mutableStateOf("login") }
                    when (pantallaActual){
                        "login" ->{
                            LoginScreen(
                                onLoginSuccess = {
                                    pantallaActual="home"
                                }
                            )
                        }
                        "home" ->{
                            ProfesorHomeScreen (
                                onBuscarSalasClick = {
                                    pantallaActual="espacios"
                                },
                                onNuevaSolicitudClick = {
                                    pantallaActual="solicitud"
                                }
                            )
                        }
                        "espacios"->{
                            EspaciosScreen(
                                onVolverClick={
                                    pantallaActual="home"
                                }
                            )
                        }
                        "solicitud"->{
                            NuevaSolicitudScreen (
                                onVolverClick = {
                                    pantallaActual="home"
                                },
                                onSolicitudEnviada = {
                                    pantallaActual="home"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}