package smu.mp.project.alarm.list;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class AlarmItem implements Serializable {
//    private String time;
//
//    public AlarmItem(String time) {
//        this.time = time;
//    }
//
//    public String getTime() {
//        return this.time;
//    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int hour;
    private int minute;
    private String title;
    private boolean totalFlag = true;
    private boolean allDayFlag;
    private String day = "";
    private int volume;
    private String alarmSoundUri = "";
    private String alarmSoundName = "";
    private boolean basicSoundFlag;
    private boolean earSoundFlag;
    private boolean vibFlag;
    public AlarmItem() {}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTotalFlag() {
        return totalFlag;
    }

    public void setTotalFlag(boolean totalFlag) {
        this.totalFlag = totalFlag;
    }

    public boolean isAllDayFlag() {
        return allDayFlag;
    }

    public void setAllDayFlag(boolean allDayFlag) {
        this.allDayFlag = allDayFlag;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
    public String getAlarmSoundUri() {
        return alarmSoundUri;
    }
    public void setAlarmSoundUri(String alarmSoundUri) {
        this.alarmSoundUri = alarmSoundUri;
    }
    public String getAlarmSoundName() {
        return alarmSoundName;
    }
    public void setAlarmSoundName(String alarmSoundName) {
        this.alarmSoundName = alarmSoundName;
    }

    public boolean isBasicSoundFlag() {
        return basicSoundFlag;
    }

    public void setBasicSoundFlag(boolean basicSoundFlag) {
        this.basicSoundFlag = basicSoundFlag;
    }



    public boolean isEarSoundFlag() {
        return earSoundFlag;
    }

    public void setEarSoundFlag(boolean earSoundFlag) {
        this.earSoundFlag = earSoundFlag;
    }

    public boolean isVibFlag() {
        return vibFlag;
    }

    public void setVibFlag(boolean vibFlag) {
        this.vibFlag = vibFlag;
    }


    @Override
    public String toString() {
        return "AlarmItem{" +
                "id=" + id +
                ", hour=" + hour +
                ", minute=" + minute +
                ", title='" + title + '\'' +
                ", totalFlag=" + totalFlag +
                ", allDayFlag=" + allDayFlag +
                ", day='" + day + '\'' +
                ", volume=" + volume +
                ", alarmSoundUri" + alarmSoundUri + '\'' +
                ", alarmSoundName" + alarmSoundName + '\'' +
                ", basicSoundFlag=" + basicSoundFlag +
                ", earSoundFlag=" + earSoundFlag +
                ", vibFlag=" + vibFlag +
                '}';
    }
}

