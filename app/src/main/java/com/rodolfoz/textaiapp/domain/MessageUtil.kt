/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.domain


/**
 * Utility object for handling messages in the chat application.
 *
 * This object provides methods for filtering invalid characters, sending questions to containers,
 * formatting messages with Markdown delimiters, and removing comments from messages.
 */
object MessageUtil {

    private const val TAG = "MessageUtil"
    private const val CHAT_INVALID_RESPONSE_CHARS = "[?25h"

    /**
     * Function that filters invalid characters from an input string.
     *
     * This function searches for the last occurrence of the delimiter "[?25h" in the input string
     * and returns the part of the string that comes after this delimiter. If the delimiter is not found,
     * the function returns the complete input string.
     *
     * @param input The input string from which invalid characters will be filtered.
     * @return The resulting string after filtering invalid characters.
     */
    fun filterInvalidChars(input: String?): String? {
        println("$TAG - filterInvalidChars")

         val filteredResponse = input?.let { it ->
            it.substringAfterLast(CHAT_INVALID_RESPONSE_CHARS, it)
        }

        return filteredResponse
    }
}
