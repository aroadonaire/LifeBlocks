package dam.pmdm.lifeblocks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    // Lee todas las actividades ordenadas por fecha (la más reciente primero)
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    // Filtra actividades de un bloque concreto (útil para estadísticas)
    @Query("SELECT * FROM activities WHERE blockId = :blockId ORDER BY timestamp DESC")
    fun getActivitiesByBlock(blockId: String): Flow<List<ActivityEntity>>
}