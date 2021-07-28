package io.eugenethedev.taigamobile.dagger

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.Settings
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.repositories.*
import io.eugenethedev.taigamobile.domain.repositories.*
import okhttp3.OkHttpClient
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
    fun provideTaigaApi(session: Session): TaigaApi {
        val baseUrlPlaceholder = "https://nothing.nothing"
        return Retrofit.Builder()
            .baseUrl(baseUrlPlaceholder) // base url is set dynamically in interceptor
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls()
                        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
                        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter().nullSafe())
                        .create()
                )
            )
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        it.run {
                            proceed(
                                request()
                                    .newBuilder()
                                    .url(it.request().url.toUrl().toExternalForm().replace(baseUrlPlaceholder, "https://${session.server}/${TaigaApi.API_PREFIX}"))
                                    .addHeader("User-Agent", "TaigaMobile/${BuildConfig.VERSION_NAME}")
                                    .addHeader("Authorization", "Bearer ${session.token}")
                                    .build()
                            )
                        }
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor(Timber::d)
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                            .also { it.redactHeader("Authorization") }
                    )
                    .build()
            )
            .build()
            .create(TaigaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSession(context: Context) = Session(context)

    @Provides
    @Singleton
    fun provideSettings(context: Context) = Settings(context)
}

@Module
abstract class RepositoriesModule {
    @Singleton
    @Binds
    abstract fun bindIAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Singleton
    @Binds
    abstract fun bindISearchRepository(searchRepository: SearchRepository): ISearchRepository

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
