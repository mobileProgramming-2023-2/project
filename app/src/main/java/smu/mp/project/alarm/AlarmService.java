package smu.mp.project.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Calendar;

import smu.mp.project.R;
import smu.mp.project.alarm.list.AlarmItem;

public class AlarmService extends Service {
    private final String CHANNEL_ID = "OnAlarm";
    private final String CHANNEL_NAME = "OnAlarm";
    final int SERVICE_ID = 1994;
    AudioManager audioManager;
    public MediaPlayer mediaPlayer;
    NotificationManager NM;
    Notification.Builder builder;
    Notification notification;
    AlarmItem alarmItem;
    Vibrator vibrator;
    boolean basicFlag, earFlag;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setNotification();
        startForeground(SERVICE_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//서비스가 호출될 때마다 실행
        alarmItem = (AlarmItem) intent.getSerializableExtra("alarmItem");
        basicFlag = alarmItem.isBasicSoundFlag();
        earFlag = alarmItem.isEarSoundFlag();

        checkAlarmDayWithToday();

        return START_NOT_STICKY;
    }

    //설정된 알람 요일과 현재 요일을 비교하여 알람을 울리지 결정하는 method
    public void checkAlarmDayWithToday() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        String alarmDay = alarmItem.getDay();
        if (alarmDay.equals("")){
            startAlarmThread();
        } else {
            if (alarmItem.isAllDayFlag() || alarmDay.contains(Integer.toString(today))){
                startAlarmThread();
            } else {
                stopForeground(true);
            }
        }
    }

    //알람이 울릴 때 실행되는 Thread, AudioManager를 설정하고 Ringtone을 재생
    public void startAlarmThread() {
        new Thread(() -> {
            setAudioManager();
            startRingtone();
            intentAlarmOnActivity();
        }).start();
        if (alarmItem.isVibFlag()){
            setVibrate();
        }
    }

    // 알람 소리의 볼륨을 조절한다.
    public void setAudioManager(){
        if (!basicFlag && !earFlag) return;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int volume = alarmItem.getVolume();

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                maxVol * volume / 100,
                AudioManager.FLAG_PLAY_SOUND);
    }

    //MediaPlayer를 사용하여 알람 소리를 재생한다.
    public void startRingtone(){
        if (!basicFlag && !earFlag) return;

        Uri alarmSoundUri = Uri.parse(alarmItem.getAlarmSoundUri());

        try {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), alarmSoundUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
            });
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                mediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // AlarmOnActivity를 시작하여 사용자에게 알람을 알리는 화면을 표시한다.
    public void intentAlarmOnActivity() {
        Intent alarmOnIntent = new Intent(this, AlarmOnActivity.class);
        alarmOnIntent.putExtra("alarmItem", alarmItem)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmOnIntent);
    }

    //Notification을 설정하는 메서드로, NotificationChannel을 생성하고 Builder를 초기화 한다.
    public void setNotification() {
        Intent intent = new Intent(this, AlarmOnActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 26) {
            NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NM.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        setNotificationBuilder(pendingIntent);
    }

    public void setNotificationBuilder(PendingIntent pendingIntent) {
        builder.setContentTitle("토끼송")
                .setTicker("알람 on")
                .setSmallIcon(R.drawable.img_rabbitsong)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.alert_light_frame, "알람 해제하기", pendingIntent);

        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    // 진동을 시작하는 Method
    public void setVibrate() {
        new Thread(() -> {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000, 1000, 1000};
            int REPEAT_VIBRATE = 0; // 0:반복, -1:반복x

            vibrator.vibrate(pattern, REPEAT_VIBRATE);
        }).start();
    }

    public void stopVibrate() {
        if (vibrator != null)
            vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        // 서비스 종료 시, 호출
        stopMediaPlayer();
        stopVibrate();
        releaseWakeLock();
        super.onDestroy();
    }

    private void releaseWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag"
            );
            wakeLock.release();
        }
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "AlarmItem";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setSound(null, null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelId;
    }
    private Uri getSelectedRingtoneUri(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ringtoneUriString = preferences.getString("selected_ringtone_uri", null);

        if (ringtoneUriString != null){
            return Uri.parse(ringtoneUriString);
        }else {
            return null;
        }
    }
}
