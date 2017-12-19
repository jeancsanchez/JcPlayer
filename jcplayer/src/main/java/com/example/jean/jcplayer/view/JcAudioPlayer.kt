package com.example.jean.jcplayer.view

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.jean.jcplayer.JcAudio
import com.example.jean.jcplayer.JcNotificationPlayerService
import com.example.jean.jcplayer.general.OnInvalidPathListener

import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.service.JcPlayerService
import com.example.jean.jcplayer.service.JcpServiceListener

import java.io.Serializable

/**
 * This class is the JcAudio manager. Handles all interactions and communicates with [JcPlayerService].
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 12/07/16.
 * Jesus loves you.
 */
class JcAudioPlayer(
        private val context: Context,
        val playlist: ArrayList<JcAudio>?,
        private var listener: JcpServiceListener?
) {
    private var jcPlayerService: JcPlayerService? = null
    private var invalidPathListener: OnInvalidPathListener? = null
    private var statusListener: JcpStatusListener? = null
    private val jcNotificationPlayer: JcNotificationPlayerService?
    private var currentJcAudio: JcAudio? = null
    private var currentPositionList: Int = 0
    private var bound = false
    val currentAudio: JcAudio?
        get() = jcPlayerService?.currentAudio
    var isPlaying: Boolean = false
        private set
    var isPaused: Boolean = false
        private set
    private val position = 1
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as JcPlayerService.JcPlayerServiceBinder
            jcPlayerService = binder.service

            listener?.let { jcPlayerService?.registerServicePlayerListener(listener) }
            statusListener?.let { jcPlayerService?.registerStatusListener(statusListener) }
            invalidPathListener?.let {
                jcPlayerService?.registerInvalidPathListener(invalidPathListener)
            }
            bound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bound = false
            isPlaying = false
            isPaused = true
        }
    }

    var instance: JcAudioPlayer? = this
    init {
        instance = this // Just for be sure ;p
        this.jcNotificationPlayer = JcNotificationPlayerService(context)
        initService()
    }

    /**
     * Registers a new [JcPlayerViewServiceListener] notification listener.
     * @param notificationListener The listener.
     */
    fun registerNotificationListener(notificationListener: JcPlayerViewServiceListener) {
        this.listener = notificationListener

        jcNotificationPlayer?.let {
            jcPlayerService?.registerNotificationListener(notificationListener)
        }
    }

    /**
     * Registers a new [OnInvalidPathListener] listener.
     * @param registerInvalidPathListener The listener.
     */
    fun registerInvalidPathListener(registerInvalidPathListener: OnInvalidPathListener) {
        this.invalidPathListener = registerInvalidPathListener
        jcPlayerService?.registerInvalidPathListener(registerInvalidPathListener)
    }

    /**
     * Registers a new [JcPlayerViewServiceListener] service listener.
     * @param jcPlayerServiceListener The listener.
     */
    fun registerServiceListener(jcPlayerServiceListener: JcPlayerViewServiceListener) {
        this.listener = jcPlayerServiceListener
        jcPlayerService?.registerServicePlayerListener(jcPlayerServiceListener)
    }

    /**
     * Registers a  new [JcPlayerViewStatusListener] listener.
     * @param statusListener The listener.
     */
    fun registerStatusListener(statusListener: JcPlayerViewStatusListener) {
        this.statusListener = statusListener
        jcPlayerService?.registerStatusListener(statusListener)
    }

    /**
     * Plays the given [JcAudio].
     * @param jcAudio The audio to be played.
     */
    @Throws(AudioListNullPointerException::class)
    fun playAudio(jcAudio: JcAudio) {
        playlist?.let {
            if (it.isEmpty()) {
                throw AudioListNullPointerException()
            } else {
                currentJcAudio = jcAudio
                jcPlayerService?.play(currentJcAudio)
                updatePositionAudioList()
                isPlaying = true
                isPaused = false
            }
        } ?: throw AudioListNullPointerException()
    }

    /**
     * Initializes the JcAudio Service.
     */
    private fun initService() {
        if (bound.not()) {
            startJcPlayerService()
        } else {
            bound = true
        }
    }

    /**
     * Goes to next audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun nextAudio() {
        playlist?.let {
            if (it.isEmpty()) {
                throw AudioListNullPointerException()
            } else {
                currentAudio?.let {
                    try {
                        val nextJcAudio = playlist[currentPositionList + position]
                        this.currentJcAudio = nextJcAudio
                        jcPlayerService?.stop()
                        jcPlayerService?.play(nextJcAudio)

                    } catch (e: IndexOutOfBoundsException) {
                        playAudio(playlist[0])
                        e.printStackTrace()
                    }
                }

                updatePositionAudioList()
                isPlaying = true
                isPaused = false
            }
        } ?: throw AudioListNullPointerException()
    }

    /**
     * Goes to previous audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun previousAudio() {
        playlist?.let {
            if (it.isEmpty()) {
                throw AudioListNullPointerException()
            } else {
                currentJcAudio?.let {
                    try {
                        val previousJcAudio = playlist[currentPositionList - position]
                        this.currentJcAudio = previousJcAudio
                        jcPlayerService?.stop()
                        jcPlayerService?.play(previousJcAudio)

                    } catch (e: IndexOutOfBoundsException) {
                        playAudio(playlist[0])
                        e.printStackTrace()
                    }
                }

                updatePositionAudioList()
                isPlaying = true
                isPaused = false
            }
        } ?: throw AudioListNullPointerException()
    }

    /**
     * Pauses the current audio.
     */
    fun pauseAudio() {
        jcPlayerService?.let {
            it.pause(currentJcAudio)
            isPaused = true
            isPlaying = false
        }
    }

    /**
     * Continues the stopped audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun continueAudio() {
        playlist?.let {
            if (it.isEmpty()) {
                throw AudioListNullPointerException()
            } else {
                currentJcAudio?.let {
                    currentJcAudio = playlist[0]
                    playAudio(it)
                    isPlaying = true
                    isPaused = false
                }
            }
        } ?: throw AudioListNullPointerException()
    }

    /**
     * Creates a new notification with icon resource.
     * @param iconResource The icon resource path.
     */
    fun createNewNotification(iconResource: Int) {
        currentJcAudio?.let {
            jcNotificationPlayer?.createNotificationPlayer(it.title, iconResource)
        }
    }

    /**
     * Updates the current notification
     */
    fun updateNotification() {
        jcNotificationPlayer?.updateNotification()
    }

    /**
     * Jumps audio to the specific time.
     */
    fun seekTo(time: Int) {
        jcPlayerService?.seekTo(time)
    }

    /**
     * Updates the current position of the audio list.
     */
    private fun updatePositionAudioList() {
        playlist?.let {
            for (i in it.indices) {
                currentJcAudio?.let { currAudio ->
                    if (it[i].id == currAudio.id) {
                        this.currentPositionList = i
                    }
                }
            }
        } ?: throw AudioListNullPointerException()
    }

    /**
     * Starts the JcPlayer service. This is a synchronized operation.
     */
    @Synchronized private fun startJcPlayerService() {
        if (bound.not()) {
            val intent = Intent(context.applicationContext, JcPlayerService::class.java)
            intent.putExtra(JcNotificationPlayerService.PLAYLIST, playlist as Serializable?)
            intent.putExtra(JcNotificationPlayerService.CURRENT_AUDIO, currentJcAudio)
            context.bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    /**
     * Kills the JcPlayer, including Notification and service.
     */
    fun kill() {
        jcPlayerService?.let {
            it.stop()
            it.destroy()
        }

        if (bound)
            try {
                context.unbindService(connection)
            } catch (e: IllegalArgumentException) {
                //TODO: Add readable exception here
            }

        jcNotificationPlayer?.destroyNotificationIfExists()
        instance = null
    }
}
