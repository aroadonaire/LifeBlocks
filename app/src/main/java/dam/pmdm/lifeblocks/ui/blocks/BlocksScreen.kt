package dam.pmdm.lifeblocks.ui.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.pmdm.lifeblocks.BlockEntity
import dam.pmdm.lifeblocks.data.MissionEntity

@Composable
fun BlocksScreen(
    navController: NavController,
    blocks: List<BlockEntity>,
    missions: List<MissionEntity>,
    onBlockClick: (String) -> Unit,
    onRegisterActivity: (String, String, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedBlockId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0E0D)).padding(16.dp)) {
        Text("CENTRO DE OPERACIONES", color = Color(0xFFC67C2C), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFC67C2C)))
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(blocks) { block ->
                BlockItemComponent(
                    block = block,
                    missions = missions, // 🔥 AÑADE ESTA LÍNEA AQUÍ
                    onClick = { onBlockClick(block.id) },
                    onAddClick = {
                        selectedBlockId = block.id
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && selectedBlockId != null) {
        var amountStr by remember { mutableStateOf("1") }
        val filteredMissions = missions.filter { !it.isCompleted && it.blockId == selectedBlockId }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Registrar avance en el bloque", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (filteredMissions.isEmpty()) {
                        Text("No hay misiones activas para este bloque.", color = Color.Gray)
                    } else {
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it },
                            label = { Text("Cantidad a sumar (Horas / Páginas)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Selecciona la misión destino:", color = Color.Gray, fontSize = 14.sp)

                        filteredMissions.forEach { mission ->
                            Button(
                                onClick = {
                                    val rawAmount = amountStr.toIntOrNull() ?: 1
                                    val finalAmount = if (mission.unit == dam.pmdm.lifeblocks.data.MissionUnit.TIME) rawAmount * 60 else rawAmount

                                    onRegisterActivity(selectedBlockId!!, mission.id, finalAmount)
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF5D5D5D))
                            ) {
                                Text(mission.name, color = Color(0xFFE3C096))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate("create_mission/${selectedBlockId}")
                    showDialog = false
                }) {
                    Text("CREAR NUEVA MISIÓN", color = Color(0xFFC67C2C))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
}

@Composable
fun BlockItemComponent(
    block: BlockEntity,
    missions: List<MissionEntity>, // 💡 Le pasamos las misiones para calcular el progreso real
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF5D5D5D)).background(Color(0xFF1E1E1E)).padding(16.dp).clickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(block.name.uppercase(), color = Color(0xFFE3C096), fontWeight = FontWeight.Bold)
            Text("+", color = Color(0xFFC67C2C), fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onAddClick() })
        }
        Text(text = block.realObjective, color = Color(0xFF888888), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // ⚡ CÁLCULO EN TIEMPO REAL SIN BUCLES INFINITOS
        // Filtramos las misiones que pertenecen a este bloque específico
        val blockMissions = missions.filter { it.blockId == block.id }

        val progress = if (blockMissions.isEmpty()) {
            0f
        } else {
            var totalTarget = 0f
            var totalCurrent = 0f

            for (mission in blockMissions) {
                if (mission.unit == dam.pmdm.lifeblocks.data.MissionUnit.CHECK) {
                    totalTarget += 1f
                    if (mission.isCompleted) totalCurrent += 1f
                } else {
                    totalTarget += mission.targetValue
                    totalCurrent += mission.currentValue
                }
            }
            if (totalTarget > 0) (totalCurrent / totalTarget).coerceIn(0f, 1f) else 0f
        }

        // Pintamos la barra con el porcentaje real calculado
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).border(1.dp, Color(0xFFC67C2C))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color(0xFFC67C2C)))
        }
    }
}