package io.eugenethedev.taigamobile.dispatcher

import com.google.gson.*
import io.eugenethedev.taigamobile.data.api.TaigaApi
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.http.*


class MockApiDispatcher(private val gson: Gson) : Dispatcher() {
    companion object {
        const val authToken = "this7is7auth"
        const val refreshToken = "this7is7refresh"
        const val userId = 1L
        const val mainTestProjectName = "Test"
        const val mainTestProjectId = 1L
        const val testPassword = "testpassword"
        const val testUsername = "test"
    }

    private val badRequestResponse get() = MockResponse().setResponseCode(400).setBody("""{"message": "Malformed request"}""")
    private val unauthorizedResponse get() = MockResponse().setResponseCode(401).setBody("""{"message": "Missing or illegal authorization"}""")
    private val notFoundResponse get() = MockResponse().setResponseCode(404).setBody("""{"message": "Resource was not found"}""")
    private val noSuchMethodResponse get() = MockResponse().setResponseCode(404).setBody("""{"message": "No such method"}""")

    private val successResponse get() = MockResponse().setResponseCode(200)

    private fun MockResponse.setFileBody(fileName: String) = setBody(
        javaClass.getResourceAsStream("/responses/$fileName")
            ?.bufferedReader()
            ?.use { it.readText() }
            .orEmpty()
    )

    override fun dispatch(request: RecordedRequest): MockResponse {

        val requestPath = request.requestUrl?.encodedPath?.replace("/${TaigaApi.API_PREFIX}/", "").orEmpty()
        if ("auth" !in requestPath && request.getHeader("Authorization") != "Bearer $authToken") {
            return unauthorizedResponse
        }

        val requiredAnnotation = when(request.method) {
            "GET" -> GET::class.java
            "POST" -> POST::class.java
            "DELETE" -> DELETE::class.java
            "PUT" -> PUT::class.java
            "PATCH" -> PATCH::class.java
            else -> return noSuchMethodResponse
        }

        var annotationPath = ""
        return javaClass.methods.filter { it.isAnnotationPresent(requiredAnnotation) } // find method with correct annotation
            .firstOrNull {
                // get specified path from this annotation
                annotationPath = it.getAnnotation(requiredAnnotation)
                    ?.let { it.javaClass.getDeclaredMethod("value").invoke(it) }
                    ?.toString()
                    .orEmpty()

                // check if request path matches path from annotation
                requestPath matches annotationPath.replace("""\{.+}""".toRegex(), ".+").toRegex()
            }
            ?.invoke(
                this,
                request, // pass request to handling method
                // create map for path params
                // for template path from annotation like "foo/{path_param}" and request path like "foo/123" it will produce Map("path_param" -> 123)
                (requestPath.split("/") zip annotationPath.split("/")).mapNotNull { (requestPart, annotationPart) ->
                    if (annotationPart matches """\{.+}""".toRegex()) {
                        annotationPart.trim('{').trim('}') to requestPart
                    } else {
                        null
                    }
                }.toMap(),
                // parse json object from string body
                try {
                    val body = request.body.readString(Charsets.UTF_8)
                    gson.fromJson(body, JsonElement::class.java)
                        ?.asJsonObject
                        ?: JsonObject()
                } catch (e: JsonParseException) {
                    return badRequestResponse
                }
            ) as? MockResponse
            ?: notFoundResponse
    }

    /**
     * Auth
     */

    @POST("auth")
    fun handleAuth(request: RecordedRequest, pathParams: Map<String, String>, body: JsonObject): MockResponse {
        return if (body["password"].asString == testPassword && body["username"].asString == testUsername) {
            successResponse.setFileBody("auth.json")
        } else {
            badRequestResponse
        }
    }

    @POST("auth/refresh")
    fun handleAuthRefresh(request: RecordedRequest, pathParams: Map<String, String>, body: JsonObject): MockResponse {
        return if (body["refresh"].asString == refreshToken) {
            successResponse.setFileBody("refresh_token.json")
        } else {
            unauthorizedResponse
        }
    }

    /**
     * Projects
     */

    @GET("projects/{id}")
    fun handleGetProject(request: RecordedRequest, pathParams: Map<String, String>, body: JsonObject): MockResponse {
        return if (pathParams["id"]?.toLongOrNull() == mainTestProjectId) {
            successResponse.setFileBody("project.json")
        } else {
            notFoundResponse
        }
    }

    @GET("projects")
    fun handleSearchProjects(request: RecordedRequest, pathParams: Map<String, String>, body: JsonObject): MockResponse {
        val url = request.requestUrl?.takeIf {
            // because only these parameters would give us correct results
            it.queryParameter("order_by") == "user_order" && it.queryParameter("slight") == "true"
        } ?: return badRequestResponse
        if (url.queryParameter("page")?.toInt() ?: 1 > 1) return notFoundResponse // only one page of results
        return successResponse.setFileBody("search_projects.json")
    }
}
