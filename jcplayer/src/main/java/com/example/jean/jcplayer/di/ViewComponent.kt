package com.example.jean.jcplayer.di

import android.view.View
import com.example.jean.jcplayer.BaseApp
import com.example.jean.jcplayer.view.JcPlayerView
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * This class represents a Dagger application component.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class
])
interface ViewComponent : AndroidInjector<JcPlayerView> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<JcPlayerView>()
}