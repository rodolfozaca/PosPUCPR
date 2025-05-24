package com.rodolfoz.textaiapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all user data.
     */
    @Query("SELECT * FROM user_data")
    suspend fun getAllUsers(): List<UserDataModel>
}
