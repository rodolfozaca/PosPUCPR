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

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodolfoz.textaiapp.data.DatabaseProvider
import com.rodolfoz.textaiapp.ui.components.LoginScreen
import com.rodolfoz.textaiapp.ui.components.PasswordSetupScreen
import com.rodolfoz.textaiapp.ui.components.PersonalDataScreen
import com.rodolfoz.textaiapp.ui.components.PromptAndResponseScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity that is the entry point of the application.
 *
 * Start destination is fixed to `LoginUI` so the login screen is always shown on app launch.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Checar credenciais salvas nas preferencias e validar com o DB
        lifecycleScope.launch {
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val savedLogin = prefs.getString("saved_login", null)
            val savedHash = prefs.getString("saved_password_hash", null)

            var startDestination = "LoginUI"

            if (!savedLogin.isNullOrBlank() && !savedHash.isNullOrBlank()) {
                // validar com o DB
                val db = DatabaseProvider.getDatabase(this@MainActivity)
                val user =
                    withContext(Dispatchers.IO) { db.userDataDao().getUserByLogin(savedLogin) }
                if (user != null && user.password == savedHash) {
                    startDestination = "PromptAndResponseUI"
                }
            }

            setContent {
                val navController = rememberNavController()
                NavHost(navController, startDestination = startDestination) {
                    composable("PersonalDataUI") {
                        PersonalDataScreen(navController)
                    }
                    composable("LoginUI") {
                        LoginScreen(navController)
                    }
                    composable("PasswordSetupUI") {
                        PasswordSetupScreen(navController)
                    }
                    composable("PromptAndResponseUI") {
                        PromptAndResponseScreen()
                    }
                }
            }
        }
    }
}