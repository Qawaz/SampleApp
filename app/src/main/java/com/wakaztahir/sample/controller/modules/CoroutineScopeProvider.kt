package com.wakaztahir.mindnode.controller.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutineScopeProvider {
    @Provides
    @Singleton
    @ApplicationScope
    fun provideCoroutineScope() : CoroutineScope {
        return CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope