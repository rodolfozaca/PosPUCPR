package com.rodolfoz.textaiapp.data

import android.content.Context
import androidx.room.Room

/**
 * Provides a singleton instance of the AppDataBase.
 *
 * This object ensures that only one instance of the database is created and shared
 * throughout the application.
 */
object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDataBase? = null

    /**
     * Retrieves the singleton instance of the AppDataBase.
     *
     * If the database instance does not exist, it creates a new one using the Room database builder.
     *
     * @param context The application context used to initialize the database.
     * @return The singleton instance of the AppDataBase.
     */
    fun getDatabase(context: Context): AppDataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
