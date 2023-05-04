package com.example.kmatool.di

import android.app.Application
import com.example.kmatool.common.DataLocalManager
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.common.MySharePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(application: Application): DataStoreManager {
        return DataStoreManager(application)
    }

    @Provides
    @Singleton
    fun provideMySharePreferences(application: Application): MySharePreferences {
        return MySharePreferences(application)
    }

    @Provides
    @Singleton
    fun provideDataLocalManager(
        mySharePreferences: MySharePreferences,
        dataStoreManager: DataStoreManager
    ): DataLocalManager {
        return DataLocalManager(mySharePreferences, dataStoreManager)
    }
}