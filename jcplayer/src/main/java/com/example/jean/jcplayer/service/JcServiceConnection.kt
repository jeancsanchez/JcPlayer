package com.example.jean.jcplayer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.notification.JcNotificationPlayer
import java.io.Serializable

/**
 * This class is an [ServiceConnection] for the [JcPlayerService] class.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 15/02/18.
 * Jesus loves you.
 */
class JcServiceConnection(private val context: Context) : ServiceConnection {

    private var serviceBound = false
    private var onConnected: ((JcPlayerService.JcPlayerServiceBinder?) -> Unit)? = null
    private var onDisconnected: ((Unit) -> Unit)? = null

    override fun onServiceDisconnected(name: ComponentName?) {
        serviceBound = false
        onDisconnected?.invoke(Unit)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serviceBound = true
        onConnected?.invoke(service as JcPlayerService.JcPlayerServiceBinder?)
    }

    /**
     * Connects with the [JcPlayerService].
     */
    fun connect(
            playlist: ArrayList<JcAudio>? = null,
            currentAudio: JcAudio? = null,
            onConnected: ((JcPlayerService.JcPlayerServiceBinder?) -> Unit)? = null,
            onDisconnected: ((Unit) -> Unit)? = null
    ) {
        this.onConnected = onConnected
        this.onDisconnected = onDisconnected

        if (serviceBound.not()) {
            val intent = Intent(context.applicationContext, JcPlayerService::class.java)
            intent.putExtra(JcNotificationPlayer.PLAYLIST, playlist as Serializable?)
            intent.putExtra(JcNotificationPlayer.CURRENT_AUDIO, currentAudio)
            context.applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Disconnects from the [JcPlayerService].
     */
    fun disconnect() {
        if (serviceBound)
            try {
                context.unbindService(this)
            } catch (e: IllegalArgumentException) {
                //TODO: Add readable exception here
            }
    }
}
