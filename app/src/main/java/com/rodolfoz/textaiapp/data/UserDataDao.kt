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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodolfoz.textaiapp.data.model.UserDataModel

/**
 * Data Access Object (DAO) for performing database operations on the user data.
 */
@Dao
interface UserDataDao {

    /**
     * Inserts a user into the database.
     *
     * If a user with the same ID already exists, it will be replaced.
     *
     * @param user The user data to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDataModel)

    /**
     * Updates an existing user in the database.
     *
     * @param user The user data to update.
     */
    @Update
    suspend fun updateUser(user: UserDataModel)

    /**
     * Deletes a user from the database.
     *
     * @param user The user data to delete.
     */
    @Delete
    suspend fun deleteUser(user: UserDataModel)

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user data, or null if no user with the given ID exists.
     */
    @Query("SELECT * FROM user_data WHERE id= :id")
    suspend fun getUserById(id: Int): UserDataModel?
}
