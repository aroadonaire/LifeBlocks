package dam.pmdm.lifeblocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dam.pmdm.lifeblocks.data.AppDatabase
import dam.pmdm.lifeblocks.data.LibraryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val libraryDao = AppDatabase.getDatabase(application).libraryDao()

    // Carga de forma reactiva todos los elementos guardados en la biblioteca
    val libraryItems = libraryDao.getAllItems()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Función para guardar un nuevo recurso (Libro, Curso, Serie...)
    fun addLibraryItem(blockId: String, title: String, author: String, type: String, total: Int) {
        viewModelScope.launch {
            val newItem = LibraryEntity(
                id = System.currentTimeMillis().toString(), // ID único basado en tiempo
                blockId = blockId,
                title = title,
                authorOrPlatform = author,
                type = type,
                totalPagesOrChapters = total,
                isCompleted = false
            )
            libraryDao.insertItem(newItem)
        }
    }

    // Cambiar el estado de completado si terminas un libro o serie
    fun toggleCompletion(itemId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            libraryDao.updateCompletionStatus(itemId, !currentStatus)
        }
    }
}