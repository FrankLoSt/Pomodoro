package com.example.pomodoro.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.datastore.preferences.core.Preferences
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import javax.inject.Singleton


//This is where you “register” things (DataStore, repositories, API clients, etc.) so Hilt can inject them.
/*
* @Module: tells Hilt this is a module that will provide dependencies.
* @InstallIn: tells Hilt that this module is created once and reused for the lifetime of the application, can be used everywhere
* @Provides
* */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        } //this returns a DataStore<Preferences>
    }
}
