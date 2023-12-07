package smu.mp.project.alarm.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import smu.mp.project.alarm.list.AlarmItem;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<AlarmItem>> allAlarms;

    public AlarmRepository(Application application){
        AlarmDatabase database = AlarmDatabase.getDatabase(application);
        alarmDao = database.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    public LiveData<List<AlarmItem>> getAllAlarms(){
        return allAlarms;
    }

    public Observable<List<AlarmItem>> getAllAlarmsFromService() {
        return alarmDao.getAllAlarmsFromService()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<AlarmItem> getAlarm(int id){
        return alarmDao.getAlarm(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void insert(AlarmItem alarmItem) {
        alarmDao.insert(alarmItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void update(AlarmItem alarmItem) {
        alarmDao.update(alarmItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void delete(AlarmItem alarmItem) {
        alarmDao.delete(alarmItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}