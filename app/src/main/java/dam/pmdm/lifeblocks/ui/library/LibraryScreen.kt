package dam.pmdm.lifeblocks.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dam.pmdm.lifeblocks.data.LibraryEntity
import dam.pmdm.lifeblocks.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val items by viewModel.libraryItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MI BIBLIOTECA DE RECURSOS",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Gestiona tus libros, cursos y material de apoyo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Biblioteca vacía. ¡Pulsa + para añadir material!", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { libraryItem ->
                        LibraryCard(
                            item = libraryItem,
                            onToggleComplete = { viewModel.toggleCompletion(libraryItem.id, libraryItem.isCompleted) }
                        )
                    }
                }
            }
        }

        // Botón Flotante Estilo Industrial
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir a Biblioteca")
        }

        if (showAddDialog) {
            AddLibraryItemDialog(
                onDismiss = { showAddDialog = false },
                onSave = { blockId, title, author, type, total ->
                    viewModel.addLibraryItem(blockId, title, author, type, total)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun LibraryCard(item: LibraryEntity, onToggleComplete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.type} • ${item.authorOrPlatform}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Tamaño: ${item.totalPagesOrChapters} pág/cap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Checkbox/Botón para marcar como terminado
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLibraryItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("10") }

    // Opciones para los selectores
    val blockOptions = listOf(
        "studies" to "🎓 Java/JS",
        "english" to "🇬🇧 Inglés",
        "fitness" to "💪 Salud",
        "mental" to "🧠 Mente",
        "finance" to "💰 Finanzas",
        "growth" to "📚 Desarrollo",
        "social" to "👥 Relaciones",
        "fun" to "✨ Ocio"
    )
    var selectedBlockIndex by remember { mutableStateOf(0) }
    var blockDropdownExpanded by remember { mutableStateOf(false) }

    val typeOptions = listOf("BOOK", "COURSE", "MEDIA")
    var selectedTypeIndex by remember { mutableStateOf(0) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("AÑADIR RECURSO", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del recurso") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor, Plataforma o Canal") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Bloque Destino
                Box {
                    OutlinedButton(onClick = { blockDropdownExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Bloque: ${blockOptions[selectedBlockIndex].second}")
                    }
                    DropdownMenu(expanded = blockDropdownExpanded, onDismissRequest = { blockDropdownExpanded = false }) {
                        blockOptions.forEachIndexed { index, pair ->
                            DropdownMenuItem(
                                text = { Text(pair.second) },
                                onClick = { selectedBlockIndex = index; blockDropdownExpanded = false }
                            )
                        }
                    }
                }

                // Selector de Tipo de Material
                Box {
                    OutlinedButton(onClick = { typeDropdownExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Tipo: ${typeOptions[selectedTypeIndex]}")
                    }
                    DropdownMenu(expanded = typeDropdownExpanded, onDismissRequest = { typeDropdownExpanded = false }) {
                        typeOptions.forEachIndexed { index, type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { selectedTypeIndex = index; typeDropdownExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it },
                    label = { Text("Total Páginas o Capítulos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            blockOptions[selectedBlockIndex].first,
                            title,
                            author,
                            typeOptions[selectedTypeIndex],
                            total.toIntOrNull() ?: 10
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Guardar", color = MaterialTheme.colorScheme.onPrimary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) }
        }
    )
}