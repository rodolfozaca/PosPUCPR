/*
 * Rodolfo Zacarias - 2025.
 *
 * All rights reserved. This software is the exclusive property of Rodolfo Zacarias.
 * Redistribution, modification, or use of this code is permitted only under the terms
 * of the GNU General Public License (GPL) as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.rodolfoz.textaiapp.domain

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


/**
 * Object responsible for interacting with the Ollama API.
 *
 * This object provides methods to generate responses, manage models, and perform various operations
 * with the Ollama API, such as listing available models, creating new models, and checking the API status.
 */
object OllamaApiClient {

    private val baseUrl: String = "http://192.168.15.10:11435"
    private val client: HttpClient = createHttpClient()
    private const val TAG = "TAA: OllamaApiClient"
    private val model = "llama3:8b"

    /**
     * Generates a response for the given prompt using the specified model.
     *
     * @param prompt The input prompt for which a response is to be generated.
     * @return The generated response as a string, or null if an error occurs.
     */
    suspend fun generate(prompt: String): String? {
        Log.d(TAG, "Generating response for prompt: $prompt")

        var response: HttpResponse? = null
        try {
            Log.d(TAG, "Sending request to: $baseUrl/api/generate")
            response = client.post("$baseUrl/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(GenerateRequest(model, prompt))
            }
            Log.d(TAG, "Response status: ${response.status}")
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
        }
        return response?.bodyAsText()
    }

    /**
     * Generates a response for the given prompt using the specified model, with streaming enabled.
     *
     * @param prompt The input prompt for which a response is to be generated.
     * @return The generated response as a string, or null if an error occurs.
     */
    suspend fun generateStreamed(prompt: String): String {
        val builder = StringBuilder()
        val requestBody = GenerateRequest(model = "llama3:8b", prompt = prompt)

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

    /**
     * Sends a chat message to the specified model and returns the response.
     *
     * @param messages The list of chat messages to be sent to the model.
     * @return The response from the model as a string.
     */
    suspend fun chat(messages: List<ChatMessage>): String {
        val response: HttpResponse = client.post("$baseUrl/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(ChatRequest(model, messages))
        }
        return response.bodyAsText()
    }

    /**
     * Lists the names of available models.
     *
     * @return A list of model names as strings.
     */
    suspend fun listModelNames(): List<String> {
        Log.d(TAG, "listModelNames")
        val response: HttpResponse = client.get("$baseUrl/api/tags")
        val json = response.bodyAsText()
        val parsed = Json.parseToJsonElement(json)
        return parsed.jsonObject["models"]!!
            .jsonArray
            .mapNotNull { it.jsonObject["name"]?.jsonPrimitive?.content }
    }

    /**
     * Lists the available models.
     *
     * @return A list of model names as strings.
     */
    suspend fun pullModel(): String {
        val response: HttpResponse = client.post("$baseUrl/api/pull") {
            contentType(ContentType.Application.Json)
            setBody(ModelNameRequest(model))
        }
        return response.bodyAsText()
    }

    /**
     * Shows the details of the specified model.
     *
     * @return A string containing the details of the model.
     */
    suspend fun showModel(): String {
        val response: HttpResponse = client.post("$baseUrl/api/show") {
            contentType(ContentType.Application.Json)
            setBody(ModelNameRequest(model))
        }
        return response.bodyAsText()
    }

    /**
     * Deletes the specified model.
     *
     * @return A string containing the result of the deletion operation.
     */
    suspend fun deleteModel(): String {
        val response: HttpResponse = client.post("$baseUrl/api/delete") {
            contentType(ContentType.Application.Json)
            setBody(ModelNameRequest(model))
        }
        return response.bodyAsText()
    }

    /**
     * Creates a new model with the specified name and model file.
     *
     * @param name The name of the new model.
     * @param modelfile The file path of the model to be created.
     * @return A string containing the result of the creation operation.
     */
    suspend fun createModel(name: String, modelfile: String): String {
        val response: HttpResponse = client.post("$baseUrl/api/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateModelRequest(name, modelfile))
        }
        return response.bodyAsText()
    }

    /**
     * Generates embeddings for the given prompt using the specified model.
     *
     * @param prompt The input prompt for which embeddings are to be generated.
     * @return The generated embeddings as a string.
     */
    suspend fun embeddings(prompt: String): String {
        val response: HttpResponse = client.post("$baseUrl/api/embeddings") {
            contentType(ContentType.Application.Json)
            setBody(GenerateRequest(model, prompt))
        }
        return response.bodyAsText()
    }

    /**
     * Checks the status of the Ollama API.
     *
     * @return True if the API is running, false otherwise.
     */
    suspend fun status(): Boolean {
        Log.d(TAG, "Checking status...")
        return try {
            val response: HttpResponse = client.get("$baseUrl/")
            Log.d(TAG, "Status response: ${response.bodyAsText()}")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }
}

fun createHttpClient(): HttpClient {
    val TAG = "TAA: OllamaApiClient"

    val httpClient = HttpClient(OkHttp) {
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

    Log.d(TAG, "createHttpClient: $httpClient")
    return httpClient
}

@Serializable
data class GenerateRequest(val model: String, val prompt: String, val stream: Boolean = false)

@Serializable
data class ChatRequest(val model: String, val messages: List<ChatMessage>)

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class ModelNameRequest(val name: String)

@Serializable
data class CreateModelRequest(val name: String, val modelfile: String)
