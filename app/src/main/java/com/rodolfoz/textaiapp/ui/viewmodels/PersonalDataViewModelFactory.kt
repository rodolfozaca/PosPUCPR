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
