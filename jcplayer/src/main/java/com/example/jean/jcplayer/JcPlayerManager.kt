package com.example.jean.jcplayer

import android.content.Context
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.general.errors.JcpServiceDisconnectedError
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.JcPlayerManagerListener
import com.example.jean.jcplayer.service.JcPlayerService
import com.example.jean.jcplayer.service.JcServiceConnection
import com.example.jean.jcplayer.service.notification.JcNotificationService
import com.example.jean.jcplayer.view.JcpViewListener
import javax.inject.Inject

/**
 * This class is the player manager. Handles all interactions and communicates with [JcPlayerService].
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 12/07/16.
 * Jesus loves you.
 */
class JcPlayerManager
@Inject constructor(private val serviceConnection: JcServiceConnection) {

    private var invalidPathListener: OnInvalidPathListener? = null

    private var viewListener: JcpViewListener? = null

    private val jcNotificationPlayer: JcNotificationService? = null

    private var currentJcAudio: JcAudio? = null

    private var currentPositionList: Int = 0

    var playlist: ArrayList<JcAudio> = ArrayList()

    private var serviceBound = false

    private var jcPlayerService: JcPlayerService? = null

    private val managerListeners: ArrayList<JcPlayerManagerListener> = ArrayList()

    var jcPlayerManagerListener: JcPlayerManagerListener? = null
        set(value) {
            value?.let { managerListeners.add(it) }
        }

    val currentAudio: JcAudio?
        get() = jcPlayerService?.currentAudio

    var isPlaying: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    private val position = 1

    init {
        initService()
    }

    companion object {

        @Volatile
        private var INSTANCE: JcPlayerManager? = null

        @JvmStatic
        fun getInstance(
                context: Context,
                playlist: ArrayList<JcAudio>? = null,
                listener: JcPlayerManagerListener? = null
        ): JcPlayerManager = INSTANCE ?: let {
            INSTANCE = JcPlayerManager(JcServiceConnection(context)).also {
                it.playlist = playlist ?: ArrayList()
                it.jcPlayerManagerListener = listener
            }
            INSTANCE!!
        }
    }

    /**
     * Notifies errors for the service listeners
     */
    private fun notifyError(throwable: Throwable) {
        for (listener in managerListeners) {
            listener.onJcpError(throwable)
        }
    }

    /**
     * Notifies on playing for the service listeners
     * @param status The current player status.
     */
    private fun notifyOnPlaying(status: JcStatus) {
        isPlaying = true
        isPaused = false

        for (listener in managerListeners) {
            listener.onPlaying(status)
        }
    }

    /**
     * Notifies on paused for the service listeners
     */
    private fun notifyOnPaused(status: JcStatus) {
        isPlaying = false
        isPaused = true

        for (listener in managerListeners) {
            listener.onPaused(status)
        }
    }

    /**
     * Notifies on completed audio for the service listeners
     */
    private fun notifyOnCompleted() {
        for (listener in managerListeners) {
            listener.onCompletedAudio()
        }
    }

    /**
     * Notifies on continue for the service listeners
     */
    private fun notifyOnContinue(status: JcStatus) {
        isPlaying = true
        isPaused = false

        for (listener in managerListeners) {
            listener.onContinueAudio(status)
        }
    }

    /**
     * Notifies on prepared aduio for the service listeners
     */
    private fun notifyOnPrepared(status: JcStatus) {
        for (listener in managerListeners) {
            listener.onPreparedAudio(status)
        }
    }

    /**
     * Notifies on time changed for the service listeners
     */
    private fun notifyOnTimeChanged(status: JcStatus) {
        for (listener in managerListeners) {
            listener.onTimeChanged(status)
        }
    }

    /**
     * Connects with audio service.
     */
    private fun initService(connectionListener: (() -> Unit)? = null) =
            serviceConnection.connect(
                    playlist = playlist,
                    onConnected = { binder ->
                        jcPlayerService = binder?.service.also {
                            serviceBound = true
                            connectionListener?.invoke()
                        } ?: throw JcpServiceDisconnectedError
                    },
                    onDisconnected = {
                        serviceBound = false
                        throw  JcpServiceDisconnectedError
                    }
            )

    /**
     * Plays the given [JcAudio].
     * @param jcAudio The audio to be played.
     */
    @Throws(AudioListNullPointerException::class, JcpServiceDisconnectedError::class)
    fun playAudio(jcAudio: JcAudio) {
        if (playlist.isEmpty()) {
            notifyError(AudioListNullPointerException())
        } else {
            jcPlayerService?.let { service ->
                currentJcAudio = jcAudio
                updatePositionAudioList()

                notifyOnPlaying(service.play(currentJcAudio!!))
                service.onPreparedListener = { notifyOnPrepared(it) }
                service.onTimeChangedListener = { notifyOnTimeChanged(it) }
                service.onCompletedListener = { notifyOnCompleted() }

            } ?: let {
                initService { playAudio(jcAudio) }
            }
        }
    }


    /**
     * Goes to next audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun nextAudio() {
        if (playlist.isEmpty()) {
            throw AudioListNullPointerException()
        } else {
            currentAudio?.let {
                try {
                    val nextJcAudio = playlist[currentPositionList + position]
                    this.currentJcAudio = nextJcAudio

                    jcPlayerService?.let { service ->
                        service.stop()
                        notifyOnPlaying(service.play(nextJcAudio))
                    }

                } catch (e: IndexOutOfBoundsException) {
                    playAudio(playlist[0])
                    e.printStackTrace()
                }
            }

            updatePositionAudioList()
        }
    }

    /**
     * Goes to previous audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun previousAudio() {
        if (playlist.isEmpty()) {
            throw AudioListNullPointerException()
        } else {
            currentJcAudio?.let {
                try {
                    val previousJcAudio = playlist[currentPositionList - position]
                    this.currentJcAudio = previousJcAudio

                    jcPlayerService?.let { service ->
                        service.stop()
                        notifyOnPlaying(service.play(previousJcAudio))
                    }

                } catch (e: IndexOutOfBoundsException) {
                    playAudio(playlist[0])
                    e.printStackTrace()
                }
            }

            updatePositionAudioList()
        }
    }

    /**
     * Pauses the current audio.
     */
    fun pauseAudio() {
        jcPlayerService?.let { service ->
            currentAudio?.let {
                notifyOnPaused(service.pause(it))
            }
        }
    }

    /**
     * Continues the stopped audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun continueAudio() {
        if (playlist.isEmpty()) {
            throw AudioListNullPointerException()
        } else {
            currentJcAudio?.let {
                currentJcAudio = playlist[0]
                playAudio(it)
            }
        }
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
        for (i in playlist.indices) {
            currentJcAudio?.let { currAudio ->
                if (playlist[i].id == currAudio.id) {
                    this.currentPositionList = i
                }
            }
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

        serviceConnection.disconnect()
        jcNotificationPlayer?.destroyNotificationIfExists()
        managerListeners.clear()
        INSTANCE = null
    }
}
