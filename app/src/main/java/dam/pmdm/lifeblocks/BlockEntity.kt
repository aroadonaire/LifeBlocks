package dam.pmdm.lifeblocks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey val id: String,
    val name: String,               // Nombre descriptivo original (ej: "🎓 Estudios")
    val realObjective: String,      // ¡NUEVO! El objetivo motivador (ej: "Conseguir empleo Java/JS")
    val metricType: String,         // ¡NUEVO! "HOURS" para tiempo, "POINTS" para esfuerzo
    val targetValue: Int,
    val currentValue: Int = 0// ¡NUEVO! El objetivo final (ej: 400 para horas, 100 para puntos)
)