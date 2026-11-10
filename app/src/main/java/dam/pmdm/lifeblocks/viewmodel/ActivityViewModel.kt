package dam.pmdm.lifeblocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dam.pmdm.lifeblocks.data.ActivityEntity
import dam.pmdm.lifeblocks.data.AppDatabase
import dam.pmdm.lifeblocks.data.MissionBehavior
import dam.pmdm.lifeblocks.data.MissionEntity
import dam.pmdm.lifeblocks.data.MissionUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val activityDao = db.activityDao()
    private val missionDao = db.missionDao()

    // Flujo de todas las misiones para la UI
    val allMissions = missionDao.getAllMissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activitiesLog = activityDao.getAllActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyEffortMap = activityDao.getAllActivities().map { activities ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val map = mutableMapOf<String, Int>()

        for (activity in activities) {
            val dateStr = sdf.format(Date(activity.timestamp))
            map[dateStr] = (map[dateStr] ?: 0) + activity.durationMinutes
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 🔥 CORREGIDO: Registro de actividad y actualización del bloque superior sin errores de DAO
    fun registerActivity(blockId: String, missionId: String, name: String, durationMinutes: Int) {
        viewModelScope.launch {
            // 1. Guardar la actividad en el historial
            activityDao.insertActivity(ActivityEntity(
                id = System.currentTimeMillis().toString(),
                blockId = blockId, missionId = missionId, name = name,
                durationMinutes = durationMinutes, timestamp = System.currentTimeMillis()
            ))

            // 2. Actualizar progreso de la misión individual
            val mission = missionDao.getMissionById(missionId)
            mission?.let {
                val updatedProgress = it.currentValue + durationMinutes
                missionDao.updateMission(it.copy(
                    currentValue = updatedProgress,
                    isCompleted = updatedProgress >= it.targetValue
                ))
            }
        }
    }
    // Dentro de ActivityViewModel.kt
    fun saveMission(blockId: String, name: String, target: Int) {
        viewModelScope.launch {
            val newMission = MissionEntity(
                id = System.currentTimeMillis().toString(),
                blockId = blockId,
                name = name,
                targetValue = target,
                currentValue = 0,
                isCompleted = false,
                behavior = MissionBehavior.PROJECT,
                unit = MissionUnit.TIME, // Debes usar el Enum MissionUnit también
                pointsReward = 0,
                lastResetTimestamp = 0L
            )
            missionDao.insertMission(newMission)
        }
    }
    fun saveFullMission(blockId: String, name: String, target: Int, behavior: MissionBehavior, unit: MissionUnit) {
        viewModelScope.launch {
            val newMission = MissionEntity(
                id = System.currentTimeMillis().toString(),
                blockId = blockId,
                name = name,
                behavior = behavior,
                unit = unit,
                targetValue = target,
                currentValue = 0,
                isCompleted = false,
                pointsReward = 10,
                lastResetTimestamp = 0L
            )
            missionDao.insertMission(newMission)
        }
    }

    // Ahora esta función está al mismo nivel que las demás y será visible
    fun updateMission(mission: MissionEntity) {
        viewModelScope.launch {
            missionDao.updateMission(mission)
        }
    }
}