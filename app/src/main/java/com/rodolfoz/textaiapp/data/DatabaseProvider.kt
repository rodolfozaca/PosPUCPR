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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
            // Migration 1 -> 2: adiciona colunas `login` e `password` na tabela user_data
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Adiciona colunas com valor padrão vazio para manter compatibilidade
                    db.execSQL("ALTER TABLE user_data ADD COLUMN login TEXT DEFAULT '' NOT NULL")
                    db.execSQL("ALTER TABLE user_data ADD COLUMN password TEXT DEFAULT '' NOT NULL")
                }
            }

            // Migration 2 -> 3: adiciona coluna `firebaseUid` na tabela user_data
            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE user_data ADD COLUMN firebaseUid TEXT DEFAULT '' NOT NULL")
                }
            }

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "app_database"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
