package dam.pmdm.lifeblocks.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp


// Asegúrate de tener esto en tu Theme.kt
// En tu Theme.kt, fuerza el estilo sin elevación
// Dentro de tu archivo Theme.kt
private val IndustrialColorScheme = darkColorScheme(
    primary = Color(0xFFC67C2C),      // Cobre
    background = Color(0xFF0F0E0D),   // Negro Carbón (Fondo global)
    surface = Color(0xFF1E1E1E),      // Gris acero (Tarjetas/Paneles)
    onSurface = Color(0xFFE3C096),    // Pergamino
    onBackground = Color(0xFFE3C096)
)
private val SteampunkColorScheme = darkColorScheme(
    primary = Color(0xFFC67C2C),      // Cobre
    background = Color(0xFF0F0E0D),   // Negro Carbón
    surface = Color(0xFF1E1E1E),      // Gris acero
    onSurface = Color(0xFFE3C096),
    onBackground = Color(0xFFE3C096)
)

// 2. Definimos las formas cuadradas
private val IndustrialShapes = Shapes(
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp)
)
@Composable
fun LifeBlocksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SteampunkColorScheme else SteampunkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(
            small = RoundedCornerShape(0.dp),
            medium = RoundedCornerShape(0.dp),
            large = RoundedCornerShape(0.dp)
        ),
        content = content
    )
}