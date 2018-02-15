package com.example.jean.jcplayer.di

import android.content.Context
import com.example.jean.jcplayer.BaseApp
import com.example.jean.jcplayer.JcPlayerManager
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
class AppModule {

    @Provides
    @Singleton
    fun providesContext(baseApp: BaseApp): Context = baseApp.applicationContext

    @Provides
    @Singleton
    fun providesJcPlayerManager(): JcPlayerManager {
        return JcPlayerManager()
    }

}