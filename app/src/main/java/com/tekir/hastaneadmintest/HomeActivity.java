package com.tekir.hastaneadmintest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    TextView userNameTextView,userEmailTextView,userJobTextView,alarmCheckTxt,alarmMessageTxt;
    Button alarmCheckBtn,serviceStartBtn;
    String userName,userEmail,userJob,alarmCheck,alarmMessage;
    ProgressDialog progressDialog;
    static final int ALARM_REQ_CODE = 100;

    /*public void onBackPressed(){
        finishAffinity();
        return;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userJobTextView = findViewById(R.id.userJobTextView);
        alarmCheckTxt = findViewById(R.id.alarmCheckTxt);
        alarmCheckBtn = findViewById(R.id.alarmCheckBtn);
        alarmMessageTxt = findViewById(R.id.alarmMessageTxt);

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser;
        mUser = mAuth.getCurrentUser();

        alarmCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore.collection("Users").document(mUser.getEmail()).update("userAlarm",false);
                if (MyReceiver.mp != null){
                    MyReceiver.mp.stop();

                    mFirestore.collection("Alarm Message").document("Message").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    alarmMessage = document.get("Message").toString();
                                    alarmMessageTxt.setText(alarmMessage);
                                }
                            } else {
                                Log.d("Hata!", "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        mFirestore.collection("Users").whereEqualTo("userEmail", mUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("HATA!", "Listen failed.", error);
                            return;
                        }
                        for (QueryDocumentSnapshot document : value) {
                            userName = document.get("userName").toString();
                            userEmail = document.get("userEmail").toString();
                            userJob = document.get("userJob").toString();
                            alarmCheck = document.get("userAlarm").toString();

                            userNameTextView.setText(userName);
                            userEmailTextView.setText(userEmail);
                            userJobTextView.setText(userJob);
                            alarmCheckTxt.setText(alarmCheck);

                            playAlarm();
                        }

                        if (progressDialog.isShowing()) ;
                        progressDialog.dismiss();
                    }
                });
    }

    public void playAlarm(){
        startService(new Intent(getApplication(), MyStartedService.class));
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(alarmCheck == "true") {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            long triggerTime = System.currentTimeMillis();
            Intent iBroadCast = new Intent(HomeActivity.this,MyReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(HomeActivity.this,ALARM_REQ_CODE,iBroadCast,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime,pi);
        }
    }
}