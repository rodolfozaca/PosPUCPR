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

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


/**
 * Object responsible for interacting with the Ollama API.
 *
 * This object provides methods to generate responses, manage models, and perform various operations
 * with the Ollama API, such as listing available models, creating new models, and checking the API status.
 */
object OllamaApiClient {

    private val baseUrl: String = "http://192.168.15.166:11435"
    private val client: HttpClient = createHttpClient()
    private const val TAG = "OllamaApiClient"
    private val model = "llama3:8b"

    /**
     * Generates a response for the given prompt using the specified model.
     *
     * @param prompt The input prompt for which a response is to be generated.
     * @return The generated response as a string, or null if an error occurs.
     */
    suspend fun generate(prompt: String): String? {
        Log.d(TAG, "Generating response for prompt: $prompt")
        Log.d(TAG, "generate: ")

        val builder = StringBuilder()
        val requestBody = GenerateRequest(model = model, prompt = prompt)

        try {
            val response: HttpResponse = client.post("$baseUrl/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line(8192)
                if (!line.isNullOrBlank()) {
                    val json = Json.parseToJsonElement(line).jsonObject
                    val text = json["response"]?.jsonPrimitive?.content.orEmpty()
                    builder.append(text)
                    val done = json["done"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (done) break
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error streaming response: ${e.message}")
        }

        return builder.toString()
    }
}

/**
 * Creates and configures an instance of [HttpClient] for interacting with the Ollama API.
 * This client is set up with JSON content negotiation and logging capabilities.
 * @return A configured [HttpClient] instance.
 */
private fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }
}

/**
 * Data class representing a request to generate a response from the Ollama API.
 * @param model The model to be used for generating the response.
 * @param prompt The input prompt for which a response is to be generated.
 * @param stream Indicates whether the response should be streamed.
 */
@Serializable
data class GenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)
