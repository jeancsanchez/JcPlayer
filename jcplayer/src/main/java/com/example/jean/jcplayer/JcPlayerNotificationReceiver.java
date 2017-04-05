package com.example.jean.jcplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jean.jcplayer.JcPlayerExceptions.AudioListNullPointerException;

public class JcPlayerNotificationReceiver extends BroadcastReceiver {
    public JcPlayerNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        JcAudioPlayer jcAudioPlayer = JcAudioPlayer.getInstance();
        String action = "";

        if (intent.hasExtra(JcNotificationPlayerService.ACTION)) {
            action = intent.getStringExtra(JcNotificationPlayerService.ACTION);
        }

        switch (action) {
            case JcNotificationPlayerService.PLAY:
                try {
                    jcAudioPlayer.continueAudio();
                    jcAudioPlayer.updateNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationPlayerService.PAUSE:
                try {
                    if(jcAudioPlayer != null) {
                        jcAudioPlayer.pauseAudio();
                        jcAudioPlayer.updateNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationPlayerService.NEXT:
                try {
                    jcAudioPlayer.nextAudio();
                } catch (AudioListNullPointerException e) {
                    try {
                        jcAudioPlayer.continueAudio();
                    } catch (AudioListNullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
                break;

            case JcNotificationPlayerService.PREVIOUS:
                try {
                    jcAudioPlayer.previousAudio();
                } catch (Exception e) {
                    try {
                        jcAudioPlayer.continueAudio();
                    } catch (AudioListNullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
        }
    }
}
