package com.app.ekma.di

import com.app.ekma.data.models.repository.IAuth
import com.app.ekma.data.models.repository.IMiniStudentRepository
import com.app.ekma.data.models.repository.INoteRepository
import com.app.ekma.data.models.repository.IPeriodRepository
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.repository.IStudentRepository
import com.app.ekma.data.models.repository.IUserRepository
import com.app.ekma.data.repository.AuthImpl
import com.app.ekma.data.repository.MiniStudentRepositoryImpl
import com.app.ekma.data.repository.NoteRepositoryImpl
import com.app.ekma.data.repository.PeriodRepositoryImpl
import com.app.ekma.data.repository.ProfileRepositoryImpl
import com.app.ekma.data.repository.StudentRepositoryImpl
import com.app.ekma.data.repository.UserRepositoryImpl
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