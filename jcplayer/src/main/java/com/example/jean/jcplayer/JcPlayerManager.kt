package com.example.jean.jcplayer

import android.annotation.SuppressLint
import android.content.Context
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.JcPlayerService
import com.example.jean.jcplayer.service.JcServiceConnection
import com.example.jean.jcplayer.service.JcpServiceListener
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
@Inject constructor(
        private val serviceConnection: JcServiceConnection
) {
    private var jcPlayerService: JcPlayerService? = null

    private var invalidPathListener: OnInvalidPathListener? = null

    private var viewListener: JcpViewListener? = null

    private val jcNotificationPlayer: JcNotificationService? = null

    private var currentJcAudio: JcAudio? = null

    private var currentPositionList: Int = 0

    private var serviceBound = false

    var playlist: ArrayList<JcAudio> = ArrayList()

    var listener: JcpServiceListener? = null

    val currentAudio: JcAudio?
        get() = jcPlayerService?.currentAudio

    var isPlaying: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    private val position = 1

    init {
//        this.jcNotificationPlayer = JcNotificationService()
        initService()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: JcPlayerManager? = null

        @JvmStatic
        fun getInstance(
                context: Context,
                playlist: ArrayList<JcAudio>? = null,
                listener: JcpServiceListener? = null
        ): JcPlayerManager =
                INSTANCE ?: JcPlayerManager(
                        // TODO: FIXME URGENT!!!!!!
                        JcServiceConnection(context)
                ).also {
                    it.playlist = playlist ?: ArrayList()
                    it.listener = listener
                }
    }

    /**
     * Connects with audio service.
     */
    private fun initService(
            onConnected: (() -> Unit)? = null,
            onDisconnected: (() -> Unit)? = null
    ) {
        serviceConnection.connect(
                playlist = playlist,
                onConnected = { binder ->
                    jcPlayerService = binder?.service

                    jcPlayerService?.let { service ->
                        listener?.let { service.registerServicePlayerListener(it) }
                        viewListener?.let { service.registerStatusListener(it) }
                        invalidPathListener?.let { service.registerInvalidPathListener(it) }
                        serviceBound = true
                        onConnected?.invoke()
                    } ?: onDisconnected?.invoke()
                },
                onDisconnected = {
                    serviceBound = false
                    isPlaying = false
                    isPaused = true
                    onDisconnected?.invoke()
                }
        )
    }

    /**
     * Registers a new [JcNotificationService] notification listener.
     * @param notificationListener The listener.
     */
    fun registerNotificationListener(notificationListener: JcNotificationService) {
        this.listener = notificationListener

        jcNotificationPlayer.let {
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
     * Registers a new [JcpServiceListener] service listener.
     * @param jcPlayerServiceListener The listener.
     */
    fun registerServiceListener(jcPlayerServiceListener: JcpServiceListener) {
        this.listener = jcPlayerServiceListener
        jcPlayerService?.registerServicePlayerListener(jcPlayerServiceListener)
    }

    /**
     * Registers a  new [JcpViewListener] listener.
     * @param viewListener The listener.
     */
    fun registerStatusListener(viewListener: JcpViewListener) {
        this.viewListener = viewListener
        jcPlayerService?.registerStatusListener(viewListener)
    }

    /**
     * Plays the given [JcAudio].
     * @param jcAudio The audio to be played.
     */
    @Throws(AudioListNullPointerException::class)
    fun playAudio(jcAudio: JcAudio) {
        if (playlist.isEmpty()) {
            throw AudioListNullPointerException()
        } else {
            currentJcAudio = jcAudio

            jcPlayerService?.let {
                jcPlayerService?.play(currentJcAudio!!)
                updatePositionAudioList()
                isPlaying = true
                isPaused = false
            } ?: let {
                initService(onConnected = { playAudio(currentJcAudio!!) })
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
    }

    /**
     * Pauses the current audio.
     */
    fun pauseAudio() {
        jcPlayerService?.let { service ->
            currentAudio?.let {
                service.pause(it)
                isPaused = true
                isPlaying = false
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
                isPlaying = true
                isPaused = false
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
        INSTANCE = null
    }
}
