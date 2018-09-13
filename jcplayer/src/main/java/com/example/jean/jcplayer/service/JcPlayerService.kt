package com.example.jean.jcplayer.service

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.Origin
import com.example.jean.jcplayer.general.errors.AudioAssetsInvalidException
import com.example.jean.jcplayer.general.errors.AudioFilePathInvalidException
import com.example.jean.jcplayer.general.errors.AudioRawInvalidException
import com.example.jean.jcplayer.general.errors.AudioUrlInvalidException
import com.example.jean.jcplayer.model.JcAudio
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

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

    var isPlaying: Boolean = false
        private set

    var isPaused: Boolean = true
        private set

    var currentAudio: JcAudio? = null
        private set

    private val jcStatus = JcStatus()

    private var assetFileDescriptor: AssetFileDescriptor? = null // For Asset and Raw file.

    var serviceListener: JcPlayerServiceListener? = null


    inner class JcPlayerServiceBinder : Binder() {
        val service: JcPlayerService
            get() = this@JcPlayerService
    }

    override fun onBind(intent: Intent): IBinder? = binder


    fun play(jcAudio: JcAudio): JcStatus {
        val tempJcAudio = currentAudio
        currentAudio = jcAudio
        var status = JcStatus()

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
                            status = updateStatus(jcAudio, JcStatus.PlayState.CONTINUE)
                            updateTime()
                            serviceListener?.onContinueListener(status)
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

                        status = updateStatus(jcAudio, JcStatus.PlayState.PREPARING)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            throwError(jcAudio.path, jcAudio.origin)
        }

        return status
    }

    fun pause(jcAudio: JcAudio): JcStatus {
        val status = updateStatus(jcAudio, JcStatus.PlayState.PAUSE)
        serviceListener?.onPausedListener(status)
        return status
    }

    fun stop(): JcStatus {
        val status = updateStatus(status = JcStatus.PlayState.STOP)
        serviceListener?.onStoppedListener(status)
        return status
    }


    fun seekTo(time: Int) {
        mediaPlayer?.seekTo(time)
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, i: Int) {}

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        serviceListener?.onCompletedListener()
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        return false
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        val status = updateStatus(currentAudio, JcStatus.PlayState.PLAY)

        updateTime()
        serviceListener?.onPreparedListener(status)
    }

    private fun updateStatus(jcAudio: JcAudio? = null, status: JcStatus.PlayState): JcStatus {
        currentAudio = jcAudio
        jcStatus.jcAudio = jcAudio
        jcStatus.playState = status

        mediaPlayer?.let {
            jcStatus.duration = it.duration.toLong()
            jcStatus.currentPosition = it.currentPosition.toLong()
        }

        when (status) {
            JcStatus.PlayState.PLAY -> {
                try {
                    mediaPlayer?.start()
                    isPlaying = true
                    isPaused = false

                } catch (exception: Exception) {
                    serviceListener?.onError(exception)
                }
            }

            JcStatus.PlayState.STOP -> {
                mediaPlayer?.let {
                    it.stop()
                    it.reset()
                    it.release()
                    mediaPlayer = null
                }

                isPlaying = false
                isPaused = true
            }

            JcStatus.PlayState.PAUSE -> {
                mediaPlayer?.pause()
                isPlaying = false
                isPaused = true
            }

            JcStatus.PlayState.PREPARING -> {
                isPlaying = false
                isPaused = true
            }

            JcStatus.PlayState.PLAYING -> {
                isPlaying = true
                isPaused = false
            }

            else -> { // CONTINUE case
                mediaPlayer?.start()
                isPlaying = true
                isPaused = false
            }
        }

        return jcStatus
    }

    private fun updateTime() {
        object : Thread() {
            override fun run() {
                while (isPlaying) {
                    try {
                        val status = updateStatus(currentAudio, JcStatus.PlayState.PLAYING)
                        serviceListener?.onTimeChangedListener(status)
                        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
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
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun finalize() {
        onDestroy()
        stopSelf()
    }
}
