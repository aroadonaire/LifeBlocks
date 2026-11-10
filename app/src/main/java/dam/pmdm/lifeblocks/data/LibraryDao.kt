package dam.pmdm.lifeblocks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LibraryEntity)

    // Obtener toda la biblioteca completa
    @Query("SELECT * FROM library_items")
    fun getAllItems(): Flow<List<LibraryEntity>>

    // Filtrar la biblioteca por un bloque específico
    @Query("SELECT * FROM library_items WHERE blockId = :blockId")
    fun getItemsByBlock(blockId: String): Flow<List<LibraryEntity>>

    @Query("UPDATE library_items SET isCompleted = :completed WHERE id = :itemId")
    suspend fun updateCompletionStatus(itemId: String, completed: Boolean)
}