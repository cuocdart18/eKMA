package com.example.kmatool.di

import com.example.kmatool.data.models.repository.IAuth
import com.example.kmatool.data.models.repository.IMiniStudentRepository
import com.example.kmatool.data.models.repository.INoteRepository
import com.example.kmatool.data.models.repository.IPeriodRepository
import com.example.kmatool.data.models.repository.IProfileRepository
import com.example.kmatool.data.models.repository.IStudentRepository
import com.example.kmatool.data.models.repository.IUserRepository
import com.example.kmatool.data.repository.AuthImpl
import com.example.kmatool.data.repository.MiniStudentRepositoryImpl
import com.example.kmatool.data.repository.NoteRepositoryImpl
import com.example.kmatool.data.repository.PeriodRepositoryImpl
import com.example.kmatool.data.repository.ProfileRepositoryImpl
import com.example.kmatool.data.repository.StudentRepositoryImpl
import com.example.kmatool.data.repository.UserRepositoryImpl
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
    fun provideIAuth(
        authImpl: AuthImpl
    ): IAuth {
        return authImpl
    }

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

    @Provides
    @Singleton
    fun provideIUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): IUserRepository {
        return userRepositoryImpl
    }
}