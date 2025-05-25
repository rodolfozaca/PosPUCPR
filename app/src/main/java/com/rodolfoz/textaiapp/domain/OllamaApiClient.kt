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

    private val baseUrl: String = "http://192.168.15.10:11435"
    private val client: HttpClient = createHttpClient()
    private const val TAG = "TAA: OllamaApiClient"
    private val model = "llama3:8b"

    /**
     * Generates a response for the given prompt using the specified model, with streaming enabled.
     *
     * @param prompt The input prompt for which a response is to be generated.
     * @return The generated response as a string, or null if an error occurs.
     */
    suspend fun generateStreamed(prompt: String): String {
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
