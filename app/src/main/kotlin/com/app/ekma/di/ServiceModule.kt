package com.app.ekma.di

import com.app.ekma.data.models.service.IAgoraService
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.data.models.service.IScoreService
import com.app.ekma.data.models.service.IUserService
import com.app.ekma.data.service.AgoraService
import com.app.ekma.data.service.FcmService
import com.app.ekma.data.service.LoginService
import com.app.ekma.data.service.NoteService
import com.app.ekma.data.service.ProfileService
import com.app.ekma.data.service.ScheduleService
import com.app.ekma.data.service.ScoreService
import com.app.ekma.data.service.UserService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun provideILoginService(
        loginService: LoginService
    ): ILoginService

    @Binds
    @Singleton
    abstract fun provideINoteService(
        noteService: NoteService
    ): INoteService

    @Binds
    @Singleton
    abstract fun provideIProfileService(
        profileService: ProfileService
    ): IProfileService

    @Binds
    @Singleton
    abstract fun provideIScheduleService(
        scheduleService: ScheduleService
    ): IScheduleService

    @Binds
    @Singleton
    abstract fun provideIScoreService(
        scoreService: ScoreService
    ): IScoreService

    @Binds
    @Singleton
    abstract fun provideIUserService(
        userService: UserService
    ): IUserService

    @Binds
    @Singleton
    abstract fun provideIFcmService(
        fcmService: FcmService
    ): IFcmService

    @Binds
    @Singleton
    abstract fun provideIAgoraService(
        agoraService: AgoraService
    ): IAgoraService
}