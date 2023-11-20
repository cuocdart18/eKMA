package com.app.ekma.di

import com.app.ekma.data.data_source.apis.AgoraAPI
import com.app.ekma.data.data_source.apis.ApiConfig.BASE_AGORA_APP_URL
import com.app.ekma.data.data_source.apis.ApiConfig.BASE_APP_URL
import com.app.ekma.data.data_source.apis.ApiConfig.BASE_SCORE_URL
import com.app.ekma.data.data_source.apis.EKmaAPI
import com.app.ekma.data.data_source.apis.ScoreAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    @Named("AppSite")
    fun provideRetrofitAppSite(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_APP_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApiAppService(@Named("AppSite") retrofit: Retrofit): EKmaAPI {
        return retrofit.create(EKmaAPI::class.java)
    }

    @Provides
    @Singleton
    @Named("ScoreSite")
    fun provideRetrofitScoreSite(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_SCORE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApiScoreService(@Named("ScoreSite") retrofit: Retrofit): ScoreAPI {
        return retrofit.create(ScoreAPI::class.java)
    }

    @Provides
    @Singleton
    @Named("AgoraSite")
    fun provideRetrofitAgoraSite(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_AGORA_APP_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApiAgoraService(@Named("AgoraSite") retrofit: Retrofit): AgoraAPI {
        return retrofit.create(AgoraAPI::class.java)
    }
}