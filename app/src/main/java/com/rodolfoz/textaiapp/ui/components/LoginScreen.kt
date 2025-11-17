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
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
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
import com.rodolfoz.textaiapp.data.AuthManager.signInWithEmailAndPassword
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
    val isLoading = remember { mutableStateOf(false) }

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
                        // Primeiro, tentar autenticação via Firebase se o usuário tem email cadastrado
                        val localUser = repository.getUserByLogin(loginState.value.trim())
                        if (localUser != null && localUser.email.isNotBlank()) {
                            // validar email
                            if (!Patterns.EMAIL_ADDRESS.matcher(localUser.email).matches()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                val firebaseResult = signInWithEmailAndPassword(
                                    localUser.email,
                                    passwordState.value.trim()
                                )
                                if (firebaseResult.isSuccess) {
                                    // sucesso firebase
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading.value = false
                                    }
                                    // Autenticação via Firebase OK
                                    val uid = firebaseResult.getOrNull() ?: ""
                                    if (uid.isNotBlank()) {
                                        try {
                                            repository.setFirebaseUidForUserByLogin(
                                                loginState.value.trim(),
                                                uid
                                            )
                                        } catch (_: Exception) {
                                            // non-fatal
                                        }
                                    }
                                    // persist or clear saved credentials depending on checkbox
                                    val prefs = context.getSharedPreferences(
                                        "app_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    if (rememberLogin.value) {
                                        prefs.edit()
                                            .putString("saved_login", localUser.login)
                                            .putString("saved_password_hash", localUser.password)
                                            .putBoolean("remember_login", true)
                                            .apply()
                                    } else {
                                        // ensure we don't keep stale saved credentials
                                        prefs.edit().remove("saved_login")
                                            .remove("saved_password_hash").remove("remember_login")
                                            .apply()
                                    }
                                    Log.d(
                                        TAG,
                                        "Firebase login succeeded for user=${localUser.login} uid=$uid"
                                    )
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Toast.makeText(
                                            context,
                                            "Login realizado (Firebase)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("PromptAndResponseUI")
                                    }
                                    return@launch
                                }
                                // se falhou firebase, mostrar mensagem amigável (mas continuar para fallback)
                                CoroutineScope(Dispatchers.Main).launch {
                                    val msg = com.rodolfoz.textaiapp.data.AuthManager.mapAuthError(
                                        firebaseResult.exceptionOrNull()
                                    )
                                    if (msg.isNotBlank()) Toast.makeText(
                                        context,
                                        "Firebase: $msg",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading.value = false
                                }
                            }
                            // se erro no Firebase, iremos tentar fallback para auth local abaixo
                        }
                        CoroutineScope(Dispatchers.Main).launch { isLoading.value = true }
                        val authenticated = repository.authenticate(
                            loginState.value.trim(),
                            passwordState.value.trim()
                        )
                        if (authenticated) {
                            CoroutineScope(Dispatchers.Main).launch { isLoading.value = false }
                            // se o usuário pediu para lembrar, salvar nas prefs o login e o hash
                            val prefs = context.getSharedPreferences(
                                "app_prefs",
                                Context.MODE_PRIVATE
                            )
                            if (rememberLogin.value) {
                                val user = repository.getUserByLogin(loginState.value.trim())
                                if (user != null) {
                                    prefs.edit()
                                        .putString("saved_login", user.login)
                                        .putString("saved_password_hash", user.password)
                                        .putBoolean("remember_login", true)
                                        .apply()
                                }
                            } else {
                                // clear any existing saved credentials
                                prefs.edit().remove("saved_login").remove("saved_password_hash")
                                    .remove("remember_login").apply()
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Login realizado", Toast.LENGTH_SHORT)
                                    .show()
                                navController.navigate("PromptAndResponseUI")
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch { isLoading.value = false }
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
        if (isLoading.value) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }
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
