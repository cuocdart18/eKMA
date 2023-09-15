package com.app.ekma.di

import android.app.Application
import android.content.Context
import com.app.ekma.alarm.AlarmEventsScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideAlarmEventsScheduler(context: Context): AlarmEventsScheduler {
        return AlarmEventsScheduler(context)
    }
}