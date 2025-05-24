package com.rodolfoz.textaiapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodolfoz.textaiapp.ui.PersonalDataUI
import com.rodolfoz.textaiapp.ui.PromptAndResponseUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "PersonalDataUI") {
                composable("PersonalDataUI") { PersonalDataUI(navController) }
                composable("PromptAndResponseUI") { PromptAndResponseUI(navController) }
            }
        }
    }
}