package com.example.jean.jcplayer.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jean.jcplayer.general.errors.AudioListNullPointerException;
import com.example.jean.jcplayer.JcAudioPlayer;

public class JcPlayerNotificationReceiver extends BroadcastReceiver {
    public JcPlayerNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        JcAudioPlayer jcAudioPlayer = JcAudioPlayer.getInstance(context, null, null);
        String action = "";

        if (intent.hasExtra(JcNotificationService.ACTION)) {
            action = intent.getStringExtra(JcNotificationService.ACTION);
        }

        switch (action) {
            case JcNotificationService.PLAY:
                try {
                    jcAudioPlayer.continueAudio();
                    jcAudioPlayer.updateNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationService.PAUSE:
                try {
                    if(jcAudioPlayer != null) {
                        jcAudioPlayer.pauseAudio();
                        jcAudioPlayer.updateNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationService.NEXT:
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

            case JcNotificationService.PREVIOUS:
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
