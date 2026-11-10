package dam.pmdm.lifeblocks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dam.pmdm.lifeblocks.MainContainerScreen
import dam.pmdm.lifeblocks.ui.blocks.BlocksScreen
import dam.pmdm.lifeblocks.ui.blocks.BlockDetailsScreen
import dam.pmdm.lifeblocks.viewmodel.ActivityViewModel
import dam.pmdm.lifeblocks.viewmodel.BlocksViewModel

@Composable
fun LifeBlocksApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "blocks"
    ) {

        // Dentro de tu NavHost en LifeBlocksApp.kt
        composable("main_screen") {
            // Necesitas los ViewModels aquí también para pasarlos
            val activityViewModel: ActivityViewModel = viewModel()
            val blockViewModel: BlocksViewModel = viewModel()

            // Llamamos a MainContainerScreen pasándole los parámetros
            MainContainerScreen(
                navController = navController,
                activityViewModel = activityViewModel,
                blockViewModel = blockViewModel
            )
        }

        composable(
            route = "details/{blockId}",
            arguments = listOf(
                navArgument("blockId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val blockId = backStackEntry.arguments?.getString("blockId")!!

            BlockDetailsScreen(
                blockId = blockId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}