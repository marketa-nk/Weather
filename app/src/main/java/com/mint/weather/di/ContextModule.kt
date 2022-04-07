package com.mint.weather.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @AppScope
    @Provides
    fun context(): Context {
        return context.applicationContext
    }
}
