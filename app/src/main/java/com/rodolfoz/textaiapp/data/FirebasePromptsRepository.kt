/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rodolfoz.textaiapp.data.model.PromptResponse
import kotlinx.coroutines.tasks.await

private const val TAG = "TAA:FirebasePromptsRepo"

class FirebasePromptsRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun userPromptsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("prompts")

    /**
     * Save a prompt/response for the given user. Returns the created document id on success.
     */
    suspend fun savePrompt(prompt: PromptResponse): Result<String> {
        try {
            if (prompt.userId.isBlank()) return Result.failure(IllegalArgumentException("userId required"))
            val data = mapOf(
                "userId" to prompt.userId,
                "prompt" to prompt.prompt,
                "response" to prompt.response,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val ref = userPromptsCollection(prompt.userId).add(data).await()
            Log.d(TAG, "Saved prompt docId=${ref.id}")
            return Result.success(ref.id)
        } catch (e: Exception) {
            Log.w(TAG, "savePrompt failed: ${e.message}")
            return Result.failure(e)
        }
    }

    // Additional helpers (not implemented now): getRecentPrompts, listenPrompts, deletePrompt
}

