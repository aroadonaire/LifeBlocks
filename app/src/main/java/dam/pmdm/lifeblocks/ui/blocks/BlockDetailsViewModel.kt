package dam.pmdm.lifeblocks.ui.blocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dam.pmdm.lifeblocks.data.AppDatabase
import dam.pmdm.lifeblocks.data.MissionBehavior
import dam.pmdm.lifeblocks.data.MissionEntity
import dam.pmdm.lifeblocks.data.MissionUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class BlockDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val missionDao = db.missionDao()

    // Devuelve las misiones del bloque actual y comprueba los reseteos en segundo plano
    fun getMissions(blockId: String): Flow<List<MissionEntity>> {
        checkAndResetDailyHabits() // ⏰ Aplica la regla de las 5:00 AM
        return missionDao.getMissionsByBlock(blockId)
    }

    fun completeMission(mission: MissionEntity) {
        viewModelScope.launch {
            if (mission.isCompleted) return@launch
            val updated = mission.copy(
                currentValue = mission.targetValue,
                isCompleted = true
            )
            missionDao.updateMission(updated)
        }
    }

    // Crea misiones con soporte flexible de comportamiento y unidades
    fun addCustomMission(
        blockId: String,
        name: String,
        behavior: MissionBehavior,
        unit: MissionUnit,
        points: Int,
        target: Int
    ) {
        viewModelScope.launch {
            val newMission = MissionEntity(
                id = System.currentTimeMillis().toString(),
                blockId = blockId,
                name = name,
                behavior = behavior, // PROJECT o DAILY_HABIT
                unit = unit,         // TIME, PAGES o CHECK
                pointsReward = points,
                currentValue = 0,
                targetValue = target,
                isCompleted = false,
                lastResetTimestamp = System.currentTimeMillis() // Fecha de nacimiento de la misión
            )
            missionDao.insertMission(newMission)
        }
    }

    // 🛌 EL MOTOR BIOLÓGICO DE LAS 5:00 AM
    private fun checkAndResetDailyHabits() {
        viewModelScope.launch {
            val currentCal = Calendar.getInstance()
            currentCal.add(Calendar.HOUR_OF_DAY, -5) // Restamos 5 horas para calcular el día biológico

            // Establecemos la marca de medianoche virtual
            currentCal.set(Calendar.HOUR_OF_DAY, 0)
            currentCal.set(Calendar.MINUTE, 0)
            currentCal.set(Calendar.SECOND, 0)
            currentCal.set(Calendar.MILLISECOND, 0)
            val limitTimestamp = currentCal.timeInMillis

            val dailyHabits = missionDao.getAllDailyHabits()
            for (habit in dailyHabits) {
                // Si el último reset ocurrió antes de las 5 AM de hoy, se limpia a 0
                if (habit.lastResetTimestamp < limitTimestamp) {
                    val resetHabit = habit.copy(
                        currentValue = 0,
                        isCompleted = false,
                        lastResetTimestamp = System.currentTimeMillis()
                    )
                    missionDao.updateMission(resetHabit)
                }
            }
        }
    }
    fun getBlockProgressPercentage(blockId: String): Flow<Float> {
        return missionDao.getMissionsByBlock(blockId).map { misiones ->
            if (misiones.isEmpty()) return@map 0f

            var totalTarget = 0f
            var totalCurrent = 0f

            for (mision in misiones) {
                // Los CHECKS valen 1 si están hechos, 0 si no
                if (mision.unit == MissionUnit.CHECK) {
                    totalTarget += 1f
                    if (mision.isCompleted) totalCurrent += 1f
                } else {
                    // Sumamos los objetivos y los avances de tiempo/páginas de forma proporcional
                    totalTarget += mision.targetValue
                    totalCurrent += mision.currentValue
                }
            }

            if (totalTarget > 0) (totalCurrent / totalTarget).coerceIn(0f, 1f) else 0f
        }
    }
}