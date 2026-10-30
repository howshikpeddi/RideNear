package com.example.data.gemini

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatTurn(
    val role: String, // "user" or "model"
    val text: String
)

class GeminiApiClient {

    companion object {
        private const val TAG = "GeminiApiClient"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

        const val MODEL_TRANSCRIBE = "gemini-3.5-transcribe"
        const val MODEL_PRO = "gemini-3.1-pro-preview"
        const val MODEL_FLASH = "gemini-3.5-flash"
        const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite"
        const val MODEL_LIVE = "gemini-3.8-live"

        const val SYSTEM_INSTRUCTION_DEFAULT =
            "You are RideNear AI, an expert peer-to-peer vehicle sharing concierge and mobility advisor for bicycles, e-bikes, scooters, and cars. Assist users with rental rules, vehicle safety inspections, pricing recommendations, deposit guidelines, dispute prevention, and local navigation. Maintain a friendly, concise, and helpful tone."
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Transcribes an audio file (e.g. m4a/aac/wav) using model gemini-3.5-transcribe.
     */
    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        mimeType: String = "audio/mp4"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Please provide a clean, accurate transcription of this audio recording.")
                            })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", base64Audio)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val requestUrl = "$BASE_URL$MODEL_TRANSCRIBE:generateContent?key=$apiKey"
            val body = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(requestUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Transcription failed: ${response.code} $responseString")
                return@withContext Result.failure(Exception("Transcription error (${response.code}): $responseString"))
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            val firstCand = candidates?.optJSONObject(0)
            val content = firstCand?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val transcript = parts?.optJSONObject(0)?.optString("text")?.trim() ?: "No speech detected"

            Result.success(transcript)
        } catch (e: Exception) {
            Log.e(TAG, "Error in transcribeAudio", e)
            Result.failure(e)
        }
    }

    /**
     * Multi-turn chat using specified Gemini model (e.g. gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite).
     * Maintains conversation history and system instructions.
     */
    suspend fun sendChatMessage(
        conversation: List<ChatTurn>,
        modelName: String = MODEL_FLASH,
        systemInstruction: String = SYSTEM_INSTRUCTION_DEFAULT
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()

            val rootJson = JSONObject().apply {
                // System Instruction
                put("systemInstruction", JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", parts)
                })

                // Contents history
                val contentsArray = JSONArray()
                conversation.forEach { turn ->
                    val contentObj = JSONObject().apply {
                        put("role", if (turn.role == "model") "model" else "user")
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", turn.text) })
                        }
                        put("parts", parts)
                    }
                    contentsArray.put(contentObj)
                }
                put("contents", contentsArray)
            }

            val requestUrl = "$BASE_URL$modelName:generateContent?key=$apiKey"
            val body = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(requestUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Chat failed: ${response.code} $responseString")
                return@withContext Result.failure(Exception("Chat error (${response.code}): $responseString"))
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            val firstCand = candidates?.optJSONObject(0)
            val content = firstCand?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val reply = parts?.optJSONObject(0)?.optString("text")?.trim() ?: "No response generated."

            Result.success(reply)
        } catch (e: Exception) {
            Log.e(TAG, "Error in sendChatMessage", e)
            Result.failure(e)
        }
    }

    /**
     * Real-time conversational turn with Live API model gemini-3.8-live.
     */
    suspend fun sendLiveMessage(
        conversation: List<ChatTurn>,
        audioInput: ByteArray? = null,
        audioMimeType: String = "audio/mp4",
        systemInstruction: String = "You are RideNear Live Voice Assistant. Give concise, conversational, real-time responses to help riders and vehicle owners on the go."
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()

            val rootJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", parts)
                })

                val contentsArray = JSONArray()
                // Append previous history
                conversation.dropLast(if (audioInput != null) 0 else 0).forEach { turn ->
                    val contentObj = JSONObject().apply {
                        put("role", if (turn.role == "model") "model" else "user")
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", turn.text) })
                        }
                        put("parts", parts)
                    }
                    contentsArray.put(contentObj)
                }

                // If user provided raw audio for Live API
                if (audioInput != null && audioInput.isNotEmpty()) {
                    val base64 = Base64.encodeToString(audioInput, Base64.NO_WRAP)
                    val contentObj = JSONObject().apply {
                        put("role", "user")
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", audioMimeType)
                                    put("data", base64)
                                })
                            })
                            put(JSONObject().apply {
                                put("text", "Respond in natural conversation to my voice.")
                            })
                        }
                        put("parts", parts)
                    }
                    contentsArray.put(contentObj)
                }

                put("contents", contentsArray)
            }

            val requestUrl = "$BASE_URL$MODEL_LIVE:generateContent?key=$apiKey"
            val body = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(requestUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Live API call failed: ${response.code} $responseString")
                return@withContext Result.failure(Exception("Live API error (${response.code}): $responseString"))
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            val firstCand = candidates?.optJSONObject(0)
            val content = firstCand?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val liveReply = parts?.optJSONObject(0)?.optString("text")?.trim() ?: "Connected with Gemini Live."

            Result.success(liveReply)
        } catch (e: Exception) {
            Log.e(TAG, "Error in sendLiveMessage", e)
            Result.failure(e)
        }
    }
}
