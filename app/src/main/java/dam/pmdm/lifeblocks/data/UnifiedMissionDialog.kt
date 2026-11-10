package dam.pmdm.lifeblocks.data

import android.text.Layout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment


@Composable
fun UnifiedMissionDialog(
    blockId: String,
    onDismiss: () -> Unit,
    onSave: (String, Int, MissionBehavior, MissionUnit) -> Unit,
    onUpdate: (MissionEntity) -> Unit,
    allMissions: List<MissionEntity>
) {
    var selectedTab by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var behavior by remember { mutableStateOf(MissionBehavior.PROJECT) }
    var unit by remember { mutableStateOf(MissionUnit.TIME) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = {
            Column {
                TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CREAR", color = Color(0xFFC67C2C)) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("ACTUALIZAR", color = Color(0xFFC67C2C)) })
                }
            }
        },
        text = {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (selectedTab == 0) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Meta") })

                    // Selectores de Tipo (RadioButtons)
                    Text("Comportamiento:", color = Color.White)
                    MissionBehavior.values().forEach { b ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = behavior == b, onClick = { behavior = b })
                            Text(b.name, color = Color.White)
                        }
                    }
                } else {
                    // Pestaña Actualizar: Lista de misiones del bloque
                    LazyColumn {
                        items(allMissions.filter { it.blockId == blockId }) { mission ->
                            Button(onClick = {
                                // Lógica para actualizar (puedes poner otro diálogo aquí para meter el nuevo valor)
                                onUpdate(mission.copy(currentValue = mission.currentValue + 10))
                            }) {
                                Text("Añadir 10 a ${mission.name}")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 0) {
                Button(onClick = { onSave(name, target.toIntOrNull() ?: 0, behavior, unit) }) {
                    Text("CREAR")
                }
            }
        }
    )
}