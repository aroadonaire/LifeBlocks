package dam.pmdm.lifeblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dam.pmdm.lifeblocks.ui.blocks.BlocksScreen
import dam.pmdm.lifeblocks.ui.library.LibraryScreen
import dam.pmdm.lifeblocks.ui.theme.LifeBlocksTheme
import dam.pmdm.lifeblocks.viewmodel.ActivityViewModel
import dam.pmdm.lifeblocks.viewmodel.BlocksViewModel
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeBlocksTheme {
                // 1. Creamos el navController aquí
                val navController = rememberNavController()

                // 2. Obtenemos los ViewModels necesarios
                val activityViewModel: ActivityViewModel = viewModel()
                val blockViewModel: BlocksViewModel = viewModel()

                // 3. Pasamos todo a tu pantalla principal
                MainContainerScreen(
                    navController = navController,
                    activityViewModel = activityViewModel,
                    blockViewModel = blockViewModel
                )
            }
        }
    }
}

@Composable
fun MainContainerScreen(
    navController: NavController,
    activityViewModel: ActivityViewModel = viewModel(),
    blockViewModel: BlocksViewModel = viewModel() // 👈 Añade esta línea
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentBlockDetailsId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            if (currentBlockDetailsId == null) {
                // Estilo Industrial para la Barra de Navegación
                NavigationBar(
                    containerColor = Color(0xFF0F0E0D), // Fondo Carbón
                    tonalElevation = 0.dp // Quitamos sombras estándar
                ) {
                    val navItems = listOf("Objetivos", "Biblioteca", "Historial")

                    navItems.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            label = { Text(label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                            icon = {
                                Icon(
                                    imageVector = if(index == 0) Icons.Default.Home else if(index == 1) Icons.Default.List else Icons.Default.Refresh,
                                    contentDescription = label
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F0E0D), // Color al estar seleccionado (Icono)
                                indicatorColor = Color(0xFFC67C2C),    // El óvalo ahora es COBRE
                                unselectedIconColor = Color(0xFF5D5D5D),
                                unselectedTextColor = Color(0xFF5D5D5D)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF0F0E0D)) // Fondo global negro
        ) {
            if (currentBlockDetailsId != null) {
                dam.pmdm.lifeblocks.ui.blocks.BlockDetailsScreen(
                    blockId = currentBlockDetailsId!!,
                    onBack = { currentBlockDetailsId = null }
                )
            } else {
                when (selectedTab) {
                    0 -> {
                        val blocksList by blockViewModel.blocks.collectAsState(initial = emptyList())
                        val missionList by activityViewModel.allMissions.collectAsState(initial = emptyList())

                        BlocksScreen(
                            navController = navController, // 👈 ¡ESTE ES EL QUE FALTABA!
                            blocks = blocksList,
                            missions = missionList,
                            onBlockClick = { id -> currentBlockDetailsId = id },
                            onRegisterActivity = { blockId, missionId, minutes ->
                                activityViewModel.registerActivity(blockId, missionId, "Registro", minutes)
                            }
                        )
                    }
                    1 -> LibraryScreen()
                    // ... dentro del when(selectedTab)
                    2 -> {
                        // Especificamos explícitamente que es una Lista de ActivityEntity
                        val activityList: List<dam.pmdm.lifeblocks.data.ActivityEntity> by activityViewModel.activitiesLog.collectAsState(initial = emptyList())

                        dam.pmdm.lifeblocks.ui.history.HistoryScreen(historyList = activityList)
                    }
                }
            }
        }
    }
}