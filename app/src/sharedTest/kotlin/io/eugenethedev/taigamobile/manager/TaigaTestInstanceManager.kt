package io.eugenethedev.taigamobile.manager

import com.google.gson.Gson
import com.google.gson.JsonElement
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
) {
    val baseUrl = "http://$host:$port"

    lateinit var accessToken: String
    lateinit var refreshToken: String
    var userId: Long = -1
    var projectId: Long = -1

    private var isInitialized = false

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .addInterceptor (
            HttpLoggingInterceptor(::println)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    private fun Request.Builder.apiEndpoint(endpoint: String): Request.Builder {
        if (endpoint.startsWith("/") || endpoint.endsWith("/")) throw IllegalArgumentException("Endpoint must not have leading or trailing slashes")
        return url("$baseUrl/api/v1/$endpoint")
    }

    private fun String.toJsonBody() = toRequestBody("application/json".toMediaType())

    private fun Response.toJsonObject() = gson.fromJson(body?.string(), JsonElement::class.java).asJsonObject

    private fun Request.Builder.withAuth() = header("Authorization", "Bearer $accessToken")


    fun setup() {
        if (isInitialized) throw IllegalStateException("Taiga instance is already initialized. You need to .clear() it first")

        val isInitSuccessful = listOf(
            checkInstanceRunning(),

            createTestUser(),
            createTestProject()
        ).all { it }

        if (!isInitSuccessful) throw IllegalStateException("Some of the init steps were not finished successfully")

        isInitialized = true
    }

    fun clear() {
        if (!isInitialized) return

        val isClearSuccessful = listOf(
            deleteTestProject(),

            deleteTestUser()
        ).all { it }

        if (!isClearSuccessful) throw IllegalStateException("Some of the clear steps were not finished successfully")

        isInitialized = false
    }


    private fun checkInstanceRunning(): Boolean {
        val checkRequest = Request.Builder()
            .apiEndpoint("")
            .build()

        try {
            return client.newCall(checkRequest).execute().isSuccessful
        } catch (e: ConnectException) {
            throw IllegalArgumentException("No Taiga instance is running on $baseUrl")
        }
    }

    private fun createTestUser(): Boolean {
        val registerUserRequest = with(TestData) {
            Request.Builder()
                .apiEndpoint("auth/register")
                .post(
                    """
                    {
                        "accepted_terms": true,
                        "email": "$email",
                        "full_name": "$fullName",
                        "password": "$password",
                        "type": "public",
                        "username": "$username"
                    }
                """.trimIndent().toJsonBody()
                )
                .build()
        }

        return client.newCall(registerUserRequest)
            .execute()
            .toJsonObject()
            ?.let {
                accessToken = it.get("auth_token").asString ?: return@let false
                refreshToken = it.get("refresh").asString ?: return@let false
                userId = it.get("id").asLong.takeIf { it > -1 } ?: return@let false
                true
            } ?: false
    }

    private fun createTestProject(): Boolean {
        val createProjectRequest = with(TestData) {
            Request.Builder()
                .apiEndpoint("projects")
                .post(
                    """
                        {
                            "name": "$projectName",
                            "description": "$description",
                            "is_private": false
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
        }

        return client.newCall(createProjectRequest)
            .execute()
            .toJsonObject()
            ?.let {
                projectId = it.get("id").asLong.takeIf { it > -1 } ?: return@let false
                true
            } ?: false
    }


    private fun deleteTestUser(): Boolean {
        val deleteTestUserRequest = Request.Builder()
            .delete()
            .apiEndpoint("users/$userId")
            .withAuth()
            .build()

        return client.newCall(deleteTestUserRequest).execute().isSuccessful
    }

    private fun deleteTestProject(): Boolean {
        val deleteTestProjectRequest = Request.Builder()
            .delete()
            .apiEndpoint("projects/$projectId")
            .withAuth()
            .build()

        return client.newCall(deleteTestProjectRequest).execute().isSuccessful
    }
}
