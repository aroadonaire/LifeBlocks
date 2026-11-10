package dam.pmdm.lifeblocks.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    // Devuelve las misiones de un bloque en tiempo real para pintar la pantalla
    @Query("SELECT * FROM misiones WHERE blockId = :blockId")
    fun getMissionsByBlock(blockId: String): Flow<List<MissionEntity>>
    @Query("SELECT * FROM misiones")
    fun getAllMissions(): Flow<List<MissionEntity>>

    // Devuelve la lista directa (sin Flow) para operaciones internas del ViewModel
    @Query("SELECT * FROM misiones WHERE blockId = :blockId")
    suspend fun getMissionsByBlockRaw(blockId: String): List<MissionEntity>

    @Query("SELECT * FROM misiones WHERE id = :missionId")
    suspend fun getMissionById(missionId: String): MissionEntity?

    // 🔥 Obtiene todos los hábitos diarios del sistema para revisar el reseteo de las 5:00 AM
    @Query("SELECT * FROM misiones WHERE behavior = 'DAILY_HABIT'")
    suspend fun getAllDailyHabits(): List<MissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Update
    suspend fun updateMission(mission: MissionEntity)
}