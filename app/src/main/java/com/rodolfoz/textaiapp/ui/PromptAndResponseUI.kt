package com.rodolfoz.textaiapp.ui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PromptAndResponseUI(navController: NavHostController) {

    val prompt = remember { mutableStateOf("") }
    val promptResponse = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
        },
        bottomBar = {
        },
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(1.dp)),
            ) {
                ChatResponseField(promptResponse)
            }

            HorizontalDivider(modifier = Modifier.padding(2.dp))

            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(1.dp)),
            ) {
                UserInputField(prompt, onSend = {
                    promptResponse.value = it
                    prompt.value = ""
                })
            }
        }
    }

}

@Composable
fun ChatResponseField(
    promptResponse: MutableState<String>
) {
    BasicTextField(
        value = promptResponse.value,
        onValueChange = { promptResponse.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(Color.LightGray)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(1.dp)),
        textStyle = TextStyle(Color.Black),
        interactionSource = remember { MutableInteractionSource() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None)
    )
}

@Composable
fun UserInputField(
    prompt: MutableState<String>,
    onSend: (String) -> Unit
) {
    BasicTextField(
        value = prompt.value,
        onValueChange = { prompt.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray),
        textStyle = TextStyle(Color.Black),
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

@Preview
@Composable
fun PreviewPromptAndResponseUI() {
    val mockNavController = NavHostController(LocalContext.current)
    PromptAndResponseUI(mockNavController)
}