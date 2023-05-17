package com.example.kmatool.di

import com.example.kmatool.data.models.repository.IMiniStudentRepository
import com.example.kmatool.data.models.repository.INoteRepository
import com.example.kmatool.data.models.repository.IPeriodRepository
import com.example.kmatool.data.models.repository.IProfileRepository
import com.example.kmatool.data.models.repository.IStudentRepository
import com.example.kmatool.data.repository.MiniStudentRepositoryImpl
import com.example.kmatool.data.repository.NoteRepositoryImpl
import com.example.kmatool.data.repository.PeriodRepositoryImpl
import com.example.kmatool.data.repository.ProfileRepositoryImpl
import com.example.kmatool.data.repository.StudentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideIMiniStudentRepository(
        miniStudentRepositoryImpl: MiniStudentRepositoryImpl
    ): IMiniStudentRepository {
        return miniStudentRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideINoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): INoteRepository {
        return noteRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideIPeriodRepository(
        periodRepositoryImpl: PeriodRepositoryImpl
    ): IPeriodRepository {
        return periodRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideIProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): IProfileRepository {
        return profileRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideIStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): IStudentRepository {
        return studentRepositoryImpl
    }
}