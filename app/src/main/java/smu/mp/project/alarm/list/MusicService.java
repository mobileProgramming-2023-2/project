package smu.mp.project.alarm.list;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import smu.mp.project.R;

public class MusicService extends Service {
    IBinder binder = new MusicServiceBinder();

    public class MusicServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp = MediaPlayer.create(this, R.raw.hello);
        mp.setLooping(true);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        //Toast.makeText(getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(getApplicationContext(), "서비스가 종료됨", Toast.LENGTH_SHORT).show();
        mp.stop();
        super.onDestroy();
    }

    public void play() {
        //Toast.makeText(getApplicationContext(), "음악을 재생", Toast.LENGTH_SHORT).show();
        mp.start();
    }

    public void pause() {
        //Toast.makeText(getApplicationContext(), "음악을 일시정지", Toast.LENGTH_SHORT).show();
        mp.pause();
    }
}
