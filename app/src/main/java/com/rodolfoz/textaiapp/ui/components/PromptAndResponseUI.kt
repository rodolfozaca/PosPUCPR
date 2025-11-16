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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.rodolfoz.textaiapp.R
import com.rodolfoz.textaiapp.domain.MessageUtil
import com.rodolfoz.textaiapp.domain.OllamaApiClient
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModel
import kotlinx.coroutines.launch

private const val TAG = "TAA: PromptAndResponseUI"

/**
 * Composable function to display the prompt and response UI.
 *
 * @param viewModel The PersonalDataViewModel instance.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PromptAndResponseUI(viewModel: PersonalDataViewModel?) {
    Log.d(TAG, "PromptAndResponseUI")

    val context = LocalContext.current
    val prompt = remember { mutableStateOf("") }
    val promptResponse = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val messageOne = context.getString(R.string.welcome_message_part_one)
        val messageTwo = context.getString(R.string.welcome_message_part_two)

        viewModel?.getUserName { name ->
            promptResponse.value = "$messageOne, $name!\n$messageTwo"
        }
    }

    Scaffold(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .imePadding()
    ) {
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
                    .background(color = Color.LightGray)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(1.dp))
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        color = Color.Black,
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
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(1.dp)),
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
                                promptResponse.value =
                                    cleanedResponse
                            }
                        } catch (e: Exception) {
                            promptResponse.value = context.getString(R.string.api_error_message)
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

/**
 * Composable function to display the chat response field.
 *
 * @param promptResponse The mutable state holding the chat response text.
 */
@Composable
fun ChatResponseField(
    promptResponse: MutableState<String>
) {
    BasicTextField(
        value = promptResponse.value,
        onValueChange = { promptResponse.value = it },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(8.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 20.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 24.sp
        ),
        interactionSource = remember { MutableInteractionSource() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None)
    )
}

/**
 * Composable function to display the user input field.
 *
 * @param prompt The mutable state holding the user input text.
 * @param onSend Callback function to handle the send action.
 */
@Composable
fun UserInputField(
    prompt: MutableState<String>,
    onSend: (String) -> Unit
) {
    BasicTextField(
        value = prompt.value,
        onValueChange = { prompt.value = it },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(8.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 20.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 24.sp
        ),
        interactionSource = remember { MutableInteractionSource() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None),
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box(
                Modifier
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        innerTextField()
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(start = 8.dp)
                        ) {
                            Row {
                                IconButton(onClick = {
                                    if (prompt.value.isNotEmpty()) {
                                        prompt.value = ""
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = Color.Black
                                    )
                                }
                                IconButton(onClick = {
                                    if (prompt.value.isNotEmpty()) {
                                        onSend(prompt.value)
                                    }
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, name = "ChatResponseField Preview")
@Composable
fun PreviewChatResponseField() {
    val loremIpsum =
        stringResource(R.string.lorem_ipsum_dolor_sit_amet_consectetur_adipiscing_elit)

    val promptResponse = remember { mutableStateOf(loremIpsum) }
    ChatResponseField(promptResponse)
}

@Preview(showBackground = true, name = "UserInputField Preview")
@Composable
fun PreviewUserInputField() {
    val loremIpsum =
        stringResource(R.string.lorem_ipsum_dolor_sit_amet_consectetur_adipiscing_elit)
    val prompt = remember { mutableStateOf(loremIpsum) }
    UserInputField(prompt, onSend = {})
}

@Preview(showBackground = true, name = "PromptAndResponseUIPreview")
@Composable
fun PreviewPromptAndResponseUI() {
    PromptAndResponseUI(null)
}
