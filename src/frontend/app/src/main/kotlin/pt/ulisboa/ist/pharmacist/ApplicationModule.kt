package pt.ulisboa.ist.pharmacist

import android.content.Context
import android.location.Geocoder
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.session.SessionManagerSharedPrefs

@Component

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
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManagerSharedPrefs(context)
    }

    @Binds
    fun bindDependenciesContainer(application: PharmacistApplication): DependenciesContainer {
        return application
    }

    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context)
    }

    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        Places.initialize(context, context.getString(R.string.google_maps_key))
        return Places.createClient(context)
    }
}