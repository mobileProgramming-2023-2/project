package smu.mp.project.alarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;


import smu.mp.project.alarm.list.AlarmItem;

public class AlarmManagerActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SCHEDULE_EXACT_ALARM = 123;
    private final long INTERVAL_TIME = 24 * 60 * 60 * 1000;
    AlarmManager alarmManager;
    Calendar calendar;
    Intent preIntent, receiverIntent;
    AlarmItem alarmItem;
    String REQUEST_STATE;
    Context context;
    long alarmTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preIntent = getIntent();
        REQUEST_STATE = preIntent.getStringExtra("request");
        alarmItem = (AlarmItem) preIntent.getSerializableExtra("alarmItem");
        context = getApplicationContext();

        receiverIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService((ALARM_SERVICE));

        switch (REQUEST_STATE){
            case "reboot":
                if (!checkOnAlarm())
                    checkAndRequestPermission(); // 알람이 설정되어 있지 않은 경우에만 권한을 확인하고 요청
                setAlarmManager();
                break;
            case "create":
                checkAndRequestPermission(); // 알람을 설정하는 경우에 권한을 확인하고 요청
                setAlarmManager();
                break;
            case "cancel":
                if (checkOnAlarm())
                    cancleAlarm();
        }
        finish();
    }

    public boolean checkOnAlarm(){
        PendingIntent checkIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkIntent = PendingIntent.getBroadcast(context,
                    alarmItem.getId(),
                    receiverIntent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        } else {
            checkIntent = PendingIntent.getBroadcast(context,
                    alarmItem.getId(),
                    receiverIntent,
                    PendingIntent.FLAG_NO_CREATE);
        }

        return checkIntent != null;
    }



    public void setAlarmManager(){
        setCalendar();
        requestReceiver(alarmItem.getId());
    }

    public void setCalendar(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmItem.getHour());
        calendar.set(Calendar.MINUTE, alarmItem.getMinute());
        calendar.set(Calendar.SECOND,0);
        alarmTime = calendar.getTimeInMillis();
        receiverIntent.putExtra("alarmId", alarmItem.getId());

        if (alarmTime <= System.currentTimeMillis())
            alarmTime += INTERVAL_TIME;
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                        MY_PERMISSIONS_REQUEST_SCHEDULE_EXACT_ALARM);
            } else {
                // Permission has already been granted
                // 알람 설정 등의 작업을 수행
            }
        } else {
            // API 23미만에서는 권한이 필요하지 않음
            // 알람 설정 등의 작업을 수행
        }
    }


    public void requestReceiver(int requestCode){
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context,
                    requestCode,
                    receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context,
                    requestCode,
                    receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상
            checkAndRequestPermission(); // 알람을 설정하는 경우에 권한을 확인하고 요청
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent);
        } else {
            // API 23미만
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    alarmTime, pendingIntent);
        }
    }


    public void cancleAlarm(){
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context,
                    alarmItem.getId(),
                    receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context,
                    alarmItem.getId(),
                    receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}