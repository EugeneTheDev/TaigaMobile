package io.eugenethedev.taigamobile.manager

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.eugenethedev.taigamobile.data.api.CommonTaskPathSingular
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.testdata.Epic
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.net.ConnectException

class TaigaTestInstanceManager(
    host: String = "localhost",
    port: Int = 9000
) : Creations, Deletions {

    val baseUrl = "http://$host:$port"

    val gson = Gson()
    val client = OkHttpClient.Builder()
        .addInterceptor (
            HttpLoggingInterceptor(::println)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    var isInitialized = false

    lateinit var accessToken: String
    lateinit var refreshToken: String
    var userId: Long = -1
    var projectId: Long = -1

    lateinit var epicToId: Map<Epic, Long>

    fun Request.Builder.apiEndpoint(endpoint: String): Request.Builder {
        if (endpoint.startsWith("/") || endpoint.endsWith("/")) throw IllegalArgumentException("Endpoint must not have leading or trailing slashes")
        return url("$baseUrl/api/v1/$endpoint")
    }

    fun String.toJsonBody() = toRequestBody("application/json".toMediaType())
    fun Request.Builder.withAuth() = header("Authorization", "Bearer $accessToken")
    fun Request.execute() = client.newCall(this).execute()
    fun Response.toJsonObject(): JsonObject = gson.fromJson(body?.string(), JsonElement::class.java).asJsonObject
    fun Response.toJsonArray(): JsonArray = gson.fromJson(body?.string(), JsonElement::class.java).asJsonArray
    fun Response.successOrThrow() {
        if (!isSuccessful) throw IllegalStateException("Got response code $code")
    }

    fun setup() {
        if (isInitialized) throw IllegalStateException("Taiga instance is already initialized. You need to .clear() it first")

        try {
            checkInstanceRunning()

            createTestUser()
            createTestProject()
            createEpics()
        } catch (e: Exception) {
            throw IllegalStateException("Some of the init steps were not finished successfully", e)
        }

        isInitialized = true
    }

    fun clear() {
        if (!isInitialized) return

        try {
            deleteTestProject()
            deleteTestUser()

            accessToken = ""
            refreshToken = ""
            userId = -1
            projectId = -1
            epicToId = emptyMap()
        } catch (e: Exception) {
            throw IllegalStateException("Some of the clear steps were not finished successfully", e)
        }

        isInitialized = false
    }

    private fun checkInstanceRunning(): Boolean {
        val checkRequest = Request.Builder()
            .apiEndpoint("")
            .build()

        try {
            return checkRequest.execute().isSuccessful
        } catch (e: ConnectException) {
            throw IllegalArgumentException("No Taiga instance is running on $baseUrl")
        }
    }

    /**
     * Some util functions
     */
    fun getClosedStatusId(commonTaskType: CommonTaskType) = Request.Builder()
            .apiEndpoint("${CommonTaskPathSingular(commonTaskType).path}-statuses?project=$projectId")
            .get()
            .withAuth()
            .build()
            .execute()
            .toJsonArray()
            .map { it.asJsonObject }
            .find { it.get("is_closed").asBoolean }
            ?.get("id")
            ?.asLong ?: throw IllegalStateException("No closed statuses? Really?")
}
