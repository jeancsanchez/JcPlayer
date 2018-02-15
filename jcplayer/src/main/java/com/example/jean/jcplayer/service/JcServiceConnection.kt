package com.example.jean.jcplayer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.notification.JcNotificationService
import io.reactivex.Observable
import java.io.Serializable

/**
 * This class is an [ServiceConnection] for the [JcPlayerService] class.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */
class JcServiceConnection
constructor(
        val context: Context
) : ServiceConnection {

    override fun onServiceDisconnected(name: ComponentName?) {

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

    }

    fun connect(
            playlist: ArrayList<JcAudio>? = null,
            currentAudio: JcAudio? = null
    ): Observable<JcPlayerService.JcPlayerServiceBinder?> {
        if (serviceBound.not()) {
            val intent = Intent(context.applicationContext, JcPlayerService::class.java)
            intent.putExtra(JcNotificationService.PLAYLIST, playlist as Serializable?)
            intent.putExtra(JcNotificationService.CURRENT_AUDIO, currentAudio)
            context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

}
