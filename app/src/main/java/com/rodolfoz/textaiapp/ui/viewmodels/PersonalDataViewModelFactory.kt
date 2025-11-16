/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rodolfoz.textaiapp.data.UserDataDao

/**
 * Factory for creating instances of [PersonalDataViewModel].
 *
 * This factory ensures that the [PersonalDataViewModel] is created with the required
 * [UserDataDao] dependency.
 *
 * @property userDataDao The DAO used to interact with the user data in the database.
 */
class PersonalDataViewModelFactory(private val userDataDao: UserDataDao) :
    ViewModelProvider.Factory {
    private val TAG = "TAA:" + this::class.java.simpleName

    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the specified ViewModel.
     * @throws IllegalArgumentException If the ViewModel class is not assignable from [PersonalDataViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: $modelClass")

        if (modelClass.isAssignableFrom(PersonalDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalDataViewModel(userDataDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
