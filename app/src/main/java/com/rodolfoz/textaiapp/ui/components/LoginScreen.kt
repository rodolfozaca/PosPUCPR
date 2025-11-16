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

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rodolfoz.textaiapp.data.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "TAA:LoginScreen"

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val loginState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val rememberLogin = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Center
    ) {
        Text(text = "Login")
        OutlinedTextField(
            value = loginState.value,
            onValueChange = { loginState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Usuário") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(text = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Button(
            onClick = {
                if (loginState.value.isBlank() || passwordState.value.isBlank()) {
                    Toast.makeText(context, "Preencha login e senha", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val db = DatabaseProvider.getDatabase(context)
                val repository = com.rodolfoz.textaiapp.data.UserRepository(db.userDataDao())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val authenticated = repository.authenticate(
                            loginState.value.trim(),
                            passwordState.value.trim()
                        )
                        if (authenticated) {
                            // se o usuário pediu para lembrar, salvar nas prefs o login e o hash
                            if (rememberLogin.value) {
                                val user = repository.getUserByLogin(loginState.value.trim())
                                if (user != null) {
                                    val prefs = context.getSharedPreferences(
                                        "app_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    prefs.edit()
                                        .putString("saved_login", user.login)
                                        .putString("saved_password_hash", user.password)
                                        .apply()
                                }
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Login realizado", Toast.LENGTH_SHORT)
                                    .show()
                                navController.navigate("PromptAndResponseUI")
                            }
                        } else {
                            // check if user exists at all to give a more precise message
                            val existing = repository.getUserByLogin(loginState.value.trim())
                            CoroutineScope(Dispatchers.Main).launch {
                                if (existing != null) {
                                    Toast.makeText(context, "Senha inválida", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Usuário não encontrado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro ao buscar usuário: ${e.message}")
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Erro no login", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Entrar")
        }

        // Checkbox lembrar login
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Checkbox(checked = rememberLogin.value, onCheckedChange = { rememberLogin.value = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Lembrar login")
        }

        Text(
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                    // Ao tentar navegar para cadastro, verificar se já existe usuário com id = 1
                    val db = DatabaseProvider.getDatabase(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        val existing = try {
                            db.userDataDao().getUserById(1)
                        } catch (_: Exception) {
                            null
                        }
                        if (existing != null) {
                            // se já existe, mostrar toast e permanecer na tela
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Já existe um usuário cadastrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // caso não exista, navegar para a tela de cadastro
                            CoroutineScope(Dispatchers.Main).launch {
                                navController.navigate("PersonalDataUI")
                            }
                        }
                    }
                },
            text = "Não tem conta? Cadastre-se"
        )
    }
}
