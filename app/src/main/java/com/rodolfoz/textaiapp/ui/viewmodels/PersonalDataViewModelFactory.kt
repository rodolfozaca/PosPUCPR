package com.rodolfoz.textaiapp.ui.viewmodels

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

    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the specified ViewModel.
     * @throws IllegalArgumentException If the ViewModel class is not assignable from [PersonalDataViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalDataViewModel(userDataDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
