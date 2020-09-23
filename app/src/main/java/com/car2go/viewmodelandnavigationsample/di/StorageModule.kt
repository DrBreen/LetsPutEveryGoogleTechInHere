package com.car2go.viewmodelandnavigationsample.di

import android.content.Context
import android.content.SharedPreferences
import com.car2go.viewmodelandnavigationsample.di.qualifiers.SettingsPreferences
import com.car2go.viewmodelandnavigationsample.di.qualifiers.StoragePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
object StorageModule {

    @Provides
    @StoragePreferences
    fun providesStoragePreferences(
        @ApplicationContext
        context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("storage", Context.MODE_PRIVATE)
    }

    @Provides
    @SettingsPreferences
    fun providesSettingsPreferences(
        @ApplicationContext
        context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

}