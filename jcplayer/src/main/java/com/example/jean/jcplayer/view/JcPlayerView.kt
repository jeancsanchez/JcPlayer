package com.example.jean.jcplayer.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.jean.jcplayer.JcPlayerManager
import com.example.jean.jcplayer.R
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener
import com.example.jean.jcplayer.general.errors.UninitializedPlaylistException
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.service.JcpServiceListener
import kotlinx.android.synthetic.main.view_jcplayer.view.*

/**
 * This class is the JcAudio View. Handles user interactions and communicate with [JcPlayerManager].
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 12/07/16.
 * Jesus loves you.
 */
class JcPlayerView : LinearLayout, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private var jcPlayerManager: JcPlayerManager? = null
    private var isInitialized: Boolean = false
    val myPlaylist: List<JcAudio>?
        get() = jcPlayerManager?.playlist

    val isPlaying: Boolean
        get() = jcPlayerManager?.isPlaying ?: false

    val isPaused: Boolean
        get() = jcPlayerManager?.isPaused ?: false

    val currentAudio: JcAudio?
        get() = jcPlayerManager?.currentAudio

    private val onInvalidPathListener = object : OnInvalidPathListener {
        override fun onPathError(jcAudio: JcAudio) {
            dismissProgressBar()
        }
    }

    private var jcpServiceListener: JcpServiceListener = object : JcpServiceListener {
        override fun onPreparedAudio(audioName: String, duration: Int) {
            dismissProgressBar()
            resetPlayerInfo()

            val aux = (duration / 1000).toLong()
            val minute = (aux / 60).toInt()
            val second = (aux % 60).toInt()

            val sDuration = // Minutes
                    ((if (minute < 10) "0" + minute else minute.toString() + "")
                            + ":" +
                            // Seconds
                            if (second < 10) "0" + second else second.toString() + "")

            seekBar?.max = duration
            txtDuration?.post { txtDuration?.text = sDuration }
        }


        override fun onCompletedAudio() {
            resetPlayerInfo()

            try {
                jcPlayerManager?.nextAudio()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        override fun onPaused() {
            btnPlay?.setBackgroundResource(R.drawable.ic_play_black)
            btnPlay?.tag = R.drawable.ic_play_black
        }


        override fun onContinueAudio() {
            dismissProgressBar()
        }


        override fun onPlaying() {
            btnPlay?.setBackgroundResource(R.drawable.ic_pause_black)
            btnPlay?.tag = R.drawable.ic_pause_black
        }


        override fun onTimeChanged(currentTime: Long) {
            val aux = currentTime / 1000
            val minutes = (aux / 60).toInt()
            val seconds = (aux % 60).toInt()
            val sMinutes = if (minutes < 10) "0" + minutes else minutes.toString() + ""
            val sSeconds = if (seconds < 10) "0" + seconds else seconds.toString() + ""

            seekBar?.progress = currentTime.toInt()
            txtCurrentDuration?.let { it.post { it.text = (sMinutes + ":" + sSeconds) } }
        }


        override fun onUpdateTitle(title: String) {
            txtCurrentMusic?.let {
                it.visibility = View.VISIBLE
                YoYo.with(Techniques.FadeInLeft)
                        .duration(TITLE_ANIMATION_DURATION.toLong())
                        .playOn(it)

                it.post { it.text = title }
            }
        }
    }

    companion object {
        private const val PULSE_ANIMATION_DURATION = 200
        private const val TITLE_ANIMATION_DURATION = 600
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private fun init() {
        View.inflate(context, R.layout.view_jcplayer, this)

        btnPlay?.tag = R.drawable.ic_play_black
        btnNext?.setOnClickListener(this)
        btnPrev?.setOnClickListener(this)
        btnPlay?.setOnClickListener(this)
        seekBar?.setOnSeekBarChangeListener(this)
    }

    /**
     * Initialize the playlist and controls.
     *
     * @param playlist List of JcAudio objects that you want play
     */
    fun initPlaylist(playlist: List<JcAudio>) {
        // Don't sort if the playlist have position number.
        // We need to do this because there is a possibility that the user reload previous playlist
        // from persistence storage like sharedPreference or SQLite.
        if (isAlreadySorted(playlist).not()) {
            sortPlaylist(playlist)
        }

        jcPlayerManager = JcPlayerManager(context, playlist as ArrayList<JcAudio>, jcpServiceListener)
                .also { it.registerInvalidPathListener(onInvalidPathListener) }
        //jcPlayerManager.registerStatusListener(jcPlayerViewStatusListener);
        isInitialized = true
    }

    /**
     * Initialize an anonymous playlist with a default JcPlayer title for all audios
     *
     * @param playlist List of urls strings
     */
    fun initAnonPlaylist(playlist: List<JcAudio>) {
        sortPlaylist(playlist)
        generateTitleAudio(playlist, context.getString(R.string.track_number))
        jcPlayerManager = JcPlayerManager(context, playlist as ArrayList<JcAudio>, jcpServiceListener)
                .also { it.registerInvalidPathListener(onInvalidPathListener) }
        //jcPlayerManager.registerStatusListener(jcPlayerViewStatusListener);
        isInitialized = true
    }

    /**
     * Initialize an anonymous playlist, but with a custom title for all audios
     *
     * @param playlist List of JcAudio files.
     * @param title    Default title for all audios
     */
    fun initWithTitlePlaylist(playlist: List<JcAudio>, title: String) {
        sortPlaylist(playlist)
        generateTitleAudio(playlist, title)
        jcPlayerManager = JcPlayerManager(context, playlist as ArrayList<JcAudio>, jcpServiceListener)
                .also { it.registerInvalidPathListener(onInvalidPathListener) }
        //jcPlayerManager.registerStatusListener(jcPlayerViewStatusListener);
        isInitialized = true
    }

    /**
     * Add an audio for the playlist. We can track the JcAudio by
     * its id. So here we returning its id after adding to list.
     *
     * @param jcAudio audio file generated from [JcAudio]
     * @return id of jcAudio.
     */
    fun addAudio(jcAudio: JcAudio): Long {
        createJcAudioPlayer()

        jcPlayerManager?.playlist?.let {
            val lastPosition = it.size

            jcAudio.id = (lastPosition + 1).toLong()
            jcAudio.position = lastPosition + 1

            if (it.contains(jcAudio).not()) {
                it.add(lastPosition, jcAudio)
            }
            return jcAudio.id
        } ?: throw UninitializedPlaylistException()
    }

    /**
     * Remove an audio for the playlist
     *
     * @param jcAudio JcAudio object
     */
    fun removeAudio(jcAudio: JcAudio) {
        jcPlayerManager?.let { player ->
            player.playlist?.let {
                if (it.contains(jcAudio)) {
                    if (it.size > 1) {
                        // play next audio when currently played audio is removed.
                        if (player.isPlaying) {
                            if (player.currentAudio == jcAudio) {
                                it.remove(jcAudio)
                                pause()
                                resetPlayerInfo()
                            } else {
                                it.remove(jcAudio)
                            }
                        } else {
                            it.remove(jcAudio)
                        }
                    } else {
                        //TODO: Maybe we need jcPlayerManager.stopPlay() for stopping the player
                        it.remove(jcAudio)
                        pause()
                        resetPlayerInfo()
                    }
                }
            } ?: throw UninitializedPlaylistException()
        }
    }

    /**
     * Plays the give audio.
     * @param jcAudio The audio to be played.
     */
    fun playAudio(jcAudio: JcAudio) {
        showProgressBar()
        createJcAudioPlayer()

        jcPlayerManager?.let { player ->
            player.playlist?.let {
                if (it.contains(jcAudio).not()) {
                    it.add(jcAudio)
                }
                try {
                    player.playAudio(jcAudio)
                } catch (e: AudioListNullPointerException) {
                    dismissProgressBar()
                    e.printStackTrace()
                }
            } ?: throw UninitializedPlaylistException()
        }
    }

    /**
     * Goes to next audio.
     */
    fun next() {
        jcPlayerManager?.let { player ->
            player.currentAudio?.let {
                resetPlayerInfo()
                showProgressBar()

                try {
                    player.nextAudio()
                } catch (e: AudioListNullPointerException) {
                    dismissProgressBar()
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Continues the current audio.
     */
    fun continueAudio() {
        showProgressBar()

        try {
            jcPlayerManager?.continueAudio()
        } catch (e: AudioListNullPointerException) {
            dismissProgressBar()
            e.printStackTrace()
        }
    }

    /**
     * Pauses the current audio.
     */
    fun pause() {
        jcPlayerManager?.pauseAudio()
    }


    /**
     * Goes to precious audio.
     */
    fun previous() {
        resetPlayerInfo()
        showProgressBar()

        try {
            jcPlayerManager?.previousAudio()
        } catch (e: AudioListNullPointerException) {
            dismissProgressBar()
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        if (isInitialized) {
            if (view.id == R.id.btnPlay) {
                btnPlay?.let {
                    YoYo.with(Techniques.Pulse)
                            .duration(PULSE_ANIMATION_DURATION.toLong())
                            .playOn(it)

                    if (it.tag == R.drawable.ic_pause_black) {
                        pause()
                    } else {
                        continueAudio()
                    }
                }
            }
        }
        if (view.id == R.id.btnNext) {
            btnNext?.let {
                YoYo.with(Techniques.Pulse)
                        .duration(PULSE_ANIMATION_DURATION.toLong())
                        .playOn(it)
                next()
            }
        }

        if (view.id == R.id.btnPrev) {
            btnPrev?.let {
                YoYo.with(Techniques.Pulse)
                        .duration(PULSE_ANIMATION_DURATION.toLong())
                        .playOn(it)
                previous()
            }
        }
    }

    /**
     * Create a notification player with same playlist with a custom icon.
     *
     * @param iconResource icon path.
     */
    fun createNotification(iconResource: Int) {
        jcPlayerManager?.createNewNotification(iconResource)
    }

    /**
     * Create a notification player with same playlist with a default icon
     */
    fun createNotification() {
        jcPlayerManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // For light theme
                it.createNewNotification(R.drawable.ic_notification_default_black)
            } else {
                // For dark theme
                it.createNewNotification(R.drawable.ic_notification_default_white)
            }
        }
    }

    /**
     * Creates a new JcAudio Player.
     */
    private fun createJcAudioPlayer() {
        if (jcPlayerManager == null) {
            val playlist = ArrayList<JcAudio>()
            jcPlayerManager = JcPlayerManager(context, playlist, jcpServiceListener)
        }
        jcPlayerManager?.registerInvalidPathListener(onInvalidPathListener)
        //jcPlayerManager.registerStatusListener(jcPlayerViewStatusListener);
        isInitialized = true
    }

    /**
     * Sorts the playlist.
     */
    private fun sortPlaylist(playlist: List<JcAudio>) {
        for (i in playlist.indices) {
            val jcAudio = playlist[i]
            jcAudio.id = i.toLong()
            jcAudio.position = i
        }
    }

    /**
     * Check if playlist already sorted or not.
     * We need to check because there is a possibility that the user reload previous playlist
     * from persistence storage like sharedPreference or SQLite.
     *
     * @param playlist list of JcAudio
     * @return true if sorted, false if not.
     */
    private fun isAlreadySorted(playlist: List<JcAudio>?): Boolean {
        // If there is position in the first audio, then playlist is already sorted.
        return playlist?.let { it[0].position != -1 } ?: false
    }

    /**
     * Generates a default audio title for each audio on list.
     * @param playlist The audio list.
     * @param title The default title.
     */
    private fun generateTitleAudio(playlist: List<JcAudio>, title: String) {
        for (i in playlist.indices) {
            if (title == context.getString(R.string.track_number)) {
                playlist[i].title = context.getString(R.string.track_number) + " " + (i + 1).toString()
            } else {
                playlist[i].title = title
            }
        }
    }

    private fun showProgressBar() {
        progressBarPlayer?.visibility = ProgressBar.VISIBLE
        btnPlay?.visibility = Button.GONE
        btnNext?.isClickable = false
        btnPrev?.isClickable = false
    }

    private fun dismissProgressBar() {
        progressBarPlayer?.visibility = ProgressBar.GONE
        btnPlay?.visibility = Button.VISIBLE
        btnNext?.isClickable = true
        btnPrev?.isClickable = true
    }

    private fun resetPlayerInfo() {
        seekBar?.progress = 0
        txtCurrentMusic?.text = ""
        txtCurrentDuration?.text = context.getString(R.string.play_initial_time)
        txtDuration?.text = context.getString(R.string.play_initial_time)
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, fromUser: Boolean) {
        jcPlayerManager?.let {
            if (fromUser) {
                it.seekTo(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        showProgressBar()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        dismissProgressBar()
    }

    /**
     * Registers a new [OnInvalidPathListener]
     * @param invalidPathListener The listener.
     */
    fun registerInvalidPathListener(invalidPathListener: OnInvalidPathListener) {
        jcPlayerManager?.registerInvalidPathListener(invalidPathListener)
    }

    /**
     * Kills the player
     */
    fun kill() {
        jcPlayerManager?.kill()
    }

    /**
     * Registers a new [JcpServiceListener]
     * @param jcpServiceListener1 the listener
     */
    fun registerServiceListener(jcpServiceListener1: JcpServiceListener) {
        jcPlayerManager?.registerServiceListener(jcpServiceListener1)
    }


    /**
     * Registers a new [JcpViewListener]
     * @param viewListener The listener.
     */
    fun registerStatusListener(viewListener: JcpViewListener) {
        jcPlayerManager?.registerStatusListener(viewListener)
    }
}
