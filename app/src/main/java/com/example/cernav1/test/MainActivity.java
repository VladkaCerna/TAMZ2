package com.example.cernav1.test;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private NotificationManager mNotifyMgr;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Boolean decision;
    private static final String NO_ACTION = "http://com.tinbytes.simplenotificationapp.yes_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Toast.makeText(this, "Sensor changed 1", Toast.LENGTH_SHORT).show();

        if (sensorEvent.values[0] == 1) {
            popUpWindowShow();
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not in use
    }

    private void popUpWindowShow() {
        AlertDialog.Builder mLockDialog = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.lock_dialog, null);
        Button mBtnYes = (Button) mView.findViewById(R.id.btnYes);
        Button mBtnNo = (Button) mView.findViewById(R.id.btnNo);
        Button mBtnMaybe = (Button) mView.findViewById(R.id.btnMaybe);

        mLockDialog.setView(mView);
        final AlertDialog dialog = mLockDialog.create();
        dialog.show();

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decision = true;
                dialog.cancel();
                Toast.makeText(MainActivity.this, "Locked", Toast.LENGTH_SHORT).show();
                mSensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decision = false;
                dialog.cancel();
                Toast.makeText(MainActivity.this, "Not locked", Toast.LENGTH_SHORT).show();
                mSensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });
        mBtnMaybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decision = false;
                dialog.cancel();
                Toast.makeText(MainActivity.this, "Notification triggered", Toast.LENGTH_SHORT).show();
                showActionButtonsNotification();
            }
        });
    }

    private void triggerNotification() {
        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this, "notChannel")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private Intent getNotificationIntent() {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return mIntent;
    }

    private void showActionButtonsNotification() {
        Intent yesIntent = getNotificationIntent();
        yesIntent.setAction(NO_ACTION);

        int mNotificationId = 002;
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        processIntentAction(getIntent());

        getSupportActionBar().hide();

        Notification notification = new NotificationCompat.Builder(this, "channel")
                .setContentIntent(PendingIntent.getActivity(this, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Action buttons not rec")
                .setContentTitle("Hi there")
                .setContentText("This is more text")
                .setWhen(0)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_launcher_background,
                        getString(R.string.ano),
                        PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                ))
                .build();

        mNotifyMgr.notify(mNotificationId, notification);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntentAction(intent);
        super.onNewIntent(intent);
    }

    private void processIntentAction(Intent intent) {
        if (intent.getAction() != null) {
            Toast.makeText(this, "Listener reactivated", Toast.LENGTH_SHORT).show();
            mNotifyMgr.cancel(002);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }




}
