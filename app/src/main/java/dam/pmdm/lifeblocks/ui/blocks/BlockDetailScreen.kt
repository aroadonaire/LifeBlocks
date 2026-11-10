package dam.pmdm.lifeblocks.ui.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import dam.pmdm.lifeblocks.data.MissionBehavior
import dam.pmdm.lifeblocks.data.MissionEntity
import dam.pmdm.lifeblocks.data.MissionUnit
import dam.pmdm.lifeblocks.data.UnifiedMissionDialog
import dam.pmdm.lifeblocks.viewmodel.ActivityViewModel

@Composable
fun BlockDetailsScreen(
    blockId: String,
    onBack: () -> Unit,
    viewModel: BlockDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    activityViewModel: ActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val missions by viewModel.getMissions(blockId).collectAsState(initial = emptyList())
    var showUnifiedDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("⬅ Volver", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PANEL DE PROYECTOS Y MISIONES",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (missions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay misiones activas en este bloque. ¡Pulsa '+'!", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(missions) { mission ->
                        MissionCard(mission = mission, onCompleteClick = { viewModel.completeMission(mission) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showUnifiedDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Gestionar Bloque")
        }

        if (showUnifiedDialog) {
            UnifiedActionDialog(
                missions = missions,
                onDismiss = { showUnifiedDialog = false },
                onCreateMission = { name, behavior, unit, points, targetValue ->
                    viewModel.addCustomMission(blockId, name, behavior, unit, points, targetValue)
                    showUnifiedDialog = false
                },
                onLogTime = { chosenMissionId, finalMinutes, activityName ->
                    activityViewModel.registerActivity(
                        blockId = blockId,
                        missionId = chosenMissionId,
                        name = activityName,
                        durationMinutes = finalMinutes
                    )
                    showUnifiedDialog = false
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedActionDialog(
    missions: List<MissionEntity>,
    onDismiss: () -> Unit,
    onCreateMission: (String, MissionBehavior, MissionUnit, Int, Int) -> Unit,
    onLogTime: (String, Int, String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Campos comunes
    var nameOrComment by remember { mutableStateOf("") }
    var inputAmountStr by remember { mutableStateOf("1") }
    var pointsRewardStr by remember { mutableStateOf("100") }

    // Selectores de tipo de misión (Creación)
    var selectedBehavior by remember { mutableStateOf(MissionBehavior.PROJECT) }
    var selectedUnit by remember { mutableStateOf(MissionUnit.TIME) }
    var behaviorDropdownExpanded by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    // Control para la pestaña de actualización
    var selectedMissionForUpdate by remember { mutableStateOf<MissionEntity?>(missions.firstOrNull { !it.isCompleted }) }
    var missionDropdownExpanded by remember { mutableStateOf(false) }
    var isHoursInputSelected by remember { mutableStateOf(true) } // Para cambiar entre Horas y Minutos

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("NUEVA MISIÓN", modifier = Modifier.padding(8.dp))
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("SUMAR AVANCE", modifier = Modifier.padding(8.dp))
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedTabIndex == 0) {
                    // ---------------- PESTAÑA: CREAR ----------------
                    OutlinedTextField(
                        value = nameOrComment,
                        onValueChange = { nameOrComment = it },
                        label = { Text("Nombre de la misión") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Selector de Comportamiento (Proyecto vs Hábito)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { behaviorDropdownExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Tipo: ${if (selectedBehavior == MissionBehavior.DAILY_HABIT) "HÁBITO DIARIO" else "PROYECTO RETO"}")
                        }
                        DropdownMenu(expanded = behaviorDropdownExpanded, onDismissRequest = { behaviorDropdownExpanded = false }) {
                            DropdownMenuItem(text = { Text("PROYECTO RETO (Acumulativo)") }, onClick = { selectedBehavior = MissionBehavior.PROJECT; behaviorDropdownExpanded = false })
                            DropdownMenuItem(text = { Text("HÁBITO DIARIO (Resetea 5 AM)") }, onClick = { selectedBehavior = MissionBehavior.DAILY_HABIT; behaviorDropdownExpanded = false })
                        }
                    }

                    // Selector de Unidad de Medida
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { unitDropdownExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Medición: ${selectedUnit.name}")
                        }
                        DropdownMenu(expanded = unitDropdownExpanded, onDismissRequest = { unitDropdownExpanded = false }) {
                            DropdownMenuItem(text = { Text("TIEMPO (Horas/Minutos)") }, onClick = { selectedUnit = MissionUnit.TIME; unitDropdownExpanded = false })
                            DropdownMenuItem(text = { Text("PÁGINAS / UNIDADES") }, onClick = { selectedUnit = MissionUnit.PAGES; unitDropdownExpanded = false })
                            DropdownMenuItem(text = { Text("CHECK (Sí / No)") }, onClick = { selectedUnit = MissionUnit.CHECK; unitDropdownExpanded = false })
                        }
                    }

                    // Campo de Meta Numérica (Oculto si es un simple check)
                    if (selectedUnit != MissionUnit.CHECK) {
                        OutlinedTextField(
                            value = inputAmountStr,
                            onValueChange = { inputAmountStr = it },
                            label = { Text(if (selectedUnit == MissionUnit.TIME) "Meta en Horas" else "Meta en Páginas/Unidades") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = pointsRewardStr,
                        onValueChange = { pointsRewardStr = it },
                        label = { Text("Puntos de Recompensa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    // ---------------- PESTAÑA: ACTUALIZAR ----------------
                    if (missions.none { !it.isCompleted }) {
                        Text("No tienes misiones activas para actualizar en este bloque.", color = MaterialTheme.colorScheme.error)
                    } else {
                        // Selector de Misión Existente
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { missionDropdownExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Misión: ${selectedMissionForUpdate?.name ?: "Seleccionar"}")
                            }
                            DropdownMenu(expanded = missionDropdownExpanded, onDismissRequest = { missionDropdownExpanded = false }) {
                                missions.filter { !it.isCompleted }.forEach { mission ->
                                    DropdownMenuItem(text = { Text(mission.name) }, onClick = { selectedMissionForUpdate = mission; missionDropdownExpanded = false })
                                }
                            }
                        }

                        selectedMissionForUpdate?.let { mission ->
                            when (mission.unit) {
                                MissionUnit.TIME -> {
                                    // Selector de entrada de tiempo (Horas o Minutos)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = isHoursInputSelected, onClick = { isHoursInputSelected = true })
                                        Text("Horas")
                                        RadioButton(selected = !isHoursInputSelected, onClick = { isHoursInputSelected = false })
                                        Text("Minutos")
                                    }
                                    OutlinedTextField(
                                        value = inputAmountStr,
                                        onValueChange = { inputAmountStr = it },
                                        label = { Text(if (isHoursInputSelected) "Cantidad de Horas" else "Cantidad de Minutos") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                MissionUnit.PAGES -> {
                                    OutlinedTextField(
                                        value = inputAmountStr,
                                        onValueChange = { inputAmountStr = it },
                                        label = { Text("Páginas leídas / Unidades completadas") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                MissionUnit.CHECK -> {
                                    Text("Esta misión es un Check. Solo pulsa el botón inferior para marcarla como hecha por hoy.", fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = nameOrComment,
                                onValueChange = { nameOrComment = it },
                                label = { Text("Comentario / Nota de actividad") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTabIndex == 0) {
                        // Lógica de Guardar Nueva Misión
                        val points = pointsRewardStr.toIntOrNull() ?: 100
                        val rawTarget = inputAmountStr.toIntOrNull() ?: 1
                        // Si es tiempo, convertimos las horas del usuario a minutos por detrás
                        val finalTarget = if (selectedUnit == MissionUnit.TIME) rawTarget * 60 else rawTarget

                        onCreateMission(nameOrComment, selectedBehavior, selectedUnit, points, finalTarget)
                    } else {
                        // Lógica de Actualizar Progreso Existente
                        selectedMissionForUpdate?.let { mission ->
                            val amount = inputAmountStr.toIntOrNull() ?: 0
                            val finalMinutes = when (mission.unit) {
                                MissionUnit.TIME -> if (isHoursInputSelected) amount * 60 else amount
                                MissionUnit.PAGES -> amount
                                MissionUnit.CHECK -> 1 // El check suma 1 para completarse
                            }
                            onLogTime(mission.id, finalMinutes, nameOrComment.ifBlank { "Avance en ${mission.name}" })
                        }
                    }
                }
            ) {
                Text(if (selectedTabIndex == 0) "INICIALIZAR" else "REGISTRAR AVANCE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}
@Composable
fun IndustrialAddButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .padding(24.dp)
            .size(60.dp)
            .border(3.dp, Color(0xFFC67C2C))
            .background(Color(0xFF0F0E0D))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "+", color = Color(0xFFC67C2C), fontSize = 30.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun IndustrialMissionComponent(mission: MissionEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border(4.dp, Color(0xFF5D5D5D))
            .background(Color(0xFF1E1E1E))
            .padding(20.dp)
    ) {
        Text(
            text = mission.name.uppercase(),
            color = Color(0xFFE3C096),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Indicador mecánico
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(Color.Black)) {
            val progress = (mission.currentValue.toFloat() / mission.targetValue).coerceIn(0f, 1f)
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).background(Color(0xFFC67C2C)))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Pistón
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(2.dp, Color(0xFFC67C2C))
                .background(if (mission.isCompleted) Color(0xFF2C2C2C) else Color(0xFFC67C2C))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (mission.isCompleted) "MECANISMO CERRADO" else "ACCIONAR PISTÓN",
                color = if (mission.isCompleted) Color(0xFF5D5D5D) else Color.Black,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            )
        }
    }
}
@Composable
fun MissionCreationDialog(blockId: String, onDismiss: () -> Unit, onSave: (String, Int, MissionBehavior, MissionUnit) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = { Text("CONFIGURAR NUEVA MISIÓN", color = Color(0xFFC67C2C)) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre misión") })
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Meta numérica") })
                // Aquí podrías añadir RadioButtons para elegir MissionBehavior.PROJECT/DAILY_HABIT y MissionUnit
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, target.toIntOrNull() ?: 0, MissionBehavior.PROJECT, MissionUnit.TIME) }) {
                Text("INICIALIZAR")
            }
        }
    )
}
@Composable
fun MissionCard(mission: MissionEntity, onCompleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mission.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "+${mission.pointsReward} PTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = { },
                    label = {
                        Text(
                            text = if (mission.behavior == MissionBehavior.DAILY_HABIT) "HÁBITO DIARIO" else "PROYECTO RETO",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(labelColor = MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val progressText = when (mission.unit) {
                MissionUnit.TIME -> {
                    val currentHours = mission.currentValue.toFloat() / 60f
                    val targetHours = mission.targetValue.toFloat() / 60f
                    "${String.format("%.1f", currentHours)} / ${String.format("%.1f", targetHours)} h"
                }
                MissionUnit.PAGES -> "${mission.currentValue} / ${mission.targetValue} págs"
                MissionUnit.CHECK -> if (mission.isCompleted) "Completado hoy" else "Pendiente para hoy"
            }

            Text(
                text = progressText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (mission.unit != MissionUnit.CHECK) {
                val progress = if (mission.targetValue > 0) {
                    (mission.currentValue.toFloat() / mission.targetValue).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!mission.isCompleted) {
                Button(
                    onClick = onCompleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = if (mission.unit == MissionUnit.CHECK) "Marcar como hecho" else "Completar Misión")
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2E3B24), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✔ Misión Completada", color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}