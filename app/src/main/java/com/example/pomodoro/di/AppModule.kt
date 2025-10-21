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
import androidx.room.Room
import com.example.pomodoro.data.AppDatabase

import com.example.pomodoro.data.AppDatabase.Companion.MIGRATION_4_5

import com.example.pomodoro.data.MonsterFightingDao
import com.example.pomodoro.data.MonsterItemsDao
import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.example.pomodoro.ui.pickmonster.InitSetUpStateHolder
import com.example.pomodoro.ui.pickmonster.MonsterDataController
import com.example.pomodoro.ui.pickmonster.MonsterDataControllerImpl
import dagger.Binds

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


//This is where you “register” things (DataStore, repositories, API clients, etc.) so Hilt can inject them.
/*
* @Module: tells Hilt this is a module that will provide dependencies.
* @InstallIn: tells Hilt that this module is created once and reused for the lifetime of the application, can be used everywhere
* @Provides
* */
@Module //this tells Hilt that this is a module, where I tell it how to create dependencies, like “This class contains recipes for how to build things.”
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides //this tells Hilt that this function provides a dependency
    @Singleton //this tells Hilt that there should only be one instance of this dependency
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        } //this returns a DataStore<Preferences>
    }
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "monster_fighting_db"
        ).addMigrations(MIGRATION_4_5)
            .build()
    }

    @Provides
    fun provideMonsterFightingDao(database: AppDatabase): MonsterFightingDao {
        return database.monsterFightingDao()
    }

    @Provides
    fun provideMonsterItemsDao(database: AppDatabase): MonsterItemsDao {
        return database.monsterItemsDao()
    }

    @Provides
    @Singleton
    fun provideInitSetUpStateHolder(): InitSetUpStateHolder {
        return InitSetUpStateHolder()
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindMonsterFightingRepository(
        impl: MonsterDataControllerImpl
    ): MonsterDataController


}

