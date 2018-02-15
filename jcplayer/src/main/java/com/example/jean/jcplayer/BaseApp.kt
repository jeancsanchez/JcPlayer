package com.example.jean.jcplayer

import com.example.jean.jcplayer.di.DaggerAppComponent
import com.example.jean.jcplayer.service.JcPlayerService
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


/**
 * This class is the player manager. Handles all interactions and communicates with [JcPlayerService].
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */

class BaseApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>  {
        return DaggerAppComponent.builder().create(this)
    }
}