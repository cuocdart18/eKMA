package com.example.kmatool.di

import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IScoreService
import com.example.kmatool.data.service.LoginService
import com.example.kmatool.data.service.NoteService
import com.example.kmatool.data.service.ProfileService
import com.example.kmatool.data.service.ScheduleService
import com.example.kmatool.data.service.ScoreService
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
}