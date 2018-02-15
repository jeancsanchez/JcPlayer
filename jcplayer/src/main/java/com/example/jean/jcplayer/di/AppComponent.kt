package com.example.jean.jcplayer.di

import com.example.jean.jcplayer.view.JcPlayerView
import dagger.Component
import javax.inject.Singleton


/**
 * This class represents a Dagger application component.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(playerView: JcPlayerView)
}
