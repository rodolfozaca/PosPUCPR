/*
 * RZDev Softwares 2025
 *
 * Todos os direitos reservados. Este software é propriedade exclusiva de
 * RZDev Softwares. É proibida a reprodução, distribuição ou modificação
 * deste código sem autorização expressa por escrito.
 *
 * O uso deste software é permitido apenas sob as condições especificadas em
 * um contrato de licença fornecido pelo autor. O autor não será responsável
 * por quaisquer danos diretos ou indiretos resultantes do uso deste software.
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
    fun filterInvalidChars(input: String): String {
        println("$TAG - filterInvalidChars")

        return input.substringAfterLast(CHAT_INVALID_RESPONSE_CHARS, input)
    }
}
