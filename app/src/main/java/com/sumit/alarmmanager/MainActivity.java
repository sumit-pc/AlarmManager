package com.sumit.alarmmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ToggleButton alarmToggle;
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);




        alarmToggle = findViewById(R.id.alarmToggle);
        alarmToggle.setChecked(alarmUp);
        alarmToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean isChecked) {
                        String toastMessage = null;
                        if(isChecked) {

                            long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                            long triggerTime = SystemClock.elapsedRealtime() + 5000 ; //+ repeatInterval;

                            // If the Toggle is turned on, set the repeating alarm with
                            // a 15 minute interval.
                            if (alarmManager != null) {
                                alarmManager.setInexactRepeating
                                        (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                                triggerTime, repeatInterval,
                                                notifyPendingIntent);
                                //Set the toast message for the "on" case.
                                toastMessage = "Stand Up Alarm On!";
                            }
                        }else {
                            mNotificationManager.cancelAll();
                            //Set the toast message for the "off" case.
                            toastMessage = "Stand Up Alarm Off!";
                        }

                        //Show a toast to say the alarm is turned on or off.
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        createNotificationChannel();
    }



    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void getTime(View view) {

       AlarmManager.AlarmClockInfo next = alarmManager.getNextAlarmClock();
        if (next!=null)
        {
            long nextAlarmTime = next.getTriggerTime();
            Date dateNow = new Date();
            long diffMilliSec = nextAlarmTime - dateNow.getTime();
            long seconds = diffMilliSec / 1000;
            long minutes = seconds / 60;
            //long hours = minutes / 60;
            Toast.makeText(this, String.valueOf(minutes), Toast.LENGTH_LONG).show();

        }
    }
}
