package io.eugenethedev.taigamobile.manager

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.eugenethedev.taigamobile.data.api.CommonTaskPathSingular
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.testdata.Project
import io.eugenethedev.taigamobile.testdata.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.net.ConnectException
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class TaigaTestInstanceManager(
    val host: String = "localhost",
) : Creations, Deletions {
    private val port: Int = 9000

    val baseUrl = "http://$host:$port"

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .addInterceptor (
            HttpLoggingInterceptor(::println)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    // for internal use only, will be initialized only on .coldInit()
    lateinit var _userToUserData: Map<User, UserData>
    lateinit var _projectToProjectData: Map<Project, ProjectData>


    lateinit var activeUser: UserInfo

    fun Request.Builder.apiEndpoint(endpoint: String): Request.Builder {
        if (endpoint.startsWith("/") || endpoint.endsWith("/")) throw IllegalArgumentException("Endpoint must not have leading or trailing slashes")
        return url("$baseUrl/api/v1/$endpoint")
    }

    fun String.toJsonBody() = toRequestBody("application/json".toMediaType())
    fun Request.Builder.withAuth(user: User) = header("Authorization", "Bearer ${_userToUserData[user]!!.accessToken}")
    fun Request.execute() = client.newCall(this).execute()
    fun Response.toJsonObject(): JsonObject = gson.fromJson(body?.string(), JsonElement::class.java).asJsonObject
    fun Response.toJsonArray(): JsonArray = gson.fromJson(body?.string(), JsonElement::class.java).asJsonArray

    fun Response.successOrThrow() {
        if (!isSuccessful) throw IllegalStateException("Got response code $code")
    }

    inline fun tx(action: Connection.() -> Unit) {
        DriverManager.getConnection(
            "jdbc:postgresql://$host:5432/taiga",
            Properties().apply {
                put("user", "taiga")
                put("password", "taiga")
                put("driver", "org.postgresql.Driver")
            }
        ).use { it.action() }
    }

    val _snapshotPostfix = "snapshot"

    fun Connection.getAllProdTables() = sequence<String> {
        createStatement()
            .executeQuery("select * from information_schema.tables where table_schema = 'public' and table_name not like '%$_snapshotPostfix' and table_name not like '%django%'").let {
                while (it.next()) {
                    yield(it.getString("table_name"))
                }
            }
    }

    fun setup() {
        try {
            checkInstanceRunning()

            tx {
                createStatement().executeQuery("select count(*) as cnt from users_user where email_token is not null").let {
                    it.next()
                    if (it.getLong("cnt") != 0L) throw IllegalStateException("Taiga instance is already initialized. You need to .clear() it first")
                }
            }

            initData()
        } catch (e: Exception) {
            throw IllegalStateException("Some of the init steps were not finished successfully", e)
        }

    }

    fun clear() {
        try {
            clearTables()
            _userToUserData = emptyMap()
            _projectToProjectData = emptyMap()
        } catch (e: Exception) {
            throw IllegalStateException("Some of the clear steps were not finished successfully", e)
        }
    }

    private fun checkInstanceRunning() {
        val checkRequest = Request.Builder()
            .apiEndpoint("")
            .build()

        try {
            // healthcheck - repeatedly ping server to check if it's warmed up
            repeat(10) {
                checkRequest.execute().let {
                    if (it.code >= 500) {
                        println("Instance is not ready yet, waiting 8s before another try")
                        Thread.sleep(8000)
                    } else {
                        it.successOrThrow()
                    }
                }
            }
            throw IllegalStateException("Waiting timeout for Taiga instance is exceed")
        } catch (e: ConnectException) {
            throw IllegalArgumentException("No Taiga instance is running on $baseUrl")
        }
    }

    /**
     * Some util functions
     */
    fun getClosedStatusId(commonTaskType: CommonTaskType, projectId: Long, user: User) = Request.Builder()
            .apiEndpoint("${CommonTaskPathSingular(commonTaskType).path}-statuses?project=$projectId")
            .get()
            .withAuth(user)
            .build()
            .execute()
            .toJsonArray()
            .map { it.asJsonObject }
            .find { it.get("is_closed").asBoolean }
            ?.get("id")
            ?.asLong ?: throw IllegalStateException("No closed statuses? Really?")
}
