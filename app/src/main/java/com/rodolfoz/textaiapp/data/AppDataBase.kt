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
 * Version bumped to 2 to accommodate new fields `login` and `password` in the
 * [UserDataModel]. A migration from 1 -> 2 must be supplied by the database provider.
 */
@Database(entities = [UserDataModel::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Abstract method to get the UserDataDao.
     */
    abstract fun userDataDao(): UserDataDao
}
