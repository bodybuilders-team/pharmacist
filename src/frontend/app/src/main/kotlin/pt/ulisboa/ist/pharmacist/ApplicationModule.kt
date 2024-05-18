package pt.ulisboa.ist.pharmacist

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.session.SessionManagerSharedPrefs

@Module
@InstallIn(PharmacistApplication::class)
object ApplicationModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
            //.connectionSpecs(listOf(okhttp3.ConnectionSpec.MODERN_TLS))
            .build()
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    fun provideSessionManager(context: Context): SessionManager {
        return SessionManagerSharedPrefs(context)
    }

    @Binds
    fun bindDependenciesContainer(application: PharmacistApplication): DependenciesContainer {
        return application
    }
}