/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.ui.components.componetest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rodolfoz.textaiapp.data.AuthManager

@Composable
fun DrawerMenu(navController: NavHostController, userName: String?) {
    val context = LocalContext.current
    val userId = AuthManager.currentUid() ?: ""
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(16.dp), verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // header with user display
            // avatar + name
            val displayName = when {
                !userName.isNullOrBlank() -> userName
                userId.isNotBlank() -> userId
                else -> "Não autenticado"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // simple initials avatar inside a circle
                val initials = displayName.takeIf { it.isNotBlank() }?.split(" ")
                    ?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")
                    ?.uppercase() ?: "U"
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray), contentAlignment = Alignment.Center
                ) {
                    Text(text = initials, color = Color.White, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // navigation buttons - icon + text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Meus Prompts",
                    modifier = Modifier.clickable { navController.navigate("PromptsListUI") })
            }
        }

        // logout as icon + text (bottom)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    AuthManager.signOut()
                    try {
                        val prefs = context.getSharedPreferences(
                            "app_prefs",
                            android.content.Context.MODE_PRIVATE
                        )
                        prefs.edit().remove("saved_login").remove("saved_password_hash")
                            .remove("remember_login").apply()
                    } catch (_: Exception) {
                    }
                    try {
                        navController.navigate("LoginUI") {
                            popUpTo("PromptAndResponseUI") {
                                inclusive = true
                            }
                        }
                    } catch (_: Exception) {
                        navController.navigate("LoginUI")
                    }
                }
                .padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sair",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
