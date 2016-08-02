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
    private List<Audio> audioList;
    private Audio currentAudio;
    private int currentPositionList;
    private Context context;
    private static JCAudioPlayer instance;
    public boolean mBound = false;
    private boolean playing;
    private boolean paused;
    private int position = 1;

    public JCAudioPlayer(Context context, List<Audio> audioList, JCPlayerService.JCPlayerServiceListener listener){
        this.context = context;
        this.audioList = audioList;
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

    public void playAudio(Audio audio) throws AudioListNullPointer {
        if(audioList == null || audioList.size() == 0)
            throw  new AudioListNullPointer();
        else {
            if (currentAudio == null)
                currentAudio = audioList.get(0);
            else
                currentAudio = audio;

            if (!mBound)
                initJCPlayerService();
            else {
                jcPlayerService.play(currentAudio);
                updatePositionAudioList();
                mBound = true;
                playing = true;
                paused = false;
            }
        }
    }

    public void nextAudio() throws AudioListNullPointer {
        if(audioList == null || audioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentAudio != null) {
                try {
                    Audio nextAudio = audioList.get(currentPositionList + position);
                    this.currentAudio = nextAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(nextAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(audioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void previousAudio() throws AudioListNullPointer {
        if(audioList == null || audioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentAudio != null) {
                try {
                    Audio previousAudio = audioList.get(currentPositionList - position);
                    this.currentAudio = previousAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(previousAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(audioList.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void pauseAudio() {
        jcPlayerService.pause(currentAudio);
        paused = true;
        playing = false;
    }

    public void continueAudio() throws AudioListNullPointer {
        if(audioList == null || audioList.size() == 0)
            throw new AudioListNullPointer();

        else {
            if (currentAudio == null)
                currentAudio = audioList.get(0);
            playAudio(currentAudio);
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

    public void createNewNotification(){
        if(currentAudio != null)
            jcNotificationPlayer.createNotificationPlayer(currentAudio.getTitle());
    }


    private void updatePositionAudioList() {
        for(int i = 0; i < audioList.size(); i ++){
            if(audioList.get(i).getId() == currentAudio.getId())
                this.currentPositionList = i;
        }
    }

    private synchronized void initJCPlayerService(){
        if(!mBound) {
            Intent intent = new Intent(context.getApplicationContext(), JCPlayerService.class);
            intent.putExtra(JCNotificationPlayer.PLAYLIST, (Serializable) audioList);
            intent.putExtra(JCNotificationPlayer.CURRENT_AUDIO, currentAudio);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            JCPlayerService.JCPlayerServiceBinder binder = (JCPlayerService.JCPlayerServiceBinder) service;
            jcPlayerService = binder.getService();
            jcPlayerService.registerListener(listener);
            jcPlayerService.play(currentAudio);
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
