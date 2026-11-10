package dam.pmdm.lifeblocks.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam.pmdm.lifeblocks.data.ActivityEntity

@Composable
fun HistoryScreen(historyList: List<ActivityEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E0D)) // Fondo Negro Carbón
            .padding(16.dp)
    ) {
        // Título tipo panel
        Text(
            text = "REGISTRO DE ACTIVIDAD (LOG)",
            color = Color(0xFFC67C2C),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp
        )

        // Línea de separación técnica
        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFC67C2C)).padding(vertical = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de eventos técnicos
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historyList) { entry ->
                LogItemComponent(entry)
            }
        }
    }
}

@Composable
fun LogItemComponent(activity: ActivityEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF5D5D5D)) // Borde gris acero
            .background(Color(0xFF1E1E1E))
            .padding(12.dp)
    ) {
        // Nombre de la actividad
        Text(
            text = activity.name.uppercase(),
            color = Color(0xFFE3C096),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        // Duración o tiempo
        Text(
            text = "${activity.durationMinutes} MIN",
            color = Color(0xFFC67C2C),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}