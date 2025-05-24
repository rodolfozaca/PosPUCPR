package com.rodolfoz.textaiapp.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rodolfoz.textaiapp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonalDataUI(navController: NavHostController) {

    val context = LocalContext.current
    val userData = remember {
        mutableListOf(
            mutableStateOf("Nome"),
            mutableStateOf("Idade"),
            mutableStateOf("Gênero"),
            mutableStateOf("Telefone"),
            mutableStateOf("Email"),
            mutableStateOf("Cidade"),
            mutableStateOf("Estado"),
            mutableStateOf("País")
        )
    }

    val focusRequester = List(userData.size) { FocusRequester() }
    val keyboController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.navigate("PromptAndResponseUI")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Salvar")
            }
        },
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
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
                text = "Foto",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .clickable {
                        Toast.makeText(context, "Insira a foto de perfil", Toast.LENGTH_SHORT)
                            .show()
                    }
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
                            .focusRequester(focusRequester[index])
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    userData[index].value = ""
                                }
                            },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if (index == userData.size - 1) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < userData.size - 1) {
                                    focusRequester[index - 1].requestFocus()
                                }
                            },
                            onDone = {
                                keyboController?.hide()
                                navController.navigate("PromptAndResponseUI")
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
    PersonalDataUI(mockNavController)
}