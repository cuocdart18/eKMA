package com.example.kmatool.di

import com.example.kmatool.data.data_source.apis.ScheduleAPI
import com.example.kmatool.data.data_source.apis.ScoreAPI
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
    @Named("ScheduleSite")
    fun provideRetrofitScheduleSite(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.example.kmatool.data.data_source.apis.ApiConfig.BASE_SCHEDULE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApiScheduleService(@Named("ScheduleSite") retrofit: Retrofit): ScheduleAPI {
        return retrofit.create(ScheduleAPI::class.java)
    }

    @Provides
    @Singleton
    @Named("ScoreSite")
    fun provideRetrofitScoreSite(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.example.kmatool.data.data_source.apis.ApiConfig.BASE_SCORE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApiScoreService(@Named("ScoreSite") retrofit: Retrofit): ScoreAPI {
        return retrofit.create(ScoreAPI::class.java)
    }
}