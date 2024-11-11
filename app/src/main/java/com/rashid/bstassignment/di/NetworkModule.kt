package com.rashid.bstassignment.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.rashid.bstassignment.data.network.Auth
import com.rashid.bstassignment.data.network.NetworkApi
import com.rashid.bstassignment.data.localDataSource.db.BSTDatabase
import com.rashid.bstassignment.data.localDataSource.dao.UserImageDao
import com.rashid.bstassignment.data.network.CustomExecutor
import com.rashid.bstassignment.data.repository.ImagePostRepoImpl
import com.rashid.bstassignment.data.repository.NetworkStatusDataSource
import com.rashid.bstassignment.data.repository.NetworkStatusDataSourceImpl
import com.rashid.bstassignment.data.repository.NetworkStatusRepoImp
import com.rashid.bstassignment.domain.repository.NetworkStatusRepo
import com.rashid.bstassignment.domain.useCases.impl.ObserveNetworkStatusUseCaseImp
import com.rashid.bstassignment.domain.repository.ImagePostRepo
import com.rashid.bstassignment.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Singleton
    @Provides
    @Auth
    fun provideRetrofitAuth(okHttp: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            addConverterFactory(GsonConverterFactory.create())
            client(okHttp)
            baseUrl(Constants.BASE_URL)
        }.build()
    }

    @Singleton
    @Provides
    fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        val executorService = CustomExecutor.createExecutor(3)
        // Set up a custom Dispatcher with the ExecutorService
        val dispatcher = Dispatcher(executorService)
        return OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            dispatcher(dispatcher)
            addInterceptor { chain ->
                chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .build()
                    .let(chain::proceed)
            }
            addInterceptor(httpInterceptor())
        }.build()
    }

    @Singleton
    @Provides
    fun httpInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideNetworkApi(@Auth retrofit: Retrofit) : NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BSTDatabase {
        return Room.databaseBuilder(
            context,
            BSTDatabase::class.java,
            Constants.BST_DATABASE
        ).build()
    }

    @Provides
    fun provideUserImageDao(database: BSTDatabase): UserImageDao {
        return database.userImageDao()
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideImageUploadRepository(networkApi: NetworkApi, bstDatabase:BSTDatabase) : ImagePostRepo {
        return ImagePostRepoImpl(networkApi,bstDatabase)
    }


    @Singleton
    @Provides
    fun provideNetworkStatusDataSource(context: Context): NetworkStatusDataSource {
        return NetworkStatusDataSourceImpl(context)
    }

    @Singleton
    @Provides
    fun provideNetworkStatusRepository(
        networkStatusDataSource: NetworkStatusDataSource
    ): NetworkStatusRepo {
        return NetworkStatusRepoImp(networkStatusDataSource)
    }

//    @Singleton
//    @Provides
//    fun provideObserveNetworkStatusUseCase(
//        networkStatusRepository: NetworkStatusRepo
//    ): ObserveNetworkStatusUseCaseImp {
//        return ObserveNetworkStatusUseCaseImp(networkStatusRepository)
//    }

//    @Singleton
//    @Provides
//    fun provideImagePostUseCase(
//        imageRepo: ImagePostRepo
//    ): ImagePostUseCaseImpl {
//        return ImagePostUseCaseImpl(imageRepo)
//    }

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }


}
