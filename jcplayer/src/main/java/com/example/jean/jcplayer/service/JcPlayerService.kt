package com.example.jean.jcplayer.service

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.Origin
import com.example.jean.jcplayer.general.errors.*
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcpViewListener
import java.io.File
import java.io.IOException
import java.util.*

/**
 * This class is an Android [Service] that handles all player changes on background.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 02/07/16.
 * Jesus loves you.
 */
class JcPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {


    private val binder = JcPlayerServiceBinder()

    private var mediaPlayer: MediaPlayer? = null

    private var isPlaying: Boolean = false

    private var duration: Int = 0

    private var currentTime: Int = 0

    var currentAudio: JcAudio? = null
        private set

    private val jcStatus = JcStatus()

    private var jcPlayerServiceListeners: MutableList<JcpServiceListener>? = null

    private var invalidPathListeners: MutableList<OnInvalidPathListener>? = null

    private var jcPlayerStatusListeners: MutableList<JcpViewListener>? = null

    private var notificationListener: JcpServiceListener? = null

    private var assetFileDescriptor: AssetFileDescriptor? = null // For Asset and Raw file.

    private var tempJcAudio: JcAudio? = null

    inner class JcPlayerServiceBinder : Binder() {
        val service: JcPlayerService
            get() = this@JcPlayerService
    }

    fun registerNotificationListener(notificationListener: JcpServiceListener) {
        this.notificationListener = notificationListener
    }

    fun registerServicePlayerListener(jcPlayerServiceListener: JcpServiceListener) {
        jcPlayerServiceListeners?.let {
            if (it.contains(jcPlayerServiceListener).not()) {
                it.add(jcPlayerServiceListener)
            }
        } ?: apply { jcPlayerServiceListeners = ArrayList() }
    }

    fun registerInvalidPathListener(invalidPathListener: OnInvalidPathListener) {
        invalidPathListeners?.let {
            if (it.contains(invalidPathListener).not()) {
                it.add(invalidPathListener)
            }
        } ?: apply { invalidPathListeners = ArrayList() }
    }

    fun registerStatusListener(statusListener: JcpViewListener) {
        jcPlayerStatusListeners?.let {
            if (it.contains(statusListener).not()) {
                it.add(statusListener)
            }
        } ?: apply { jcPlayerStatusListeners = ArrayList() }
    }

    override fun onBind(intent: Intent): IBinder? = binder

    fun pause(jcAudio: JcAudio) {
        mediaPlayer?.let {
            it.pause()
            duration = it.duration
            currentTime = it.currentPosition
            isPlaying = false
        }

        jcPlayerServiceListeners?.let {
            it.forEach { listener ->
                listener.onPaused()
            }
        }.also { notificationListener?.onPaused() }

        jcPlayerStatusListeners?.let {
            it.forEach { listener ->
                jcStatus.jcAudio = jcAudio
                jcStatus.duration = duration.toLong()
                jcStatus.currentPosition = currentTime.toLong()
                jcStatus.playState = JcStatus.PlayState.PAUSE
                listener.onPausedStatus(jcStatus)
            }
        }
    }

    fun destroy() {
        stop()
        stopSelf()
    }

    fun stop() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }

        isPlaying = false
    }

    fun play(jcAudio: JcAudio) {
        tempJcAudio = currentAudio
        currentAudio = jcAudio

        if (isAudioFileValid(jcAudio.path, jcAudio.origin)) {
            try {
                mediaPlayer?.let {
                    if (isPlaying) {
                        stop()
                        play(jcAudio)
                    } else {
                        if (tempJcAudio !== jcAudio) {
                            stop()
                            play(jcAudio)
                        } else {
                            it.start()
                            isPlaying = true

                            jcPlayerServiceListeners?.let { list ->
                                list.forEach { listener ->
                                    listener.onContinueAudio()
                                }
                            }

                            jcPlayerStatusListeners?.let { list ->
                                list.forEach { listener ->
                                    jcStatus.jcAudio = jcAudio
                                    jcStatus.playState = JcStatus.PlayState.PLAY
                                    jcStatus.duration = it.duration.toLong()
                                    jcStatus.currentPosition = it.currentPosition.toLong()
                                    listener.onContinueAudioStatus(jcStatus)
                                }
                            }
                        }
                    }
                } ?: let {
                    mediaPlayer = MediaPlayer().also {
                        when {
                            jcAudio.origin == Origin.URL -> it.setDataSource(jcAudio.path)
                            jcAudio.origin == Origin.RAW -> assetFileDescriptor =
                                    applicationContext.resources.openRawResourceFd(
                                            Integer.parseInt(jcAudio.path)
                                    ).also { descriptor ->
                                        it.setDataSource(
                                                descriptor.fileDescriptor,
                                                descriptor.startOffset,
                                                descriptor.length
                                        )
                                        descriptor.close()
                                        assetFileDescriptor = null
                                    }


                            jcAudio.origin == Origin.ASSETS -> {
                                assetFileDescriptor = applicationContext.assets.openFd(jcAudio.path)
                                        .also { descriptor ->
                                            it.setDataSource(
                                                    descriptor.fileDescriptor,
                                                    descriptor.startOffset,
                                                    descriptor.length
                                            )

                                            descriptor.close()
                                            assetFileDescriptor = null
                                        }
                            }

                            jcAudio.origin == Origin.FILE_PATH ->
                                it.setDataSource(applicationContext, Uri.parse(jcAudio.path))
                        }

                        it.prepareAsync()
                        it.setOnPreparedListener(this)
                        it.setOnBufferingUpdateListener(this)
                        it.setOnCompletionListener(this)
                        it.setOnErrorListener(this)

                        //} else if (isPlaying) {
                        //    stop();
                        //    play(jcAudio);

                        //} else if (isPlaying) {
                        //    stop();
                        //    play(jcAudio);
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            updateTimeAudio()

            jcPlayerServiceListeners?.let {
                it.forEach { listener -> listener.onPlaying() }
            }

            jcPlayerStatusListeners?.let {
                it.forEach { listener ->
                    jcStatus.jcAudio = jcAudio
                    jcStatus.playState = JcStatus.PlayState.PLAY
                    jcStatus.duration = 0
                    jcStatus.currentPosition = 0
                    listener.onPlayingStatus(jcStatus)
                }
            }

            notificationListener?.onPlaying()

        } else {
            throwError(jcAudio.path, jcAudio.origin)
        }
    }

    fun seekTo(time: Int) {
        Log.d("time = ", Integer.toString(time))
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(time)
        }
    }

    private fun updateTimeAudio() {
        object : Thread() {
            override fun run() {
                while (isPlaying) {
                    try {
                        jcPlayerServiceListeners?.let {
                            it.forEach { listener ->
                                listener.onTimeChanged(mediaPlayer?.currentPosition?.toLong()
                                        ?: 0)
                            }
                        }

                        notificationListener?.onTimeChanged(mediaPlayer?.currentPosition?.toLong()
                                ?: 0)

                        jcPlayerStatusListeners?.let {
                            it.forEach {
                                jcStatus.playState = JcStatus.PlayState.PLAY
                                jcStatus.duration = mediaPlayer!!.duration.toLong()
                                jcStatus.currentPosition = mediaPlayer!!.currentPosition.toLong()
                                it.onTimeChangedStatus(jcStatus)
                            }
                        }

                        Thread.sleep(200)
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, i: Int) {}

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        jcPlayerServiceListeners?.let {
            it.forEach { listener ->
                listener.onCompletedAudio()
            }
        }

        jcPlayerStatusListeners?.let {
            it.forEach { listener ->
                listener.onCompletedAudioStatus(jcStatus)
            }
        }

        notificationListener?.onCompletedAudio()
    }

    private fun throwError(path: String, origin: Origin) {
        when (origin) {
            Origin.URL -> throw AudioUrlInvalidException(path)

            Origin.RAW -> try {
                throw AudioRawInvalidException(path)
            } catch (e: AudioRawInvalidException) {
                e.printStackTrace()
            }

            Origin.ASSETS -> try {
                throw AudioAssetsInvalidException(path)
            } catch (e: AudioAssetsInvalidException) {
                e.printStackTrace()
            }

            Origin.FILE_PATH -> try {
                throw AudioFilePathInvalidException(path)
            } catch (e: AudioFilePathInvalidException) {
                e.printStackTrace()
            }
        }

        currentAudio?.let { audio ->
            invalidPathListeners?.let {
                it.forEach { listener -> listener.onPathError(audio) }
            }
        }
    }

    private fun isAudioFileValid(path: String, origin: Origin): Boolean {
        when (origin) {
            Origin.URL -> return path.startsWith("http") || path.startsWith("https")

            Origin.RAW -> {
                assetFileDescriptor = null
                assetFileDescriptor =
                        applicationContext.resources.openRawResourceFd(Integer.parseInt(path))
                return assetFileDescriptor != null
            }

            Origin.ASSETS -> return try {
                assetFileDescriptor = null
                assetFileDescriptor = applicationContext.assets.openFd(path)
                assetFileDescriptor != null
            } catch (e: IOException) {
                e.printStackTrace() //TODO: need to give user more readable error.
                false
            }

            Origin.FILE_PATH -> {
                val file = File(path)
                //TODO: find an alternative to checking if file is exist, this code is slower on average.
                //read more: http://stackoverflow.com/a/8868140
                return file.exists()
            }

            else -> // We should never arrive here.
                return false // We don't know what the origin of the Audio File
        }
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        return false
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        isPlaying = true
        this.duration = mediaPlayer.duration
        this.currentTime = mediaPlayer.currentPosition
        updateTimeAudio()


        currentAudio?.let { audio ->
            notificationListener?.onUpdateTitle(audio.title)
            notificationListener?.onPreparedAudio(audio.title, mediaPlayer.duration)

            jcPlayerServiceListeners?.let {
                it.forEach { listener ->
                    listener.onUpdateTitle(audio.title)
                    listener.onPreparedAudio(audio.title, mediaPlayer.duration)
                }
            }
        }

        jcPlayerStatusListeners?.let {
            it.forEach {
                jcStatus.jcAudio = currentAudio
                jcStatus.playState = JcStatus.PlayState.PLAY
                jcStatus.duration = duration.toLong()
                jcStatus.currentPosition = currentTime.toLong()
                it.onPreparedAudioStatus(jcStatus)
            }
        }
    }
}
