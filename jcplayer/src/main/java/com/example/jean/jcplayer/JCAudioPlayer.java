package com.example.jean.jcplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.jean.jcplayer.JCPlayerExceptions.AudioListNullPointer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jean on 12/07/16.
 */

public class JCAudioPlayer{
    public JCPlayerService jcPlayerService;
    private JCPlayerService.JCPlayerServiceListener listener;
    private JCPlayerService.JCPlayerServiceListener notificationListener;
    private JCNotificationPlayer jcNotificationPlayer;
    private List<JCAudio> JCAudioList;
    private JCAudio currentJCAudio;
    private int currentPositionList;
    private Context context;
    private static JCAudioPlayer instance;
    public boolean mBound = false;
    private boolean playing;
    private boolean paused;
    private int position = 1;

    public JCAudioPlayer(Context context, List<JCAudio> JCAudioList, JCPlayerService.JCPlayerServiceListener listener){
        this.context = context;
        this.JCAudioList = JCAudioList;
        this.listener = listener;
        instance = JCAudioPlayer.this;
        this.jcNotificationPlayer = new JCNotificationPlayer(context);
    }

    public void registerNotificationListener(JCPlayerService.JCPlayerServiceListener notificationListener){
        this.notificationListener = notificationListener;
        if(jcNotificationPlayer != null)
            jcPlayerService.registerNotificationListener(notificationListener);
    }

    public static JCAudioPlayer getInstance(){
        return instance;
    }

    public void playAudio(JCAudio JCAudio) throws AudioListNullPointer {
        if(JCAudioList == null || JCAudioList.size() == 0)
            throw  new AudioListNullPointer();
        else {
            if (currentJCAudio == null)
                currentJCAudio = JCAudioList.get(0);
            else
                currentJCAudio = JCAudio;

            if (!mBound)
                initJCPlayerService();
            else {
                jcPlayerService.play(currentJCAudio);
                updatePositionAudioList();
                mBound = true;
                playing = true;
                paused = false;
            }
        }
    }

    public void nextAudio() throws AudioListNullPointer {
        if(JCAudioList == null || JCAudioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentJCAudio != null) {
                try {
                    JCAudio nextJCAudio = JCAudioList.get(currentPositionList + position);
                    this.currentJCAudio = nextJCAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(nextJCAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(JCAudioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void previousAudio() throws AudioListNullPointer {
        if(JCAudioList == null || JCAudioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentJCAudio != null) {
                try {
                    JCAudio previousJCAudio = JCAudioList.get(currentPositionList - position);
                    this.currentJCAudio = previousJCAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(previousJCAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(JCAudioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void pauseAudio() {
        jcPlayerService.pause(currentJCAudio);
        paused = true;
        playing = false;
    }

    public void continueAudio() throws AudioListNullPointer {
        if(JCAudioList == null || JCAudioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentJCAudio == null)
                currentJCAudio = JCAudioList.get(0);
            playAudio(currentJCAudio);
            playing = true;
            paused = false;
        }
    }

    public void stop(){
        if(jcPlayerService != null)
            jcPlayerService.destroy();


        if(jcNotificationPlayer != null)
            jcNotificationPlayer.destroy();

        paused = true;
        playing = true;
    }

    public void createNewNotification(int iconResource){
        if(currentJCAudio != null)
            jcNotificationPlayer.createNotificationPlayer(currentJCAudio.getTitle(), iconResource);
    }

    public void updateNotification(){
        jcNotificationPlayer.updateNotification();
    }

    public void seekTo(int time){
        jcPlayerService.seekTo(time);
    }


    private void updatePositionAudioList() {
        for(int i = 0; i < JCAudioList.size(); i ++){
            if(JCAudioList.get(i).getId() == currentJCAudio.getId())
                this.currentPositionList = i;
        }
    }

    private synchronized void initJCPlayerService(){
        if(!mBound) {
            Intent intent = new Intent(context.getApplicationContext(), JCPlayerService.class);
            intent.putExtra(JCNotificationPlayer.PLAYLIST, (Serializable) JCAudioList);
            intent.putExtra(JCNotificationPlayer.CURRENT_AUDIO, currentJCAudio);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            JCPlayerService.JCPlayerServiceBinder binder = (JCPlayerService.JCPlayerServiceBinder) service;
            jcPlayerService = binder.getService();
            jcPlayerService.registerListener(listener);
            jcPlayerService.play(currentJCAudio);
            updatePositionAudioList();
            mBound = true;
            playing = true;
            paused = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public boolean isPlaying(){
        return playing;
    }

    public boolean isPaused(){
        return paused;
    }
}
