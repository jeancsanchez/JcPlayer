package com.example.jean.jcplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.jean.jcplayer.JcPlayerExceptions.AudioListNullPointerException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jean on 12/07/16.
 */

public class JCAudioPlayer {
    public JCPlayerService jcPlayerService;
    private JCPlayerService.JCPlayerServiceListener listener;
    private JCPlayerService.JCPlayerServiceListener notificationListener;
    private JCNotificationPlayer jcNotificationPlayer;
    private List<JCAudio> JcAudioList;
    private JCAudio currentJcAudio;
    private int currentPositionList;
    private Context context;
    private static JCAudioPlayer instance = null;
    public boolean mBound = false;
    private boolean playing;
    private boolean paused;
    private int position = 1;

    public JCAudioPlayer(Context context, List<JCAudio> JcAudioList, JCPlayerService.JCPlayerServiceListener listener){
        this.context = context;
        this.JcAudioList = JcAudioList;
        this.listener = listener;
        instance = JCAudioPlayer.this;
        this.jcNotificationPlayer = new JCNotificationPlayer(context);
    }

    public void setInstance(JCAudioPlayer instance){
        this.instance = instance;
    }

    public void registerNotificationListener(JCPlayerService.JCPlayerServiceListener notificationListener){
        this.notificationListener = notificationListener;
        if(jcNotificationPlayer != null)
            jcPlayerService.registerNotificationListener(notificationListener);
    }

    public static JCAudioPlayer getInstance(){
        return instance;
    }

    public void playAudio(JCAudio JcAudio) throws AudioListNullPointerException {
        if(JcAudioList == null || JcAudioList.size() == 0)
            throw  new AudioListNullPointerException();
        else {
            currentJcAudio = JcAudio;

            if (!mBound)
                initJCPlayerService();
            else {
                jcPlayerService.play(currentJcAudio);
                updatePositionAudioList();
                mBound = true;
                playing = true;
                paused = false;
            }
        }
    }

    public void nextAudio() throws AudioListNullPointerException {
        if(JcAudioList == null || JcAudioList.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio != null) {
                try {
                    JCAudio nextJcAudio = JcAudioList.get(currentPositionList + position);
                    this.currentJcAudio = nextJcAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(nextJcAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(JcAudioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void previousAudio() throws AudioListNullPointerException {
        if(JcAudioList == null || JcAudioList.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio != null) {
                try {
                    JCAudio previousJcAudio = JcAudioList.get(currentPositionList - position);
                    this.currentJcAudio = previousJcAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(previousJcAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(JcAudioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void pauseAudio() {
        jcPlayerService.pause(currentJcAudio);
        paused = true;
        playing = false;
    }

    public void continueAudio() throws AudioListNullPointerException {
        if(JcAudioList == null || JcAudioList.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio == null)
                currentJcAudio = JcAudioList.get(0);
            playAudio(currentJcAudio);
            playing = true;
            paused = false;
        }
    }

    public void createNewNotification(int iconResource){
        if(currentJcAudio != null)
            jcNotificationPlayer.createNotificationPlayer(currentJcAudio.getTitle(), iconResource);
    }

    public void updateNotification(){
        jcNotificationPlayer.updateNotification();
    }

    public void seekTo(int time){
        if(jcPlayerService != null)
            jcPlayerService.seekTo(time);
    }


    private void updatePositionAudioList() {
        for(int i = 0; i < JcAudioList.size(); i ++){
            if(JcAudioList.get(i).getId() == currentJcAudio.getId())
                this.currentPositionList = i;
        }
    }

    private synchronized void initJCPlayerService(){
        if(!mBound) {
            Intent intent = new Intent(context.getApplicationContext(), JCPlayerService.class);
            intent.putExtra(JCNotificationPlayer.PLAYLIST, (Serializable) JcAudioList);
            intent.putExtra(JCNotificationPlayer.CURRENT_AUDIO, currentJcAudio);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            JCPlayerService.JCPlayerServiceBinder binder = (JCPlayerService.JCPlayerServiceBinder) service;
            jcPlayerService = binder.getService();
            jcPlayerService.registerListener(listener);
            jcPlayerService.play(currentJcAudio);
            updatePositionAudioList();
            mBound = true;
            playing = true;
            paused = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            playing = false;
            paused = true;
        }
    };

    public boolean isPlaying(){
        return playing;
    }

    public boolean isPaused(){
        return paused;
    }

    public void kill() {
        if (jcPlayerService != null) {
            jcPlayerService.stop();
            jcPlayerService.destroy();
        }

        if (mBound)
            try {
                context.unbindService(mConnection);
            }catch (IllegalArgumentException e){

            }

        if (jcNotificationPlayer != null) {
            jcNotificationPlayer.destroyNotificationIfExists();
        }

        if(JCAudioPlayer.getInstance() != null)
            JCAudioPlayer.getInstance().setInstance(null);
    }


}
