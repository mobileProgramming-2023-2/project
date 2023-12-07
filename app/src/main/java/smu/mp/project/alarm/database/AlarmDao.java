package smu.mp.project.alarm.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import smu.mp.project.alarm.list.AlarmItem;


@Dao
public interface AlarmDao {
    @Insert
    Completable insert(AlarmItem alarm);

    @Update
    Completable update(AlarmItem alarm);


    @Delete
    Completable delete(AlarmItem alarm);

    @Query("SELECT * FROM alarm_table ORDER BY hour, minute ASC")
    LiveData<List<AlarmItem>> getAllAlarms();

    @Query("SELECT * FROM alarm_table")
    Observable<List<AlarmItem>> getAllAlarmsFromService();

    @Query("SELECT * FROM alarm_table WHERE id = :id")
    Single<AlarmItem> getAlarm(int id);

}