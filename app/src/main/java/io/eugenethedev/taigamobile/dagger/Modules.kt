package io.eugenethedev.taigamobile.dagger

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    fun provideTaigaApi(session: Session): TaigaApi {
        val baseUrlPlaceholder = "https://nothing.nothing"
        return Retrofit.Builder()
            .baseUrl(baseUrlPlaceholder) // base url is set dynamically in interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        it.run {
                            proceed(
                                request()
                                    .newBuilder()
                                    .url(it.request().url.toUrl().toExternalForm().replace(baseUrlPlaceholder, session.server + TaigaApi.API_PREFIX))
                                    .addHeader("User-Agent", "Taiga App")
                                    .addHeader("Authorization", "Bearer ${session.token}")
                                    .build()
                            )
                        }
                    }
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()
            .create(TaigaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSession(context: Context) = Session(context)
}

@Module
abstract class RepositoriesModule {
    @Binds abstract fun bindIAuthRepository(authRepository: AuthRepository): IAuthRepository
}