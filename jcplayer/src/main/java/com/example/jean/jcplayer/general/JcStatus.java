package com.example.jean.jcplayer.general;

import com.example.jean.jcplayer.model.JcAudio;

/**
 * Created by rio on 02 January 2017.
 */
public class JcStatus {
    private JcAudio jcAudio;
    private long duration;
    private long currentPosition;
    private PlayState playState;

    public JcStatus() {
        this(null, 0, 0, PlayState.PREPARING);
    }

    public JcStatus(JcAudio jcAudio, long duration, long currentPosition, PlayState playState) {
        this.jcAudio = jcAudio;
        this.duration = duration;
        this.currentPosition = currentPosition;
        this.playState = playState;
    }

    public JcAudio getJcAudio() {
        return jcAudio;
    }

    public void setJcAudio(JcAudio jcAudio) {
        this.jcAudio = jcAudio;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PlayState getPlayState() {
        return playState;
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }

    public enum PlayState {
        PLAY, PAUSE, STOP, CONTINUE, PREPARING, PLAYING
    }
}
