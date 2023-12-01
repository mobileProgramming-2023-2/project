package smu.mp.project.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import smu.mp.project.alarm.database.AlarmDao;
import smu.mp.project.alarm.database.AlarmDatabase;

public class AlarmReceiver extends BroadcastReceiver {

    AlarmDao alarmDao;
    Context context;
    Intent serviceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int alarmId = intent.getIntExtra("alarmId", 0);

        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(context);
        alarmDao = alarmDatabase.alarmDao();
        serviceIntent = new Intent(context, AlarmService.class);

        getAlarmWithRxJava(alarmId);

        // 알람이 울렸는지 확인하는 로그 추가
        Log.d("AlarmReceiver", "Alarm rang for ID: " + alarmId);
    }

    public void getAlarmWithRxJava(int alarmId) {

        alarmDao.getAlarm(alarmId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarmItem -> {

                    serviceIntent.putExtra("alarmItem", alarmItem);
                    onService();
                });
    }

    public void onService() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
