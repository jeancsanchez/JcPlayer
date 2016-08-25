package com.example.jean.jcplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class JCPlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener{
    private final IBinder mBinder = new JCPlayerServiceBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int duration;
    private int currentTime;
    private Audio currentAudio;
    private JCPlayerServiceListener jcPlayerServiceListener;
    private JCPlayerServiceListener notificationListener;

    public class JCPlayerServiceBinder extends Binder {
        public JCPlayerService getService(){
            return JCPlayerService.this;
        }
    }

    public void registerListener(JCPlayerServiceListener jcPlayerServiceListener){
        this.jcPlayerServiceListener = jcPlayerServiceListener;
    }

    public void registerNotificationListener(JCPlayerServiceListener notificationListener){
        this.notificationListener = notificationListener;
    }

    public interface JCPlayerServiceListener{
        void onPreparedAudio(String audioName, int duration);
        void onCompletedAudio();
        void onPaused();
        void onContinueAudio();
        void onPlaying();
        void onTimeChanged(long currentTime);
        void updateTextTime(String time);
        void updateTitle(String title);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public JCPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void pause(Audio audio) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            duration = mediaPlayer.getDuration();
            currentTime = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }

        jcPlayerServiceListener.onPaused();
        if(notificationListener != null)
            notificationListener.onPaused();
    }

    public void destroy(){
        stop();
        stopSelf();
    }

    public void stop(){
        if( mediaPlayer != null ) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        isPlaying = false;
    }

    public void play(Audio audio)  {
        this.currentAudio = audio;

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audio.getUrl());
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);

            } else if (isPlaying) {
                stop();
                play(audio);

            } else {
                mediaPlayer.start();
                isPlaying = true;

                if(jcPlayerServiceListener != null)
                    jcPlayerServiceListener.onContinueAudio();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        updateTimeAudio();
        jcPlayerServiceListener.onPlaying();

        if(notificationListener != null)
            notificationListener.onPlaying();
    }

    public void seekTo(int time){
        mediaPlayer.seekTo(time);
    }

    private void updateTimeAudio() {
        new Thread() {
            public void run() {
                while (isPlaying){
                    try {

                        updateTimeAudio(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition());
                        Thread.sleep(1000);
                    }catch (IllegalStateException | InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updateTimeAudio(final long duration, final long currentTime){
            long aux;
            int minute, second;

            // DURATION
            aux = duration / 1000;
            minute = (int) (aux / 60);
            second = (int) (aux % 60);
            String sDuration = minute < 10 ? "0"+minute : minute+"";
            sDuration += ":" + (second < 10 ? "0"+second : second);

            // CURRENT TIME
            aux = currentTime / 1000;
            minute = (int) (aux / 60);
            second = (int) (aux % 60);
            String sCurrentTime = minute < 10 ? "0"+minute : minute+"";
            sCurrentTime += ":" + (second < 10 ? "0"+second : second);

            String time = sCurrentTime + " /" + sDuration;

            jcPlayerServiceListener.updateTextTime(time);
            jcPlayerServiceListener.onTimeChanged(currentTime);
            if (notificationListener != null){
                notificationListener.updateTextTime(time);
                jcPlayerServiceListener.onTimeChanged(currentTime);
            }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(jcPlayerServiceListener != null)
            jcPlayerServiceListener.onCompletedAudio();
        if(notificationListener != null)
            notificationListener.onCompletedAudio();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        isPlaying = true;
        this.duration = mediaPlayer.getDuration();
        this.currentTime = mediaPlayer.getCurrentPosition();
        updateTimeAudio();

        jcPlayerServiceListener.updateTitle(currentAudio.getTitle());
        jcPlayerServiceListener.onPreparedAudio(currentAudio.getTitle(), mediaPlayer.getDuration());

        if(notificationListener != null) {
            notificationListener.updateTitle(currentAudio.getTitle());
            notificationListener.onPreparedAudio(currentAudio.getTitle(), mediaPlayer.getDuration());
        }
    }
}