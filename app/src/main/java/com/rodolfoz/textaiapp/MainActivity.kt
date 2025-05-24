package com.rodolfoz.textaiapp

import android.app.VoiceInteractor.Prompt
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
