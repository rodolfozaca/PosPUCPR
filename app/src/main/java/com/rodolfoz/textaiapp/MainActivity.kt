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
package com.rodolfoz.textaiapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodolfoz.textaiapp.data.DatabaseProvider
import com.rodolfoz.textaiapp.ui.PersonalDataUI
import com.rodolfoz.textaiapp.ui.PromptAndResponseUI
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModel
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModelFactory

/**
 * MainActivity that is the entry point of the application.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "PromptAndResponseUI") {
                composable("PersonalDataUI") {
                    PersonalDataScreen(navController)
                }
                composable("PromptAndResponseUI") {
                    PromptAndResponseScreen(navController)
                }
            }
        }
    }
}

/**
 * Composable function that sets up the PersonalData screen.
 *
 * @param navController The NavController for navigation.
 */
@Composable
fun PersonalDataScreen(navController: androidx.navigation.NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = DatabaseProvider.getDatabase(context)
    val userDataDao = database.userDataDao()
    val viewModel: PersonalDataViewModel = viewModel(
        factory = PersonalDataViewModelFactory(userDataDao)
    )

    PersonalDataUI(navController = navController, viewModel = viewModel)
}


@Composable
fun PromptAndResponseScreen(navController: androidx.navigation.NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = DatabaseProvider.getDatabase(context)
    val userDataDao = database.userDataDao()
    val viewModel: PersonalDataViewModel = viewModel(
        factory = PersonalDataViewModelFactory(userDataDao)
    )

    PromptAndResponseUI(navController = navController, viewModel = viewModel)
}
