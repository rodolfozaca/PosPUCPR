/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.ui.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodolfoz.textaiapp.data.DatabaseProvider
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModel
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModelFactory

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
    val repository = com.rodolfoz.textaiapp.data.UserRepository(userDataDao)
    val viewModel: PersonalDataViewModel = viewModel(
        factory = PersonalDataViewModelFactory(repository)
    )

    PersonalDataUI(navController = navController, viewModel = viewModel)
}
