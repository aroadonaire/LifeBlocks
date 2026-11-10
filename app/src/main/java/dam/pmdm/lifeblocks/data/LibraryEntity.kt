package dam.pmdm.lifeblocks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_items")
data class LibraryEntity(
    @PrimaryKey val id: String,
    val blockId: String,          // Vinculado a "studies", "english", "growth", etc.
    val title: String,            // Ej: "Curso Spring Boot" o "Serie Severance"
    val authorOrPlatform: String, // Ej: "Udemy", "Netflix", "Stephen King"
    val type: String,             // "BOOK" (Libro), "COURSE" (Curso), "MEDIA" (Serie/Película)
    val totalPagesOrChapters: Int, // Para medir el tamaño (ej: 400 páginas o 10 capítulos)
    val isCompleted: Boolean = false
)