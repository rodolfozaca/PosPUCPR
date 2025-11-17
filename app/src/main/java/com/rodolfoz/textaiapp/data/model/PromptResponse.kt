/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.data.model

import com.google.firebase.Timestamp

/**
 * Represents a user prompt and the model response to be stored in Firestore.
 */
data class PromptResponse(
    var id: String = "",
    var userId: String = "",
    var prompt: String = "",
    var response: String = "",
    // Use server timestamp when writing; read as Timestamp and convert as needed.
    var createdAt: Timestamp? = null
)

