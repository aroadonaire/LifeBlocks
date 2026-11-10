package dam.pmdm.lifeblocks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. ¿Cómo se comporta la misión en el tiempo?
enum class MissionBehavior {
    PROJECT,     // Acumulativo (ej: Curso de 170h, Libro de 350 pág). No se borra.
    DAILY_HABIT  // Reiniciable (ej: Meditar 15 min al día, Leer 5 pág). Se vacía a las 5:00 AM.
}

// 2. ¿En qué unidad estamos midiendo este objetivo?
enum class MissionUnit {
    TIME,        // Medimos en minutos por detrás (se muestra en horas/minutos en la UI)
    PAGES,       // Medimos en páginas o unidades físicas (ej: 5 páginas, 2 litros de agua)
    CHECK        // Sí / No (ej: Hacer la cama, tomar suplementos)
}

// 3. La tabla unificada de misiones en la Base de Datos
@Entity(tableName = "misiones")
data class MissionEntity(
    @PrimaryKey val id: String,
    val blockId: String,               // A qué bloque pertenece (Estudios, Salud...)
    val name: String,                  // Nombre: "Curso de Java", "Leer El Quijote"
    val behavior: MissionBehavior,     // PROJECT o DAILY_HABIT
    val unit: MissionUnit,             // TIME, PAGES o CHECK
    val currentValue: Int,             // Lo que llevas hecho hoy o acumulado (minutos, páginas, o 0/1)
    val targetValue: Int,              // La meta final (ej: 10200 min (170h), 350 páginas, o 1 para checks)
    val pointsReward: Int,             // Puntos opcionales si quieres usar el sistema Gamer
    val isCompleted: Boolean,          // Si se ha marcado como finiquitado
    val lastResetTimestamp: Long = 0L  // 🔥 Crucial: Guarda la última vez que se reseteó (para el truco de las 5 AM)
)