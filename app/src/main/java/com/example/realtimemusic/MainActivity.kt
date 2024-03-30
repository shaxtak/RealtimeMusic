package com.example.realtimemusic

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultRingtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, defaultRingtoneUri)

        val button: Button = findViewById(R.id.btn_play)

        button.setOnClickListener {
            if (!ringtone.isPlaying) {
                ringtone.play()
                databaseRef.setValue("online")
            } else {
                ringtone.stop()
                databaseRef.setValue("offline")
            }
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("ring_sound_status")

        val ringtoneStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isPlaying = dataSnapshot.getValue(Boolean::class.java)
                if (isPlaying == true) {
                    if (!ringtone.isPlaying) {
                        ringtone.play()
                    }
                } else {
                    if (ringtone.isPlaying) {
                        ringtone.stop()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        databaseRef.addValueEventListener(ringtoneStatusListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }
}