package com.rodolfoz.textaiapp.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rodolfoz.textaiapp.data.AuthManager
import com.rodolfoz.textaiapp.data.FirebasePromptsRepository
import com.rodolfoz.textaiapp.data.model.PromptResponse
import kotlinx.coroutines.launch

private const val TAG = "TAA:PromptsListUI"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsListUI(navController: NavHostController) {
    val context = LocalContext.current
    val items = remember { mutableStateListOf<PromptResponse>() }
    val isLoading = remember { mutableStateOf(true) }

    suspend fun load() {
        isLoading.value = true
        val uid = AuthManager.currentUid() ?: ""
        if (uid.isBlank()) {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            return
        }
        try {
            val repo = FirebasePromptsRepository()
            val res = repo.getPrompts(uid)
            if (res.isSuccess) {
                items.clear()
                items.addAll(res.getOrNull() ?: emptyList())
            } else {
                Toast.makeText(
                    context,
                    "Falha ao carregar: ${res.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error loading prompts: ${e.message}")
            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading.value = false
        }
    }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) { coroutineScope.launch { load() } }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(8.dp)
    ) {
        Text(text = "Histórico de prompts", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
        val swipeState = rememberSwipeRefreshState(isRefreshing = isLoading.value)
        SwipeRefresh(state = swipeState, onRefresh = { coroutineScope.launch { load() } }) {
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(items) { pr ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Prompt:")
                        Text(text = pr.prompt, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Resposta:")
                        Text(text = pr.response, style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
