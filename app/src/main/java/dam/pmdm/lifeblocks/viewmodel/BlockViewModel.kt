package dam.pmdm.lifeblocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dam.pmdm.lifeblocks.data.AppDatabase
import dam.pmdm.lifeblocks.BlockEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BlocksViewModel(private val application: Application) : AndroidViewModel(application) {

    private val blockDao = AppDatabase.getDatabase(application).blockDao()
    private val missionDao = AppDatabase.getDatabase(application).missionDao()

    val blocks = blockDao.getAllBlocks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        insertDefaultBlocks()
    }

    private fun insertDefaultBlocks() {
        viewModelScope.launch {
            // Evitamos duplicar bloques si ya existen en la base de datos
            if (blockDao.getBlockCount() > 0) return@launch

            // 🗺️ Mapeamos tus 8 bloques con tus objetivos reales, métricas y metas
            val defaultBlocks = listOf(
                BlockEntity("studies", "🎓 Estudios", "Conseguir empleo de desarrolladora Java/JS", "HOURS", 400),
                BlockEntity("english", "🇬🇧 Inglés", "Certificar nivel B2 alto de Inglés", "HOURS", 250),
                BlockEntity("fitness", "💪 Salud Física", "Perder 10kg y consolidar hábitos saludables", "POINTS", 100),
                BlockEntity("mental", "🧠 Salud Mental", "Fortalecer mi paz interior y resiliencia", "POINTS", 100),
                BlockEntity("finance", "💰 Finanzas", "Aprender a controlar y gestionar mis ahorros", "POINTS", 100),
                BlockEntity("growth", "📚 Des. Personal", "Completar proyectos de maquetas y lectura", "POINTS", 100),
                BlockEntity("social", "👥 Relaciones", "Organizar planes de calidad y romper rutinas", "POINTS", 100),
                BlockEntity("fun", "✨ Ocio Selecto", "Consumir obras maestras de cine y series", "POINTS", 100)
            )

            // Los insertamos en la base de datos de forma segura
            for (block in defaultBlocks) {
                blockDao.insertBlock(block)
            }
        }
    }
}