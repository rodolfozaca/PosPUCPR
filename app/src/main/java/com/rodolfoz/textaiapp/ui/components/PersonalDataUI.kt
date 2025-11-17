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
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rodolfoz.textaiapp.R
import com.rodolfoz.textaiapp.data.model.UserDataModel
import com.rodolfoz.textaiapp.ui.viewmodels.PersonalDataViewModel

private const val TAG = "TAA: PersonalDataUI"

/**
 * Composable function to display the personal data UI.
 *
 * @param navController The NavHostController for navigation.
 * @param viewModel The PersonalDataViewModel instance.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonalDataUI(navController: NavHostController, viewModel: PersonalDataViewModel?) {
    Log.d(TAG, "PersonalDataUI")

    val context = LocalContext.current
    val userData = remember {
        mutableListOf(
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf("")
        )
    }
    val fieldLabels = mutableListOf(
        stringResource(R.string.label_name),
        stringResource(R.string.label_age),
        stringResource(R.string.label_gender),
        stringResource(R.string.label_phone),
        stringResource(R.string.label_email),
        stringResource(R.string.label_city),
        stringResource(R.string.label_state),
        stringResource(R.string.label_country)
    )

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    Log.d(TAG, "Save button clicked")

                    val user = UserDataModel(
                        id = 1,
                        name = userData[0].value,
                        age = userData[1].value.toIntOrNull() ?: 0,
                        gender = userData[2].value,
                        phone = userData[3].value,
                        email = userData[4].value,
                        city = userData[5].value,
                        state = userData[6].value,
                        country = userData[7].value,
                    )

                    if (user.name.isNotBlank() && user.age > 0 && user.gender.isNotBlank() &&
                        user.phone.isNotBlank() && user.email.isNotBlank() &&
                        user.city.isNotBlank() && user.state.isNotBlank() && user.country.isNotBlank()
                    ) {
                        // Validar formato de email
                        if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                            Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel?.saveUserData(
                            user,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_text_data_save_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Após salvar os dados pessoais, ir para a tela de criação de senha
                                navController.navigate("PasswordSetupUI")
                            },
                            onError = { errorMessage: String ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_text_field_info_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = stringResource(R.string.button_text_save))
            }
        },
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .imePadding()
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(R.string.profile_image_text),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .clickable {
                        Log.d(TAG, "Profile image clicked")
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_text_profile_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
            )
            LazyColumn(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(16.dp)
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userData.size) { index ->
                    OutlinedTextField(
                        value = userData[index].value,
                        onValueChange = { newValue -> userData[index].value = newValue },
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    userData[index].value = ""
                                }
                            },
                        label = { Text(fieldLabels[index]) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if (index == userData.size - 1) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "UiPreview")
@Composable
fun PersonalDataUIPreview() {
    val mockNavController = NavHostController(LocalContext.current)
    PersonalDataUI(mockNavController, null)
}
