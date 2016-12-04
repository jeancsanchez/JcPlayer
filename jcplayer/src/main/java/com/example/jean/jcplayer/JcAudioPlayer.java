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
    private JcPlayerService jcPlayerService;
    private JcPlayerService.JcPlayerServiceListener listener;
    private JcPlayerService.OnInvalidPathListener invalidPathListener;
    private JcNotificationPlayer jcNotificationPlayer;
    private List<JcAudio> playlist;
    private JcAudio currentJcAudio;
    private int currentPositionList;
    private Context context;
    private static JcAudioPlayer instance = null;
    public boolean mBound = false;
    private boolean playing;
    private boolean paused;
    private int position = 1;

    public JcAudioPlayer(Context context, List<JcAudio> playlist, JcPlayerService.JcPlayerServiceListener listener){
        this.context = context;
        this.playlist = playlist;
        this.listener = listener;
        instance = JcAudioPlayer.this;
        this.jcNotificationPlayer = new JcNotificationPlayer(context);

        initService();
    }

    public void setInstance(JcAudioPlayer instance){
        this.instance = instance;
    }

    public void registerNotificationListener(JcPlayerService.JcPlayerServiceListener notificationListener){
        this.listener = notificationListener;
        if(jcNotificationPlayer != null)
            jcPlayerService.registerNotificationListener(notificationListener);
    }

    public void registerInvalidPathListener(JcPlayerService.OnInvalidPathListener registerInvalidPathListener){
        this.invalidPathListener = registerInvalidPathListener;
        if(jcPlayerService != null)
            jcPlayerService.registerInvalidPathListener(invalidPathListener);
    }

    public void registerServiceListener(JcPlayerService.JcPlayerServiceListener jcPlayerServiceListener) {
        this.listener = jcPlayerServiceListener;
        if(jcPlayerService != null)
            jcPlayerService.registerServicePlayerListener(jcPlayerServiceListener);
    }

    public static JcAudioPlayer getInstance(){
        return instance;
    }

    public void playAudio(JcAudio JcAudio) throws AudioListNullPointerException {
        if(playlist == null || playlist.size() == 0)
            throw  new AudioListNullPointerException();

        currentJcAudio = JcAudio;
        jcPlayerService.play(currentJcAudio);
        updatePositionAudioList();
        playing = true;
        paused = false;
    }


    private void initService(){
        if (!mBound)
            startJcPlayerService();
        else
            mBound = true;
    }

    public void nextAudio() throws AudioListNullPointerException {
        if(playlist == null || playlist.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio != null) {
                try {
                    JcAudio nextJcAudio = playlist.get(currentPositionList + position);
                    this.currentJcAudio = nextJcAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(nextJcAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(playlist.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();
            playing = true;
            paused = false;
        }
    }

    public void previousAudio() throws AudioListNullPointerException {
        if(playlist == null || playlist.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio != null) {
                try {
                    JcAudio previousJcAudio = playlist.get(currentPositionList - position);
                    this.currentJcAudio = previousJcAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(previousJcAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(playlist.get(0));
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
        if(playlist == null || playlist.size() == 0)
            throw new AudioListNullPointerException();

        else {
            if (currentJcAudio == null)
                currentJcAudio = playlist.get(0);
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
        for(int i = 0; i < playlist.size(); i ++){
            if(playlist.get(i).getId() == currentJcAudio.getId())
                this.currentPositionList = i;
        }
    }

    private synchronized void startJcPlayerService(){
        if(!mBound) {
            Intent intent = new Intent(context.getApplicationContext(), JcPlayerService.class);
            intent.putExtra(JcNotificationPlayer.PLAYLIST, (Serializable) playlist);
            intent.putExtra(JcNotificationPlayer.CURRENT_AUDIO, currentJcAudio);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            JcPlayerService.JcPlayerServiceBinder binder = (JcPlayerService.JcPlayerServiceBinder) service;
            jcPlayerService = binder.getService();

            if(listener != null)
                jcPlayerService.registerServicePlayerListener(listener);

            if(invalidPathListener != null)
                jcPlayerService.registerInvalidPathListener(invalidPathListener);

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            playing = false;
            paused = true;
        }
    };

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

    public List<JcAudio> getPlaylist() {
        return playlist;
    }

    public JcAudio getCurrentAudio() {
        return jcPlayerService.getCurrentAudio();
    }
}
