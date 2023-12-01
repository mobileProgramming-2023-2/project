package smu.mp.project.alarm.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import smu.mp.project.alarm.list.AlarmItem;

public class AlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private LiveData<List<AlarmItem>> allAlarms;

    public AlarmViewModel(Application application){
        super(application);
        alarmRepository = new AlarmRepository(application);
        allAlarms = alarmRepository.getAllAlarms();
    }
    public void insert(AlarmItem alarmItem) {
        alarmRepository.insert(alarmItem);
    }
    public void update(AlarmItem alarmItem) {
        alarmRepository.update(alarmItem);
    }
    public void delete(AlarmItem alarmItem) {
        alarmRepository.delete(alarmItem);
    }
    public LiveData<List<AlarmItem>> getAllAlarms(){
        return allAlarms;
    }
    public Single<AlarmItem> getAlarm(int id){
        return alarmRepository.getAlarm(id);
    }
    public Observable<List<AlarmItem>> getAllAlarmsFromService(){
        return alarmRepository.getAllAlarmsFromService();
    }
}
