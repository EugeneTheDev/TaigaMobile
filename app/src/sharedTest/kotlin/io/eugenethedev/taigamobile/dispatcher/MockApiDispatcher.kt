package io.eugenethedev.taigamobile.dispatcher

import io.eugenethedev.taigamobile.data.api.TaigaApi
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.http.*


class MockApiDispatcher : Dispatcher() {
    companion object {
        const val authToken = "this7is7auth"
        const val refreshToken = "this7is7refresh"
        const val userId = 1L
        const val mainTestProjectName = "Test"
        const val mainTestProjectId = 1L
        const val testPassword = "testpassword"
        const val testUsername = "test"
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val noSuchEndpointResponse = MockResponse().setResponseCode(404).setBody("""{"message": "No such endpoint: ${request.path}"}""")
        val noSuchMethodResponse = MockResponse().setResponseCode(404).setBody("""{"message": "No such method: ${request.method}"}""")

        val requestPath = request.path?.replace("/${TaigaApi.API_PREFIX}/", "")
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
                requestPath.orEmpty() matches annotationPath.replace("""\{.+}""".toRegex(), ".+").toRegex()
            }
            ?.invoke(
                this,
                request, // pass request to handling method
                // create map for path params
                // for template path from annotation like "foo/{path_param}" and request path like "foo/123" it will produce Map("path_param" -> 123)
                (requestPath?.split("/").orEmpty() zip annotationPath.split("/")).mapNotNull { (requestPart, annotationPart) ->
                    if (annotationPart matches """\{.+}""".toRegex()) {
                        annotationPart.trim('{').trim('}') to requestPart
                    } else {
                        null
                    }
                }.toMap()
            ) as? MockResponse
            ?: noSuchEndpointResponse
    }

    @POST("auth")
    fun handleAuth(request: RecordedRequest, pathParams: Map<String, String>): MockResponse {
        return MockResponse().setResponseCode(200).setBody("""
            {"id": $userId, "username": "Test", "full_name": "Test", "full_name_display": "Test Test", "color": "#19db70", "bio": "", "lang": "", "theme": "", "timezone": "", "is_active": true, "photo": null, "big_photo": null, "gravatar_id": "1234567faf45", "roles": ["Product Owner"], "total_private_projects": 1, "total_public_projects": 1, "email": "test@test.com", "uuid": "000000000000000000", "date_joined": "2019-03-05T11:23:18.539Z", "read_new_terms": true, "accepted_terms": true, "max_private_projects": 1, "max_public_projects": null, "max_memberships_private_projects": 3, "max_memberships_public_projects": null, "verified_email": true, "refresh": "$refreshToken", "auth_token": "$authToken"}
        """.trimIndent())
    }
}
