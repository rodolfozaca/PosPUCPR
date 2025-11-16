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
import androidx.lifecycle.viewModelScope
import com.rodolfoz.textaiapp.data.UserRepository
import com.rodolfoz.textaiapp.data.model.UserDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing personal user data using a repository abstraction.
 */
class PersonalDataViewModel(private val repository: UserRepository) : ViewModel() {

    private val TAG = "TAA:" + this::class.java.simpleName

    /**
     * Saves user data to the database.
     *
     * This method performs the database operation on a background thread and
     * invokes the appropriate callback on the main thread upon success or failure.
     *
     * @param user The user data to save.
     * @param onSuccess Callback invoked when the operation is successful.
     * @param onError Callback invoked when an error occurs, with the error message.
     */
    fun saveUserData(user: UserDataModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d(TAG, "saveUserData: $user")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertUser(user)
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    onError(e.message ?: "Error saving user data")
                }
            }
        }
    }

    fun getUserName(onResult: (String?) -> Unit) {
        Log.d(TAG, "getUserName")

        viewModelScope.launch {
            val user = repository.getUserById(1)
            onResult(user?.name)
        }
    }
}
