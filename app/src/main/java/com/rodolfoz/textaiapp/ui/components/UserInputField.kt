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

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodolfoz.textaiapp.R


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
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 24.sp
        ),
        interactionSource = remember { MutableInteractionSource() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None),
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
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
                                        tint = MaterialTheme.colorScheme.primary
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
                                        tint = MaterialTheme.colorScheme.primary
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

@Preview(showBackground = true, name = "UserInputField Preview")
@Composable
fun PreviewUserInputField() {
    val loremIpsum =
        stringResource(R.string.lorem_ipsum_dolor_sit_amet_consectetur_adipiscing_elit)
    val prompt = remember { mutableStateOf(loremIpsum) }
    UserInputField(prompt, onSend = {})
}
