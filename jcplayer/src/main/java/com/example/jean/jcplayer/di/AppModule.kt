package com.example.jean.jcplayer.di

import android.content.Context
import com.example.jean.jcplayer.JcPlayerManager
import com.example.jean.jcplayer.service.JcServiceConnection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This class represents an Dagger application module.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */

@Module
class AppModule
constructor(val context: Context) {


    @Provides
    @Singleton
    fun providesContext(): Context = context

    @Provides
    @Singleton
    fun providesJcPlayerManager(jcServiceConnection: JcServiceConnection): JcPlayerManager {
        return JcPlayerManager(jcServiceConnection)
    }

    @Provides
    @Singleton
    fun providesJcServiceConnection(context: Context): JcServiceConnection {
        return JcServiceConnection(context)
    }
}