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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideILoginService(
        loginService: LoginService
    ): ILoginService {
        return loginService
    }

    @Provides
    @Singleton
    fun provideINoteService(
        noteService: NoteService
    ): INoteService {
        return noteService
    }

    @Provides
    @Singleton
    fun provideIProfileService(
        profileService: ProfileService
    ): IProfileService {
        return profileService
    }

    @Provides
    @Singleton
    fun provideIScheduleService(
        scheduleService: ScheduleService
    ): IScheduleService {
        return scheduleService
    }

    @Provides
    @Singleton
    fun provideIScoreService(
        scoreService: ScoreService
    ): IScoreService {
        return scoreService
    }

    @Provides
    @Singleton
    fun provideIUserService(
        userService: UserService
    ): IUserService {
        return userService
    }

    @Provides
    @Singleton
    fun provideIFcmService(
        fcmService: FcmService
    ): IFcmService {
        return fcmService
    }

    @Provides
    @Singleton
    fun provideIAgoraService(
        agoraService: AgoraService
    ): IAgoraService {
        return agoraService
    }
}