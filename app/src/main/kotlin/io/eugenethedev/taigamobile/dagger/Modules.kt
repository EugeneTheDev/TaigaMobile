package io.eugenethedev.taigamobile.dagger

import android.content.ClipboardManager
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.state.Settings
import io.eugenethedev.taigamobile.data.api.RefreshTokenRequest
import io.eugenethedev.taigamobile.data.api.RefreshTokenResponse
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.repositories.*
import io.eugenethedev.taigamobile.domain.repositories.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideTaigaApi(session: Session, gson: Gson): TaigaApi {
        val baseUrlPlaceholder = "https://nothing.nothing"
        fun getApiUrl() = // for compatibility with older app versions
            if (!session.server.value.run { startsWith("https://") || startsWith("http://") }) {
                "https://"
            } else {
                ""
            } + "${session.server.value}/${TaigaApi.API_PREFIX}"

        val okHttpBuilder = OkHttpClient.Builder()
            .addInterceptor {
                it.run {
                    val url = it.request().url.toUrl().toExternalForm()

                    proceed(
                        request()
                            .newBuilder()
                            .url(url.replace(baseUrlPlaceholder, getApiUrl()))
                            .header("User-Agent", "TaigaMobile/${BuildConfig.VERSION_NAME}")
                            .also {
                                if ("/${TaigaApi.AUTH_ENDPOINTS}" !in url) { // do not add Authorization header to authorization requests
                                    it.header("Authorization", "Bearer ${session.token.value}")
                                }
                            }
                            .build()
                    )
                }
            }
            .addInterceptor(
                HttpLoggingInterceptor(Timber::d)
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                    .also { it.redactHeader("Authorization") }
            )

        val tokenClient = okHttpBuilder.build()

        return Retrofit.Builder()
            .baseUrl(baseUrlPlaceholder) // base url is set dynamically in interceptor
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                okHttpBuilder.authenticator { _, response ->
                        response.request.header("Authorization")?.let {
                            try {
                                // prevent multiple refresh requests from different threads
                                synchronized(session) {
                                    // refresh token only if it was not refreshed in another thread
                                    if (it.replace("Bearer ", "") == session.token.value) {
                                        val body = gson.toJson(RefreshTokenRequest(session.refreshToken.value))

                                        val request = Request.Builder()
                                            .url("$baseUrlPlaceholder/${TaigaApi.REFRESH_ENDPOINT}")
                                            .post(body.toRequestBody("application/json".toMediaType()))
                                            .build()

                                        val refreshResponse = gson.fromJson(
                                            tokenClient.newCall(request).execute().body?.string(),
                                            RefreshTokenResponse::class.java
                                        )

                                        session.changeAuthCredentials(refreshResponse.auth_token, refreshResponse.refresh)
                                    }
                                }

                                response.request.newBuilder()
                                    .header("Authorization", "Bearer ${session.token.value}")
                                    .build()
                            } catch (e: Exception) {
                                Timber.w(e)
                                session.changeAuthCredentials("", "")
                                null
                            }
                        }
                    }
                    .build()
            )
            .build()
            .create(TaigaApi::class.java)
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().serializeNulls()
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter().nullSafe())
        .create()

    @Provides
    @Singleton
    fun provideSession(context: Context) = Session(context)

    @Provides
    @Singleton
    fun provideSettings(context: Context) = Settings(context)

    @Provides
    @Singleton
    fun provideClipboard(context: Context) = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}

@Module
abstract class RepositoriesModule {
    @Singleton
    @Binds
    abstract fun bindIAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Singleton
    @Binds
    abstract fun bindIProjectsRepository(searchRepository: ProjectsRepository): IProjectsRepository

    @Singleton
    @Binds
    abstract fun bindIStoriesRepository(storiesRepository: TasksRepository): ITasksRepository

    @Singleton
    @Binds
    abstract fun bindIUsersRepository(usersRepository: UsersRepository): IUsersRepository

    @Singleton
    @Binds
    abstract fun bindISprintsRepository(sprintsRepository: SprintsRepository): ISprintsRepository
}
