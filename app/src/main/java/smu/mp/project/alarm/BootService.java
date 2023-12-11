package smu.mp.project.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import smu.mp.project.R;
import smu.mp.project.alarm.database.AlarmDao;
import smu.mp.project.alarm.database.AlarmDatabase;
import smu.mp.project.alarm.list.AlarmItem;

public class BootService extends Service {
    private final String CHANNEL_ID = "BootAlarm";
    private final String CHANNEL_NAME = "BootAlarm";
    private final int SERVICE_ID = 1993;
    private final long INTERVAL_TIME = 24 * 60 * 60 * 1000;
    long alarmTime;
    NotificationManager NM;
    Notification.Builder builder;
    Notification notification;
    Context context;
    Intent receiverIntent;
    AlarmManager alarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setNotification();
        startForeground(SERVICE_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(getApplicationContext());
        AlarmDao alarmDao = alarmDatabase.alarmDao();

        alarmDao.getAllAlarmsFromService()
                .subscribe(alarmItems -> {
                    new Thread(() -> {
                        if (alarmItems.size() > 0){
                            resetAlarms(alarmItems);
                        }
                        stopService(intent);
                    }).start();
                });
        return START_NOT_STICKY;
    }

    public void resetAlarms(List<AlarmItem> alarmItems){
        receiverIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for (AlarmItem alarmItem: alarmItems){
            if (alarmItem.isTotalFlag()){
                setCalendar(alarmItem);
                requestReceiver(alarmItem.getId());
            }
        }
    }

    public void setCalendar(AlarmItem alarmItem){
        receiverIntent.putExtra("alarmId", alarmItem.getId());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmItem.getHour());
        calendar.set(Calendar.MINUTE, alarmItem.getMinute());
        calendar.set(Calendar.SECOND, 0);

        alarmTime = calendar.getTimeInMillis();
        if (alarmTime <= System.currentTimeMillis())
            alarmTime += INTERVAL_TIME;
    }

    public void requestReceiver(int requestCode){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                requestCode,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent);
        }
    }

    public void setNotification(){
        if (Build.VERSION.SDK_INT >= 26){
            NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NM.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        setNotificationBuilder();
    }

    public void setNotificationBuilder(){
        builder.setContentTitle("송토끼")
                .setContentText("알람 재부팅 중...")
                .setSmallIcon(R.drawable.img_rabbitsong);
        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
    }
}