package com.example.jean.jcplayer.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jean.jcplayer.general.errors.AudioListNullPointerException;
import com.example.jean.jcplayer.JcPlayerManager;

public class JcPlayerNotificationReceiver extends BroadcastReceiver {
    public JcPlayerNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        JcPlayerManager jcPlayerManager = JcPlayerManager.getInstance(context, null, null);
        String action = "";

        if (intent.hasExtra(JcNotificationService.ACTION)) {
            action = intent.getStringExtra(JcNotificationService.ACTION);
        }

        switch (action) {
            case JcNotificationService.PLAY:
                try {
                    jcPlayerManager.continueAudio();
                    jcPlayerManager.updateNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationService.PAUSE:
                try {
                    if(jcPlayerManager != null) {
                        jcPlayerManager.pauseAudio();
                        jcPlayerManager.updateNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JcNotificationService.NEXT:
                try {
                    jcPlayerManager.nextAudio();
                } catch (AudioListNullPointerException e) {
                    try {
                        jcPlayerManager.continueAudio();
                    } catch (AudioListNullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
                break;

            case JcNotificationService.PREVIOUS:
                try {
                    jcPlayerManager.previousAudio();
                } catch (Exception e) {
                    try {
                        jcPlayerManager.continueAudio();
                    } catch (AudioListNullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
        }
    }
}
