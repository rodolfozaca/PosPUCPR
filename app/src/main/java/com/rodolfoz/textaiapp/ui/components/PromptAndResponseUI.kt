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

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rodolfoz.textaiapp.R
import com.rodolfoz.textaiapp.data.AuthManager
import com.rodolfoz.textaiapp.data.FirebasePromptsRepository
import com.rodolfoz.textaiapp.data.model.PromptResponse
import com.rodolfoz.textaiapp.domain.MessageUtil
import com.rodolfoz.textaiapp.domain.OllamaApiClient
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "TAA: PromptAndResponseUI"

/**
 * Composable function to display the prompt and response UI.
 *
 * @param viewModel The PersonalDataViewModel instance.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PromptAndResponseUI(viewModel: PersonalDataViewModel?, navController: NavHostController) {
    Log.d(TAG, "PromptAndResponseUI")

    val context = LocalContext.current
    val prompt = remember { mutableStateOf("") }
    val promptResponse = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val userNameState = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // repository for saving prompts in Firestore
    val promptsRepo = FirebasePromptsRepository()

    LaunchedEffect(Unit) {
        val messageOne = context.getString(R.string.welcome_message_part_one)
        val messageTwo = context.getString(R.string.welcome_message_part_two)

        viewModel?.getUserName { name ->
            promptResponse.value = "$messageOne, $name!\n$messageTwo"
            userNameState.value = name // Update the username state
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            com.rodolfoz.textaiapp.ui.components.componetest.DrawerMenu(
                navController,
                userNameState.value
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .padding(WindowInsets.statusBars.asPaddingValues())
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TextAIApp",
                        style = TextStyle(fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Box(modifier = Modifier.weight(1f))
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(2.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(1.dp)
                            )
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            ChatResponseField(promptResponse)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(2.dp))

                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(1.dp)
                            ),
                    ) {
                        UserInputField(prompt, onSend = { userPromt ->
                            isLoading.value = true
                            viewModel?.viewModelScope?.launch {
                                try {
                                    val rolePrompt = context.getString(R.string.role_model_prompt)
                                    val tunedPrompt = "$rolePrompt\n\n$userPromt"
                                    val response = OllamaApiClient.generate(tunedPrompt)
                                    val cleanedResponse = MessageUtil.filterInvalidChars(response)
                                    if (cleanedResponse != null) {
                                        promptResponse.value = cleanedResponse
                                        // save automatically to Firestore
                                        val userId = AuthManager.currentUid() ?: ""
                                        if (userId.isNotBlank()) {
                                            // run save on IO
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val pr = PromptResponse(
                                                    userId = userId,
                                                    prompt = userPromt,
                                                    response = cleanedResponse
                                                )
                                                val result = promptsRepo.savePrompt(pr)
                                                withContext(Dispatchers.Main) {
                                                    if (result.isSuccess) {
                                                        Toast.makeText(
                                                            context,
                                                            "Salvo no Firebase",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Falha ao salvar: ${result.exceptionOrNull()?.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            }
                                        } else {
                                            // user not authenticated, inform optionally
                                            CoroutineScope(Dispatchers.Main).launch {
                                                Toast.makeText(
                                                    context,
                                                    "Não autenticado no Firebase; não foi possível salvar",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Log the exception for diagnostics and show a friendly message
                                    Log.e(TAG, "Error calling Ollama API", e)
                                    promptResponse.value =
                                        context.getString(R.string.api_error_message)
                                } finally {
                                    isLoading.value = false
                                }
                            }
                            prompt.value = ""
                        })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "PromptAndResponseUIPreview")
@Composable
fun PreviewPromptAndResponseUI() {
    PromptAndResponseUI(null, navController = rememberNavController())
}
