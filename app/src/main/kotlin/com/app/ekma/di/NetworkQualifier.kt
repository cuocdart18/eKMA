package com.app.ekma.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppSite

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScoreSite

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AgoraSite