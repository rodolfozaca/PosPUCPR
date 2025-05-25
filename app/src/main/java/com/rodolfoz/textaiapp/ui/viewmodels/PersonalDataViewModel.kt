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
package com.rodolfoz.textaiapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodolfoz.textaiapp.data.UserDataDao
import com.rodolfoz.textaiapp.data.model.UserDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing personal user data.
 *
 * This ViewModel interacts with the UserDataDao to perform database operations
 * and provides methods to save user data.
 *
 * @property userDataDao The DAO used to interact with the user data in the database.
 */
class PersonalDataViewModel(private val userDataDao: UserDataDao) : ViewModel() {

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
                userDataDao.insertUser(user)
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error saving user data")
            }
        }
    }

    fun getUserName(onResult: (String?) -> Unit) {
        Log.d(TAG, "getUserName")

        viewModelScope.launch {
            val user = userDataDao.getUserById(1)
            onResult(user?.name)
        }
    }
}
