package smu.mp.project.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.Calendar;

import smu.mp.project.R;
import smu.mp.project.alarm.list.AlarmItem;
import smu.mp.project.alarm.list.MusicService;

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


    // 서비스 바인딩 시 호출되는 메서드
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // 서비스 생성 시 호출되는 메서드, 알람 설정 및 wake lock 획득
    @Override
    public void onCreate() {
        super.onCreate();
        setNotification();
        acquireWakeLock();
        startForeground(SERVICE_ID, notification);
    }

    MusicService mService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) service;
            mService = binder.getService();
            mService.play(); // 서비스가 연결되면 음악 재생
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    // 서비스 시작 시 호출되는 메서드, 알람 설정 및 음악 서비스 시작
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmItem = (AlarmItem) intent.getSerializableExtra("alarmItem");
        if (alarmItem == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        basicFlag = alarmItem.isBasicSoundFlag();
        earFlag = alarmItem.isEarSoundFlag();

        // 이어폰 연결 상태 확인
        boolean isEarphoneConnected = isWiredHeadsetConnected();

        // 이어폰이 연결되어 있으면 earFlag를 true로 설정
        if (isEarphoneConnected) {
            earFlag = true;
            Intent service = new Intent(AlarmService.this, MusicService.class);
            startService(service);
            bindService(service, conn, BIND_AUTO_CREATE);
        }

        // 기존 로직 유지
        if (!earFlag && alarmItem.getAlarmSoundUri() != null) {
            startAlarm(alarmItem.getAlarmSoundUri());
        }
        checkAlarmDayWithToday();

        return START_NOT_STICKY;
    }

    private boolean isWiredHeadsetConnected() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

    private boolean isBound = false;

    // 일반 알람 소리 시작 메서드
    private void startAlarm(String alarmSoundUri) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(this, Uri.parse(alarmSoundUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.prepare(); // prepare는 MediaPlayer.create를 사용할 경우 필요하지 않음
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }

    // 설정된 알람 요일과 현재 요일을 비교하여 알람 여부 결정
    public void checkAlarmDayWithToday() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        String alarmDay = alarmItem.getDay();

        if (earFlag) {
            // earFlag가 true이면 바로 intentAlarmOnActivity를 호출
            intentAlarmOnActivity();
            return;
        }

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

    // 알람 시작 스레드, AudioManager 설정 및 알람 소리 재생
    public void startAlarmThread() {
        new Thread(() -> {
            setAudioManager();
            if (alarmItem.isVibFlag()) {
                setVibrate();
            } else {
                // 진동이 설정되어 있지 않은 경우에만 소리를 재생
                if (basicFlag && !earFlag) {
                    startRingtone(alarmItem.getAlarmSoundUri());
                }
            }
            intentAlarmOnActivity();
        }).start();
    }


    // 알람 소리 볼륨 조절 메서드
    public void setAudioManager(){
        if (!basicFlag && !earFlag) return;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int volume = alarmItem.getVolume();

        if (earFlag) { }
        else {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                    maxVol * volume / 100,
                    AudioManager.FLAG_PLAY_SOUND);
        }
    }


    // MediaPlayer를 사용해 알람 소리 재생
    private void startRingtone(String alarmSoundUri) {
        // MediaPlayer 객체 초기화 및 설정
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(alarmSoundUri));
            setMediaPlayerAttributes();
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            // 오류 처리...
        }
    }

    // MediaPlayer의 오디오 속성 설정 메서드
    private void setMediaPlayerAttributes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
    }


    // 알람 활성화 액티비티 시작 메서드
    public void intentAlarmOnActivity() {
        Intent alarmOnIntent = new Intent(this, AlarmOnActivity.class);
        alarmOnIntent.putExtra("alarmItem", alarmItem)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmOnIntent);
    }


    // 알림 설정 메서드, NotificationChannel 생성 및 Builder 초기화
    public void setNotification() {
        Intent intent = new Intent(this, AlarmOnActivity.class);

        // PendingIntent에 FLAG_IMMUTABLE 또는 FLAG_MUTABLE 플래그 추가
        int pendingIntentFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NM.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        setNotificationBuilder(pendingIntent);
    }


    // Notification Builder 설정 메서드
    public void setNotificationBuilder(PendingIntent pendingIntent) {
        builder.setContentTitle("송토끼")
                .setTicker("알람 on")
                .setSmallIcon(R.drawable.img_rabbitsong)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.alert_light_frame, "알람 해제하기", pendingIntent);

        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
    }


    // 진동 시작 메서드
    public void setVibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // 예시 패턴: 0ms 대기, 1000ms 진동, 500ms 대기
            long[] pattern = {0, 1000, 500};
            // -1: 반복 없음, 0 이상: 지정된 인덱스부터 시작하여 반복
            vibrator.vibrate(pattern, 0);
        }
    }


    // 진동 중지 메서드
    public void stopVibrate() {
        if (vibrator != null)
            vibrator.cancel();
    }

    // Wake lock 획득 메서드
    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag"
            );
            wakeLock.acquire();
        }
    }

    // 서비스 종료 시 호출되는 메서드, MediaPlayer 및 진동 중지, wake lock 해제
    @Override
    public void onDestroy() {
        stopMediaPlayer();
        stopVibrate();
        releaseWakeLock();

        if (earFlag) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            stopService(serviceIntent); // Stop MusicService if earFlag is set
        }

        super.onDestroy();
    }

    // Wake lock 해제 메서드
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

    // MediaPlayer 중지 메서드
    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Notification Channel 생성 메서드 (Android O 이상에서 사용)
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
}