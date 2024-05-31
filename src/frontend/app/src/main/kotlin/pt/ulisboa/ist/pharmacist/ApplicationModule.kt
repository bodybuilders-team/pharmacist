package pt.ulisboa.ist.pharmacist

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.repository.remote.upload.UploaderApi
import pt.ulisboa.ist.pharmacist.repository.remote.users.UsersApi
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.session.SessionManagerSharedPrefs
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
//            .connectTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
//            .readTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
//            .writeTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
            //.connectionSpecs(listOf(okhttp3.ConnectionSpec.MODERN_TLS))
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManagerSharedPrefs(context)
    }

    @Provides
    @Singleton
    fun providePharmacistDatabase(@ApplicationContext context: Context): PharmacistDatabase {
        return Room.databaseBuilder(
            context,
            PharmacistDatabase::class.java,
            "pharmacist.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMedicineApi(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient,
        sessionManager: SessionManager
    ): MedicineApi {
        return MedicineApi(
            context = context,
            httpClient = httpClient,
            sessionManager = sessionManager
        )
    }

    @Provides
    @Singleton
    fun providePharmacyApi(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient,
        sessionManager: SessionManager
    ): PharmacyApi {
        return PharmacyApi(
            context = context,
            httpClient = httpClient,
            sessionManager = sessionManager
        )
    }

    @Provides
    @Singleton
    fun provideUsersApi(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient,
        sessionManager: SessionManager
    ): UsersApi {
        return UsersApi(
            context = context,
            httpClient = httpClient,
            sessionManager = sessionManager
        )
    }

    @Provides
    @Singleton
    fun provideUploaderApi(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient,
        sessionManager: SessionManager
    ): UploaderApi {
        return UploaderApi(
            context = context,
            httpClient = httpClient,
            sessionManager = sessionManager
        )
    }

    @Provides
    @Singleton
    fun provideRealTimeUpdatesService(
        sessionManager: SessionManager,
        httpClient: OkHttpClient
    ): RealTimeUpdatesService {
        return RealTimeUpdatesService(
            httpClient = httpClient,
            sessionManager = sessionManager
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context)
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        Places.initialize(context, context.getString(R.string.google_maps_key))
        return Places.createClient(context)
    }
}