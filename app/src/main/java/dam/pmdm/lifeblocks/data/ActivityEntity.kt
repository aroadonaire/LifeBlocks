package dam.pmdm.lifeblocks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val blockId: String,               // Ej: "studies"
    val libraryItemId: String? = null, // Ej: "Java Udemy" (Opcional)
    val missionId: String? = null,     // Qué misión directa ha alimentado (Opcional)
    val name: String,                  // Ej: "Proyecto Java"
    val durationMinutes: Int,          // Minutos invertidos (para calcular la intensidad del calendario)
    val timestamp: Long = System.currentTimeMillis() // Fecha y hora exacta del registro
)