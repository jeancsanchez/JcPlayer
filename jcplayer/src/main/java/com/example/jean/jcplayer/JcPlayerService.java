package com.example.jean.jcplayer;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jean.jcplayer.JcPlayerExceptions.AudioAssetsInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioFilePathInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioRawInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioUrlInvalidException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JcPlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener{

    private static final String TAG = JcPlayerService.class.getSimpleName();

    private final IBinder mBinder = new JcPlayerServiceBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int duration;
    private int currentTime;
    private JcAudio currentJcAudio;
    private JcStatus jcStatus = new JcStatus();
    private List<JcPlayerView.JcPlayerViewServiceListener> jcPlayerServiceListeners;
    private List<JcPlayerView.OnInvalidPathListener> invalidPathListeners;
    private List<JcPlayerView.JcPlayerViewStatusListener> jcPlayerStatusListeners;
    private JcPlayerView.JcPlayerViewServiceListener notificationListener;
    private AssetFileDescriptor assetFileDescriptor = null; // For Asset and Raw file.

    public class JcPlayerServiceBinder extends Binder {
        public JcPlayerService getService() {
            return JcPlayerService.this;
        }
    }

    public void registerNotificationListener(JcPlayerView.JcPlayerViewServiceListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void registerServicePlayerListener(JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener) {
        if (jcPlayerServiceListeners == null) {
            jcPlayerServiceListeners = new ArrayList<>();
        }

        if (!jcPlayerServiceListeners.contains(jcPlayerServiceListener)) {
            jcPlayerServiceListeners.add(jcPlayerServiceListener);
        }
    }

    public void registerInvalidPathListener(JcPlayerView.OnInvalidPathListener invalidPathListener) {
        if (invalidPathListeners == null) {
            invalidPathListeners = new ArrayList<>();
        }

        if (!invalidPathListeners.contains(invalidPathListener)) {
            invalidPathListeners.add(invalidPathListener);
        }
    }

    public void registerStatusListener(JcPlayerView.JcPlayerViewStatusListener statusListener) {
        if (jcPlayerStatusListeners == null) {
            jcPlayerStatusListeners = new ArrayList<>();
        }

        if (!jcPlayerStatusListeners.contains(statusListener)) {
            jcPlayerStatusListeners.add(statusListener);
        }
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

    public void pause(JcAudio jcAudio) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            duration = mediaPlayer.getDuration();
            currentTime = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }

        for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
            jcPlayerServiceListener.onPaused();
        }

        if (notificationListener != null) {
            notificationListener.onPaused();
        }

        if(jcPlayerStatusListeners != null) {
            for (JcPlayerView.JcPlayerViewStatusListener jcPlayerStatusListener : jcPlayerStatusListeners) {
                jcStatus.setJcAudio(jcAudio);
                jcStatus.setDuration(duration);
                jcStatus.setCurrentPosition(currentTime);
                jcStatus.setPlayState(JcStatus.PlayState.PAUSE);
                jcPlayerStatusListener.onPausedStatus(jcStatus);
            }
        }
    }

    public void destroy() {
        stop();
        stopSelf();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        isPlaying = false;
    }

    private JcAudio tempJcAudio;
    public void play(JcAudio jcAudio) {
        tempJcAudio = this.currentJcAudio;
        this.currentJcAudio = jcAudio;

        if (isAudioFileValid(jcAudio.getPath(), jcAudio.getOrigin())) {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();

                    if (jcAudio.getOrigin() == Origin.URL) {
                        mediaPlayer.setDataSource(jcAudio.getPath());
                    } else if (jcAudio.getOrigin() == Origin.RAW) {
                        assetFileDescriptor = getApplicationContext().getResources().openRawResourceFd(Integer.parseInt(jcAudio.getPath()));
                        if (assetFileDescriptor == null) return; // TODO: Should throw error.
                        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                                assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                        assetFileDescriptor.close();
                        assetFileDescriptor = null;
                    } else if (jcAudio.getOrigin() == Origin.ASSETS) {
                        assetFileDescriptor = getApplicationContext().getAssets().openFd(jcAudio.getPath());
                        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                                assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                        assetFileDescriptor.close();
                        assetFileDescriptor = null;
                    } else if (jcAudio.getOrigin() == Origin.FILE_PATH) {
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(jcAudio.getPath()));
                    }

                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.setOnBufferingUpdateListener(this);
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.setOnErrorListener(this);

                //} else if (isPlaying) {
                //    stop();
                //    play(jcAudio);
                } else {
                    if (isPlaying) {
                        stop();
                        play(jcAudio);
                    } else {
                        if(tempJcAudio != jcAudio) {
                            stop();
                            play(jcAudio);
                        } else {
                            mediaPlayer.start();
                            isPlaying = true;

                            if (jcPlayerServiceListeners != null) {
                                for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                                    jcPlayerServiceListener.onContinueAudio();
                                }
                            }

                            if (jcPlayerStatusListeners != null) {
                                for (JcPlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                                    jcStatus.setJcAudio(jcAudio);
                                    jcStatus.setPlayState(JcStatus.PlayState.PLAY);
                                    jcStatus.setDuration(mediaPlayer.getDuration());
                                    jcStatus.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                    jcPlayerViewStatusListener.onContinueAudioStatus(jcStatus);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateTimeAudio();

            for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.onPlaying();
            }

            if (jcPlayerStatusListeners != null) {
                for (JcPlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                    jcStatus.setJcAudio(jcAudio);
                    jcStatus.setPlayState(JcStatus.PlayState.PLAY);
                    jcStatus.setDuration(0);
                    jcStatus.setCurrentPosition(0);
                    jcPlayerViewStatusListener.onPlayingStatus(jcStatus);
                }
            }

            if (notificationListener != null) notificationListener.onPlaying();

        } else {
            throwError(jcAudio.getPath(), jcAudio.getOrigin());
        }
    }

    public void seekTo(int time){
        Log.d("time = ", Integer.toString(time));
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }

    private void updateTimeAudio() {
        new Thread() {
            public void run() {
                while (isPlaying) {
                    try {

                        if (jcPlayerServiceListeners != null) {
                            for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                                jcPlayerServiceListener.onTimeChanged(mediaPlayer.getCurrentPosition());
                            }
                        }
                        if (notificationListener != null) {
                            notificationListener.onTimeChanged(mediaPlayer.getCurrentPosition());
                        }

                        if (jcPlayerStatusListeners != null) {
                            for (JcPlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                                jcStatus.setPlayState(JcStatus.PlayState.PLAY);
                                jcStatus.setDuration(mediaPlayer.getDuration());
                                jcStatus.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                jcPlayerViewStatusListener.onTimeChangedStatus(jcStatus);
                            }
                        }
                        Thread.sleep(200);
                    } catch (IllegalStateException | InterruptedException | NullPointerException e) {
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
        if (jcPlayerServiceListeners != null) {
            for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.onCompletedAudio();
            }
        }
        if (notificationListener != null) {
            notificationListener.onCompletedAudio();
        }

        if (jcPlayerStatusListeners != null) {
            for (JcPlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                jcPlayerViewStatusListener.onCompletedAudioStatus(jcStatus);
            }
        }
    }

    private void throwError(String path, Origin origin) {
        if (origin == Origin.URL) {
            throw new AudioUrlInvalidException(path);
        } else if (origin == Origin.RAW) {
            try {
                throw new AudioRawInvalidException(path);
            } catch (AudioRawInvalidException e) {
                e.printStackTrace();
            }
        } else if (origin == Origin.ASSETS) {
            try {
                throw new AudioAssetsInvalidException(path);
            } catch (AudioAssetsInvalidException e) {
                e.printStackTrace();
            }
        } else if (origin == Origin.FILE_PATH) {
            try {
                throw new AudioFilePathInvalidException(path);
            } catch (AudioFilePathInvalidException e) {
                e.printStackTrace();
            }
        }

        if (invalidPathListeners != null) {
            for (JcPlayerView.OnInvalidPathListener onInvalidPathListener : invalidPathListeners) {
                onInvalidPathListener.onPathError(currentJcAudio);
            }
        }
    }


    private boolean isAudioFileValid(String path, Origin origin) {
        if (origin == Origin.URL) {
            return path.startsWith("http") || path.startsWith("https");
        } else if (origin == Origin.RAW) {
            assetFileDescriptor = null;
            assetFileDescriptor = getApplicationContext().getResources().openRawResourceFd(Integer.parseInt(path));
            return assetFileDescriptor != null;
        } else if (origin == Origin.ASSETS) {
            try {
                assetFileDescriptor = null;
                assetFileDescriptor = getApplicationContext().getAssets().openFd(path);
                return assetFileDescriptor != null;
            } catch (IOException e) {
                e.printStackTrace(); //TODO: need to give user more readable error.
                return false;
            }
        } else if (origin == Origin.FILE_PATH) {
            File file = new File(path);
            //TODO: find an alternative to checking if file is exist, this code is slower on average.
            //read more: http://stackoverflow.com/a/8868140
            return file.exists();
        } else {
            // We should never arrive here.
            return false; // We don't know what the origin of the Audio File
        }
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

        if (jcPlayerServiceListeners != null) {
            for (JcPlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.updateTitle(currentJcAudio.getTitle());
                jcPlayerServiceListener.onPreparedAudio(currentJcAudio.getTitle(), mediaPlayer.getDuration());
            }
        }

        if (notificationListener != null) {
            notificationListener.updateTitle(currentJcAudio.getTitle());
            notificationListener.onPreparedAudio(currentJcAudio.getTitle(), mediaPlayer.getDuration());
        }

        if (jcPlayerStatusListeners != null) {
            for (JcPlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                jcStatus.setJcAudio(currentJcAudio);
                jcStatus.setPlayState(JcStatus.PlayState.PLAY);
                jcStatus.setDuration(duration);
                jcStatus.setCurrentPosition(currentTime);
                jcPlayerViewStatusListener.onPreparedAudioStatus(jcStatus);
            }
        }
    }

    public JcAudio getCurrentAudio() {
        return currentJcAudio;
    }
}
