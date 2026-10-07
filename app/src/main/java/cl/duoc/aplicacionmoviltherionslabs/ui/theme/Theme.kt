package cl.duoc.aplicacionmoviltherionslabs.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DuocColorScheme = lightColorScheme(


    primary = AzulDuoc,
    onPrimary = Color.White,
    primaryContainer = AzulDuoc.copy(alpha = 0.1f),
    secondary = AmarilloDuoc,
    onSecondary = AzulDuoc,
    secondaryContainer = AmarilloDuoc.copy(alpha = 0.2f),
    onSecondaryContainer = AzulOscuroDuoc,
    background = GrisFondoDuoc,
    onBackground = AzulOscuroDuoc,
    surface = Color.White,
    onSurface = AzulOscuroDuoc,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextoGrisDuoc
)
@Composable
fun AplicacionMovilTherionsLabsTheme(
   content:@Composable ()-> Unit
) {
    MaterialTheme(
        colorScheme = DuocColorScheme,
        typography = Typography,
        content = content
    )
}