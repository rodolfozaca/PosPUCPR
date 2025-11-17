/*
 * Copyright (C) 2025 Rodolfo Zacarias. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this code, via any medium, is strictly prohibited.
 *
 * This file is part of a proprietary software package and may not be used or
 * disclosed without explicit permission from the author.
 */
package com.rodolfoz.textaiapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodolfoz.textaiapp.R


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
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 24.sp
        ),
        interactionSource = remember { MutableInteractionSource() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None)
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