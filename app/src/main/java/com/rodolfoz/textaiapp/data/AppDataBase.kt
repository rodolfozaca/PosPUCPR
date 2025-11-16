/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
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
