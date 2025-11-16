/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodolfoz.textaiapp.data.DatabaseProvider
import com.rodolfoz.textaiapp.ui.components.PersonalDataScreen
import com.rodolfoz.textaiapp.ui.components.PromptAndResponseScreen
import kotlinx.coroutines.launch

/**
 * MainActivity that is the entry point of the application.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val destinations = listOf(
            "PersonalDataUI",
            "PromptAndResponseUI"
        )

        val userDataDao = DatabaseProvider.getDatabase(this).userDataDao()
        lifecycleScope.launch {
            val hasUser = userDataDao.getUserById(1) != null

            setContent {
                val navController = rememberNavController()
                val startDestination = if (hasUser) destinations[1] else destinations[0]
                NavHost(navController, startDestination = startDestination) {
                    composable("PersonalDataUI") {
                        PersonalDataScreen(navController)
                    }
                    composable("PromptAndResponseUI") {
                        PromptAndResponseScreen()
                    }
                }
            }
        }
    }
}