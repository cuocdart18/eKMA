package com.app.ekma.di

import android.app.Application
import com.app.ekma.data.data_source.database.AppDatabase
import com.app.ekma.data.data_source.database.daos.MiniStudentDao
import com.app.ekma.data.data_source.database.daos.NoteDao
import com.app.ekma.data.data_source.database.daos.PeriodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.getInstance(application)
    }

    @Singleton
    @Provides
    fun provideMiniStudentDao(appDatabase: AppDatabase): MiniStudentDao {
        return appDatabase.miniStudentDao()
    }

    @Singleton
    @Provides
    fun providePeriodDao(appDatabase: AppDatabase): PeriodDao {
        return appDatabase.periodDao()
    }

    @Singleton
    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao {
        return appDatabase.noteDao()
    }
}