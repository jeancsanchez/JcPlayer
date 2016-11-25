package com.example.jean.jcplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class JcPlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener{
    private final IBinder mBinder = new JCPlayerServiceBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int duration;
    private int currentTime;
    private JcAudio currentJcAudio;
    private JCPlayerServiceListener jcPlayerServiceListener;
    private JCPlayerServiceListener notificationListener;

    public class JCPlayerServiceBinder extends Binder {
        public JcPlayerService getService(){
            return JcPlayerService.this;
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

    public JcPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void pause(JcAudio JcAudio) {
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

    public void play(JcAudio JcAudio)  {
        this.currentJcAudio = JcAudio;

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(JcAudio.getUrl());
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);

            } else if (isPlaying) {
                stop();
                play(JcAudio);

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

                        if(jcPlayerServiceListener  != null)
                            jcPlayerServiceListener.onTimeChanged(mediaPlayer.getCurrentPosition());

                        if (notificationListener != null)
                                        notificationListener.onTimeChanged(mediaPlayer.getCurrentPosition());

                        Thread.sleep(1000);
                    }catch (IllegalStateException | InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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

        jcPlayerServiceListener.updateTitle(currentJcAudio.getTitle());
        jcPlayerServiceListener.onPreparedAudio(currentJcAudio.getTitle(), mediaPlayer.getDuration());

        if(notificationListener != null) {
            notificationListener.updateTitle(currentJcAudio.getTitle());
            notificationListener.onPreparedAudio(currentJcAudio.getTitle(), mediaPlayer.getDuration());
        }
    }
}