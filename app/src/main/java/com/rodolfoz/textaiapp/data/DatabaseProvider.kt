/*
 * Rodolfo Zacarias - 2025.
 *
 * All rights reserved. This software is the exclusive property of Rodolfo Zacarias.
 * Redistribution, modification, or use of this code is permitted only under the terms
 * of the GNU General Public License (GPL) as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
