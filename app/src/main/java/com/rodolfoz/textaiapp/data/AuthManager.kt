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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

/**
 * Simple wrapper around FirebaseAuth providing suspend-friendly methods.
 * This keeps Firebase-specific code isolated and easy to test.
 */
object AuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private const val TAG = "TAA:AuthManager"

    /**
     * Returns the UID of the currently signed-in user, or null if no user is signed in.
     * @return String? The UID of the current user or null.
     */
    fun currentUid(): String? = auth.currentUser?.uid

    /**
     * Returns true if a user is currently signed in, false otherwise.
     * @return Boolean indicating sign-in status.
     */
    fun isSignedIn(): Boolean = auth.currentUser != null

    /**
     * Signs in a user with the provided email and password.
     * @param email The user's email address.
     * @param password The user's password.
     * @return Result<String> containing the user's UID on success, or an exception on failure
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""
            Log.d(TAG, "signIn successful for email=$email uid=$uid")
            Result.success(uid)
        } catch (e: Exception) {
            Log.w(TAG, "signIn failed for email=$email: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Creates a new user with the provided email and password.
     * @param email The user's email address.
     * @param password The user's password.
     * @return Result<String> containing the new user's UID on success, or an exception on
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""
            Log.d(TAG, "createUser successful for email=$email uid=$uid")
            Result.success(uid)
        } catch (e: Exception) {
            Log.w(TAG, "createUser failed for email=$email: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Sends a password reset email to the specified email address.
     * @param email The user's email address.
     * @return Result<Unit> indicating success or failure.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Signs out the currently signed-in user.
     */
    fun signOut() {
        Log.d(TAG, "signOut called")
        auth.signOut()
    }

    /**
     * Map Firebase auth exceptions to user-friendly messages.
     */
    fun mapAuthError(e: Throwable?): String {
        if (e == null) return "Erro desconhecido"
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas"
            is FirebaseAuthInvalidUserException -> "Usuário não encontrado ou desabilitado"
            is FirebaseAuthException -> {
                // Use the error code if available
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email já está em uso"
                    "ERROR_WEAK_PASSWORD" -> "Senha fraca"
                    "ERROR_WRONG_PASSWORD" -> "Senha incorreta"
                    else -> e.message ?: "Erro de autenticação"
                }
            }

            else -> e.message ?: "Erro de autenticação"
        }
    }
}
