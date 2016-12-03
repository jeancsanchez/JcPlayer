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

public class JcAudioPlayer {
    public JcPlayerService jcPlayerService;
    private JcPlayerService.JCPlayerServiceListener listener;
    private JcPlayerService.JCPlayerServiceListener notificationListener;
    private JcNotificationPlayer jcNotificationPlayer;
    private List<JcAudio> JcAudioList;
    private JcAudio currentJcAudio;
    private int currentPositionList;
    private Context context;
    private static JcAudioPlayer instance = null;
    public boolean mBound = false;
    private boolean playing;
    private boolean paused;
    private int position = 1;

    public JcAudioPlayer(Context context, List<JcAudio> JcAudioList, JcPlayerService.JCPlayerServiceListener listener){
        this.context = context;
        this.JcAudioList = JcAudioList;
        this.listener = listener;
        instance = JcAudioPlayer.this;
        this.jcNotificationPlayer = new JcNotificationPlayer(context);
    }

    public void setInstance(JcAudioPlayer instance){
        this.instance = instance;
    }

    public void registerNotificationListener(JcPlayerService.JCPlayerServiceListener notificationListener){
        this.notificationListener = notificationListener;
        if(jcNotificationPlayer != null)
            jcPlayerService.registerNotificationListener(notificationListener);
    }

    public static JcAudioPlayer getInstance(){
        return instance;
    }

    public void playAudio(JcAudio JcAudio) throws AudioListNullPointerException {
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
                    JcAudio nextJcAudio = JcAudioList.get(currentPositionList + position);
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
                    JcAudio previousJcAudio = JcAudioList.get(currentPositionList - position);
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
            Intent intent = new Intent(context.getApplicationContext(), JcPlayerService.class);
            intent.putExtra(JcNotificationPlayer.PLAYLIST, (Serializable) JcAudioList);
            intent.putExtra(JcNotificationPlayer.CURRENT_AUDIO, currentJcAudio);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            JcPlayerService.JCPlayerServiceBinder binder = (JcPlayerService.JCPlayerServiceBinder) service;
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
                //TODO: Add readable exception here
            }

        if (jcNotificationPlayer != null) {
            jcNotificationPlayer.destroyNotificationIfExists();
        }

        if(JcAudioPlayer.getInstance() != null)
            JcAudioPlayer.getInstance().setInstance(null);
    }
}
