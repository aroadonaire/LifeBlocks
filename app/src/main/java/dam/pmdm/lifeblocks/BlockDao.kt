package dam.pmdm.lifeblocks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

    @Query("SELECT * FROM blocks")
    fun getAllBlocks(): Flow<List<BlockEntity>>

    @Query("SELECT COUNT(*) FROM blocks")
    suspend fun getBlockCount(): Int

    // 🔥 CORREGIDO: Cambiado "missions" por "misiones" para que coincida con la Entidad
    @Query("SELECT COALESCE(SUM(currentValue), 0) FROM misiones WHERE blockId = :blockId")
    fun getInvertedMinutesInBlock(blockId: String): Flow<Int>

    // 🔥 CORREGIDO: Cambiado "missions" por "misiones" para que coincida con la Entidad
    @Query("SELECT COALESCE(SUM(pointsReward), 0) FROM misiones WHERE blockId = :blockId AND isCompleted = 1")
    fun getEarnedPointsInBlock(blockId: String): Flow<Int>
}