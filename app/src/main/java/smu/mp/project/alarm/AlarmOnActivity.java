package smu.mp.project.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import smu.mp.project.R;
import smu.mp.project.alarm.database.AlarmViewModel;
import smu.mp.project.alarm.list.AlarmItem;

public class AlarmOnActivity extends AppCompatActivity {

    TextView day, time;
    Button stop;
    AlarmItem alarmItem;
    int deviceHeight, deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_on);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setView();

        Intent preIntent = getIntent();
        alarmItem = (AlarmItem) preIntent.getSerializableExtra("alarmItem");
        setTimeView();
    }

    public void getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int density = (int) getResources().getDisplayMetrics().density;

        deviceHeight = outMetrics.heightPixels;
        deviceWidth = outMetrics.widthPixels;
    }

    public void setView() {
        day = findViewById(R.id.day);
        time = findViewById(R.id.time);
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(active -> {
            changeAlarmTotalFlag();
            stopAlarmAndFinishApp();
        });

        getDisplaySize();
    }

    public void changeAlarmTotalFlag() {
        if (alarmItem.getDay().equals("")) {
            AlarmViewModel alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
            alarmItem.setTotalFlag(false);
            alarmViewModel.update(alarmItem);
        } else {
            setRepeatAlarm();
        }
    }

    public void setRepeatAlarm() {
        Intent alarmIntent = new Intent(this, AlarmManagerActivity.class);
        alarmIntent.putExtra("alarmItem", alarmIntent);
        alarmIntent.putExtra("request", "create");
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmIntent);
    }

    public void stopAlarmAndFinishApp() {
        Intent serviceIntent = new Intent(this, AlarmService.class);
        stopService(serviceIntent);

        moveTaskToBack(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { finishAndRemoveTask(); }
        else { finish(); }
    }

    public void setTimeView() {
        String[] timeArr = changeUTCtoDate().split(" ");
        String dayStr = timeArr[0] + " " + timeArr[1] + " (" + timeArr[2].charAt(0) + ")";
        String timeStr = timeArr[3];

        day.setText(dayStr);
        time.setText(timeStr);
    }

    // UTC를 date로 변환
    // 2021-01-04 05:00:00
    public String changeUTCtoDate() {
        long dt = System.currentTimeMillis();
        Date date = new Date(dt);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM월 dd일 EE요일 HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));

        return sdf.format(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}