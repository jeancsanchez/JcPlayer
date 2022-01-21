package com.example.jean.jcplayer

import android.content.Context
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.general.errors.JcpServiceDisconnectedError
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.JcPlayerService
import com.example.jean.jcplayer.service.JcPlayerServiceListener
import com.example.jean.jcplayer.service.JcServiceConnection
import com.example.jean.jcplayer.service.notification.JcNotificationPlayer
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * This class is the player manager. Handles all interactions and communicates with [JcPlayerService].
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 12/07/16.
 * Jesus loves you.
 */
class JcPlayerManager
private constructor(private val serviceConnection: JcServiceConnection) : JcPlayerServiceListener {

    lateinit var context: Context
    private var jcNotificationPlayer: JcNotificationPlayer? = null
    private var jcPlayerService: JcPlayerService? = null
    private var serviceBound = false
    var playlist: ArrayList<JcAudio> = ArrayList()
    private var currentPositionList: Int = 0
    private val managerListeners: CopyOnWriteArrayList<JcPlayerManagerListener> = CopyOnWriteArrayList()

    var jcPlayerManagerListener: JcPlayerManagerListener? = null
        set(value) {
            value?.let {
                if (managerListeners.contains(it).not()) {
                    managerListeners.add(it)
                }
            }
            field = value
        }

    val currentAudio: JcAudio?
        get() = jcPlayerService?.currentAudio

    var currentStatus: JcStatus? = null
        private set

    var onShuffleMode: Boolean = false

    var repeatPlaylist: Boolean = false
        private set

    var repeatCurrAudio: Boolean = false
        private set

    private var repeatCount = 0

    init {
        initService()
    }

    companion object {

        @Volatile
        private var INSTANCE: WeakReference<JcPlayerManager>? = null

        @JvmStatic
        fun getInstance(
                context: Context,
                playlist: ArrayList<JcAudio>? = null,
                listener: JcPlayerManagerListener? = null
        ): WeakReference<JcPlayerManager> = INSTANCE ?: let {
            INSTANCE = WeakReference(
                    JcPlayerManager(JcServiceConnection(context)).also {
                        it.context = context
                        it.playlist = playlist ?: ArrayList()
                        it.jcPlayerManagerListener = listener
                    }
            )
            INSTANCE!!
        }
    }

    /**
     * Connects with audio service.
     */
    private fun initService(connectionListener: ((service: JcPlayerService?) -> Unit)? = null) =
            serviceConnection.connect(
                    playlist = playlist,
                    onConnected = { binder ->
                        jcPlayerService = binder?.service.also { service ->
                            serviceBound = true
                            connectionListener?.invoke(service)
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
    @Throws(AudioListNullPointerException::class)
    fun playAudio(jcAudio: JcAudio) {
        if (playlist.isEmpty()) {
            notifyError(AudioListNullPointerException())
        } else {
            jcPlayerService?.let { service ->
                service.serviceListener = this
                service.play(jcAudio)
            } ?: initService { service ->
                jcPlayerService = service
                playAudio(jcAudio)
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
            jcPlayerService?.let { service ->
                if (repeatCurrAudio) {
                    currentAudio?.let {
                        service.seekTo(0)
                        service.onPrepared(service.getMediaPlayer()!!)
                    }
                } else {
                    service.stop()
                    getNextAudio()?.let { service.play(it) } ?: service.finalize()
                }
            }
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
            jcPlayerService?.let { service ->
                if (repeatCurrAudio) {
                    currentAudio?.let {
                        service.seekTo(0)
                        service.onPrepared(service.getMediaPlayer()!!)
                    }
                } else {
                    service.stop()
                    getPreviousAudio().let { service.play(it) }
                }
            }
        }
    }

    /**
     * Pauses the current audio.
     */
    fun pauseAudio() {
        jcPlayerService?.let { service -> currentAudio?.let { service.pause(it) } }
    }

    /**
     * Continues the stopped audio.
     */
    @Throws(AudioListNullPointerException::class)
    fun continueAudio() {
        if (playlist.isEmpty()) {
            throw AudioListNullPointerException()
        } else {
            val audio = jcPlayerService?.currentAudio ?: let { playlist.first() }
            playAudio(audio)
        }
    }

    /**
     * Creates a new notification with icon resource.
     * @param iconResource The icon resource path.
     */
    fun createNewNotification(iconResource: Int) {
        jcNotificationPlayer
                ?.createNotificationPlayer(currentAudio?.title, iconResource)
                ?: let {
                    jcNotificationPlayer = JcNotificationPlayer
                            .getInstance(context)
                            .get()
                            .also { notification ->
                                jcPlayerManagerListener = notification
                            }

                    createNewNotification(iconResource)
                }
    }

    /**
     * Updates the current notification
     */
    fun updateNotification() {
        jcNotificationPlayer
                ?.updateNotification()
                ?: let {
                    jcNotificationPlayer = JcNotificationPlayer
                            .getInstance(context)
                            .get()
                            .also { jcPlayerManagerListener = it }

                    updateNotification()
                }
    }

    /**
     * Jumps audio to the specific time.
     */
    fun seekTo(time: Int) {
        jcPlayerService?.seekTo(time)
    }


    private fun getNextAudio(): JcAudio? {
        return if (onShuffleMode) {
            playlist[Random().nextInt(playlist.size)]
        } else {
            try {
                playlist[currentPositionList.inc()]
            } catch (e: IndexOutOfBoundsException) {
                if (repeatPlaylist) {
                    return playlist.first()
                }

                null
            }
        }
    }

    private fun getPreviousAudio(): JcAudio {
        return if (onShuffleMode) {
            playlist[Random().nextInt(playlist.size)]
        } else {
            try {
                playlist[currentPositionList.dec()]

            } catch (e: IndexOutOfBoundsException) {
                return playlist.first()
            }
        }
    }


    override fun onPreparedListener(status: JcStatus) {
        currentStatus = status
        updatePositionAudioList()

        for (listener in managerListeners) {
            listener.onPreparedAudio(status)
        }
    }

    override fun onTimeChangedListener(status: JcStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onTimeChanged(status)

            if (status.currentPosition in 1..2 /* Not to call this every second */) {
                listener.onPlaying(status)
            }
        }
    }

    override fun onContinueListener(status: JcStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onContinueAudio(status)
        }
    }

    override fun onCompletedListener() {
        for (listener in managerListeners) {
            listener.onCompletedAudio()
        }
    }

    override fun onPausedListener(status: JcStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onPaused(status)
        }
    }

    override fun onStoppedListener(status: JcStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onStopped(status)
        }
    }

    override fun onError(exception: Exception) {
        notifyError(exception)
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
     * Handles the repeat mode.
     */
    fun activeRepeat() {
        if (repeatCount == 0) {
            repeatPlaylist = true
            repeatCurrAudio = false
            repeatCount++
            return
        }

        if (repeatCount == 1) {
            repeatCurrAudio = true
            repeatPlaylist = false
            repeatCount++
            return
        }

        if (repeatCount == 2) {
            repeatCurrAudio = false
            repeatPlaylist = false
            repeatCount = 0
        }
    }

    /**
     * Updates the current position of the audio list.
     */
    private fun updatePositionAudioList() {
        playlist.indices
                .singleOrNull { playlist[it] == currentAudio }
                ?.let { this.currentPositionList = it }
                ?: let { this.currentPositionList = 0 }
    }

    fun isPlaying(): Boolean {
        return jcPlayerService?.isPlaying ?: false
    }

    fun isPaused(): Boolean {
        return jcPlayerService?.isPaused ?: true
    }

    /**
     * Kills the JcPlayer, including Notification and service.
     */
    fun kill() {
        jcPlayerService?.let {
            it.stop()
            it.stopSelf()
            it.stopForeground(true)
        }

        serviceConnection.disconnect()
        jcNotificationPlayer?.destroyNotificationIfExists()
        managerListeners.clear()
        INSTANCE = null
    }
}
