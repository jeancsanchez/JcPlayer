package com.example.jean.jcplayer.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.jean.jcplayer.JcPlayerManager
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException

class JcPlayerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val jcPlayerManager = JcPlayerManager.getInstance(context)
        var action = ""

        if (intent.hasExtra(JcNotificationService.ACTION)) {
            action = intent.getStringExtra(JcNotificationService.ACTION)
        }

        when (action) {
            JcNotificationService.PLAY -> try {
                jcPlayerManager.get()?.continueAudio()
                jcPlayerManager.get()?.updateNotification()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            JcNotificationService.PAUSE -> try {
                jcPlayerManager.get()?.pauseAudio()
                jcPlayerManager.get()?.updateNotification()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            JcNotificationService.NEXT -> try {
                jcPlayerManager.get()?.nextAudio()
            } catch (e: AudioListNullPointerException) {
                try {
                    jcPlayerManager.get()?.continueAudio()
                } catch (e1: AudioListNullPointerException) {
                    e1.printStackTrace()
                }

            }

            JcNotificationService.PREVIOUS -> try {
                jcPlayerManager.get()?.previousAudio()
            } catch (e: Exception) {
                try {
                    jcPlayerManager.get()?.continueAudio()
                } catch (e1: AudioListNullPointerException) {
                    e1.printStackTrace()
                }
            }

        }
    }
}
