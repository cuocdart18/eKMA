package com.app.ekma.di

import com.app.ekma.data.models.repository.IAgoraTokenRepository
import com.app.ekma.data.models.repository.IAuth
import com.app.ekma.data.models.repository.IFcmRepository
import com.app.ekma.data.models.repository.IMiniStudentRepository
import com.app.ekma.data.models.repository.INoteRepository
import com.app.ekma.data.models.repository.IPeriodRepository
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.repository.IStudentRepository
import com.app.ekma.data.models.repository.IUserRepository
import com.app.ekma.data.repository.AgoraTokenRepositoryImpl
import com.app.ekma.data.repository.AuthImpl
import com.app.ekma.data.repository.FcmRepositoryImpl
import com.app.ekma.data.repository.MiniStudentRepositoryImpl
import com.app.ekma.data.repository.NoteRepositoryImpl
import com.app.ekma.data.repository.PeriodRepositoryImpl
import com.app.ekma.data.repository.ProfileRepositoryImpl
import com.app.ekma.data.repository.StudentRepositoryImpl
import com.app.ekma.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideIAuth(
        authImpl: AuthImpl
    ): IAuth

    @Binds
    @Singleton
    abstract fun provideIMiniStudentRepository(
        miniStudentRepositoryImpl: MiniStudentRepositoryImpl
    ): IMiniStudentRepository

    @Binds
    @Singleton
    abstract fun provideINoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): INoteRepository

    @Binds
    @Singleton
    abstract fun provideIPeriodRepository(
        periodRepositoryImpl: PeriodRepositoryImpl
    ): IPeriodRepository

    @Binds
    @Singleton
    abstract fun provideIProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): IProfileRepository

    @Binds
    @Singleton
    abstract fun provideIStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): IStudentRepository

    @Binds
    @Singleton
    abstract fun provideIUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): IUserRepository

    @Binds
    @Singleton
    abstract fun provideIFcmRepository(
        fcmRepositoryImpl: FcmRepositoryImpl
    ): IFcmRepository

    @Binds
    @Singleton
    abstract fun provideIAgoraTokenRepository(
        agoraTokenRepositoryImpl: AgoraTokenRepositoryImpl
    ): IAgoraTokenRepository
}