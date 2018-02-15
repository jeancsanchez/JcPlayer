package com.example.jean.jcplayer.service

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log

import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener
import com.example.jean.jcplayer.general.errors.AudioAssetsInvalidException
import com.example.jean.jcplayer.general.errors.AudioFilePathInvalidException
import com.example.jean.jcplayer.general.errors.AudioRawInvalidException
import com.example.jean.jcplayer.general.errors.AudioUrlInvalidException
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.Origin
import com.example.jean.jcplayer.view.JcpViewListener

import java.io.File
import java.io.IOException
import java.util.ArrayList

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
        if (jcPlayerServiceListeners == null) {
            jcPlayerServiceListeners = ArrayList()
        }

        if (!jcPlayerServiceListeners!!.contains(jcPlayerServiceListener)) {
            jcPlayerServiceListeners!!.add(jcPlayerServiceListener)
        }
    }

    fun registerInvalidPathListener(invalidPathListener: OnInvalidPathListener) {
        if (invalidPathListeners == null) {
            invalidPathListeners = ArrayList()
        }

        if (!invalidPathListeners!!.contains(invalidPathListener)) {
            invalidPathListeners!!.add(invalidPathListener)
        }
    }

    fun registerStatusListener(statusListener: JcpViewListener) {
        if (jcPlayerStatusListeners == null) {
            jcPlayerStatusListeners = ArrayList()
        }

        if (!jcPlayerStatusListeners!!.contains(statusListener)) {
            jcPlayerStatusListeners!!.add(statusListener)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun pause(jcAudio: JcAudio) {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            duration = mediaPlayer!!.duration
            currentTime = mediaPlayer!!.currentPosition
            isPlaying = false
        }

        for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
            jcPlayerServiceListener.onPaused()
        }

        if (notificationListener != null) {
            notificationListener!!.onPaused()
        }

        if (jcPlayerStatusListeners != null) {
            for (jcPlayerStatusListener in jcPlayerStatusListeners!!) {
                jcStatus.jcAudio = jcAudio
                jcStatus.duration = duration.toLong()
                jcStatus.currentPosition = currentTime.toLong()
                jcStatus.playState = JcStatus.PlayState.PAUSE
                jcPlayerStatusListener.onPausedStatus(jcStatus)
            }
        }
    }

    fun destroy() {
        stop()
        stopSelf()
    }

    fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        isPlaying = false
    }

    fun play(jcAudio: JcAudio) {
        tempJcAudio = this.currentAudio
        this.currentAudio = jcAudio

        if (isAudioFileValid(jcAudio.path, jcAudio.origin)) {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()

                    if (jcAudio.origin == Origin.URL) {
                        mediaPlayer!!.setDataSource(jcAudio.path)
                    } else if (jcAudio.origin == Origin.RAW) {
                        assetFileDescriptor = applicationContext.resources.openRawResourceFd(Integer.parseInt(jcAudio.path))
                        if (assetFileDescriptor == null) return  // TODO: Should throw error.
                        mediaPlayer!!.setDataSource(assetFileDescriptor!!.fileDescriptor,
                                assetFileDescriptor!!.startOffset, assetFileDescriptor!!.length)
                        assetFileDescriptor!!.close()
                        assetFileDescriptor = null
                    } else if (jcAudio.origin == Origin.ASSETS) {
                        assetFileDescriptor = applicationContext.assets.openFd(jcAudio.path)
                        mediaPlayer!!.setDataSource(assetFileDescriptor!!.fileDescriptor,
                                assetFileDescriptor!!.startOffset, assetFileDescriptor!!.length)
                        assetFileDescriptor!!.close()
                        assetFileDescriptor = null
                    } else if (jcAudio.origin == Origin.FILE_PATH) {
                        mediaPlayer!!.setDataSource(applicationContext, Uri.parse(jcAudio.path))
                    }

                    mediaPlayer!!.prepareAsync()
                    mediaPlayer!!.setOnPreparedListener(this)
                    mediaPlayer!!.setOnBufferingUpdateListener(this)
                    mediaPlayer!!.setOnCompletionListener(this)
                    mediaPlayer!!.setOnErrorListener(this)

                    //} else if (isPlaying) {
                    //    stop();
                    //    play(jcAudio);
                } else {
                    if (isPlaying) {
                        stop()
                        play(jcAudio)
                    } else {
                        if (tempJcAudio !== jcAudio) {
                            stop()
                            play(jcAudio)
                        } else {
                            mediaPlayer!!.start()
                            isPlaying = true

                            if (jcPlayerServiceListeners != null) {
                                for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
                                    jcPlayerServiceListener.onContinueAudio()
                                }
                            }

                            if (jcPlayerStatusListeners != null) {
                                for (jcPlayerViewStatusListener in jcPlayerStatusListeners!!) {
                                    jcStatus.jcAudio = jcAudio
                                    jcStatus.playState = JcStatus.PlayState.PLAY
                                    jcStatus.duration = mediaPlayer!!.duration.toLong()
                                    jcStatus.currentPosition = mediaPlayer!!.currentPosition.toLong()
                                    jcPlayerViewStatusListener.onContinueAudioStatus(jcStatus)
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            updateTimeAudio()

            for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
                jcPlayerServiceListener.onPlaying()
            }

            if (jcPlayerStatusListeners != null) {
                for (jcPlayerViewStatusListener in jcPlayerStatusListeners!!) {
                    jcStatus.jcAudio = jcAudio
                    jcStatus.playState = JcStatus.PlayState.PLAY
                    jcStatus.duration = 0
                    jcStatus.currentPosition = 0
                    jcPlayerViewStatusListener.onPlayingStatus(jcStatus)
                }
            }

            if (notificationListener != null) notificationListener!!.onPlaying()

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

                        if (jcPlayerServiceListeners != null) {
                            for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
                                jcPlayerServiceListener.onTimeChanged(mediaPlayer!!.currentPosition.toLong())
                            }
                        }
                        if (notificationListener != null) {
                            notificationListener!!.onTimeChanged(mediaPlayer!!.currentPosition.toLong())
                        }

                        if (jcPlayerStatusListeners != null) {
                            for (jcPlayerViewStatusListener in jcPlayerStatusListeners!!) {
                                jcStatus.playState = JcStatus.PlayState.PLAY
                                jcStatus.duration = mediaPlayer!!.duration.toLong()
                                jcStatus.currentPosition = mediaPlayer!!.currentPosition.toLong()
                                jcPlayerViewStatusListener.onTimeChangedStatus(jcStatus)
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

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, i: Int) {

    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        if (jcPlayerServiceListeners != null) {
            for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
                jcPlayerServiceListener.onCompletedAudio()
            }
        }
        if (notificationListener != null) {
            notificationListener!!.onCompletedAudio()
        }

        if (jcPlayerStatusListeners != null) {
            for (jcPlayerViewStatusListener in jcPlayerStatusListeners!!) {
                jcPlayerViewStatusListener.onCompletedAudioStatus(jcStatus)
            }
        }
    }

    private fun throwError(path: String, origin: Origin) {
        if (origin == Origin.URL) {
            throw AudioUrlInvalidException(path)
        } else if (origin == Origin.RAW) {
            try {
                throw AudioRawInvalidException(path)
            } catch (e: AudioRawInvalidException) {
                e.printStackTrace()
            }

        } else if (origin == Origin.ASSETS) {
            try {
                throw AudioAssetsInvalidException(path)
            } catch (e: AudioAssetsInvalidException) {
                e.printStackTrace()
            }

        } else if (origin == Origin.FILE_PATH) {
            try {
                throw AudioFilePathInvalidException(path)
            } catch (e: AudioFilePathInvalidException) {
                e.printStackTrace()
            }

        }

        if (invalidPathListeners != null) {
            for (onInvalidPathListener in invalidPathListeners!!) {
                onInvalidPathListener.onPathError(currentAudio!!)
            }
        }
    }


    private fun isAudioFileValid(path: String, origin: Origin): Boolean {
        if (origin == Origin.URL) {
            return path.startsWith("http") || path.startsWith("https")
        } else if (origin == Origin.RAW) {
            assetFileDescriptor = null
            assetFileDescriptor = applicationContext.resources.openRawResourceFd(Integer.parseInt(path))
            return assetFileDescriptor != null
        } else if (origin == Origin.ASSETS) {
            try {
                assetFileDescriptor = null
                assetFileDescriptor = applicationContext.assets.openFd(path)
                return assetFileDescriptor != null
            } catch (e: IOException) {
                e.printStackTrace() //TODO: need to give user more readable error.
                return false
            }

        } else if (origin == Origin.FILE_PATH) {
            val file = File(path)
            //TODO: find an alternative to checking if file is exist, this code is slower on average.
            //read more: http://stackoverflow.com/a/8868140
            return file.exists()
        } else {
            // We should never arrive here.
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

        if (jcPlayerServiceListeners != null) {
            for (jcPlayerServiceListener in jcPlayerServiceListeners!!) {
                jcPlayerServiceListener.onUpdateTitle(currentAudio!!.title)
                jcPlayerServiceListener.onPreparedAudio(currentAudio!!.title, mediaPlayer.duration)
            }
        }

        if (notificationListener != null) {
            notificationListener!!.onUpdateTitle(currentAudio!!.title)
            notificationListener!!.onPreparedAudio(currentAudio!!.title, mediaPlayer.duration)
        }

        if (jcPlayerStatusListeners != null) {
            for (jcPlayerViewStatusListener in jcPlayerStatusListeners!!) {
                jcStatus.jcAudio = currentAudio
                jcStatus.playState = JcStatus.PlayState.PLAY
                jcStatus.duration = duration.toLong()
                jcStatus.currentPosition = currentTime.toLong()
                jcPlayerViewStatusListener.onPreparedAudioStatus(jcStatus)
            }
        }
    }

    companion object {

        private val TAG = JcPlayerService::class.java.simpleName
    }
}
