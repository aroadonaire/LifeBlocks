package dam.pmdm.lifeblocks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dam.pmdm.lifeblocks.BlockDao
import dam.pmdm.lifeblocks.BlockEntity

@Database(
    entities = [
        BlockEntity::class,
        MissionEntity::class,
        LibraryEntity::class,  // 🔥 Nueva tabla
        ActivityEntity::class  // 🔥 Nueva tabla
    ],
    version = 5, // 🔥 Subimos la versión por los cambios estructurales
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun blockDao(): BlockDao
    abstract fun missionDao(): MissionDao
    abstract fun activityDao(): ActivityDao
    abstract fun libraryDao(): LibraryDao
    // En los próximos pasos añadiremos los DAOs de actividades y biblioteca si hacen falta

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeblocks_db"
                )
                    .fallbackToDestructiveMigration() // Borra la DB vieja y crea la nueva estructura
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}