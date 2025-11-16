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

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

private const val TAG = "TAA:PasswordSetup"

@Composable
fun PasswordSetupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val loginState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Center
    ) {
        Text(text = "Defina um login (nome de usuário)")
        OutlinedTextField(
            value = loginState.value,
            onValueChange = { loginState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Login") },
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
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = confirmState.value,
            onValueChange = { confirmState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(text = "Confirmar senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Button(
            onClick = {
                if (loginState.value.isBlank() || passwordState.value.isBlank() || confirmState.value.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (passwordState.value != confirmState.value) {
                    Toast.makeText(context, "Senhas não coincidem", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Save login and hashed password to DB for user id=1 via repository
                val db = DatabaseProvider.getDatabase(context)
                val repository = com.rodolfoz.textaiapp.data.UserRepository(db.userDataDao())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Attempt to set credentials for the user id 1
                        repository.setCredentialsForUser(
                            1,
                            loginState.value.trim(),
                            passwordState.value.trim()
                        )
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Cadastro concluído", Toast.LENGTH_SHORT).show()
                            navController.navigate("PromptAndResponseUI")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro salvando credenciais: ${e.message}")
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Erro ao salvar credenciais",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Finalizar cadastro")
        }
    }
}
