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

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodolfoz.textaiapp.data.model.UserDataModel

/**
 * The main database of the application.
 *
 * This class defines the Room database configuration and serves as the main access point
 * for the underlying SQLite database.
 *
 * @property userDataDao Provides access to the UserDataDao for performing database operations.
 */
@Database(entities = [UserDataModel::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Abstract method to get the UserDataDao.
     *
     * @return An instance of UserDataDao to interact with user data in the database.
     */
    abstract fun userDataDao(): UserDataDao
}
