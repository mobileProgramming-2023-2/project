package smu.mp.project.alarm.list;

public class AlarmItemBuilder {
    AlarmItem alarmItem;

    public AlarmItemBuilder(){
        alarmItem = new AlarmItem();
    }

    public AlarmItemBuilder setId(int id){
        alarmItem.setId(id);
        return this;
    }

    public AlarmItemBuilder setHour(int hour){
        alarmItem.setHour(hour);
        return this;
    }

    public AlarmItemBuilder setMinute(int minute) {
        alarmItem.setMinute(minute);
        return this;
    }
    public AlarmItemBuilder setTitle(String title) {
        alarmItem.setTitle(title);
        return this;
    }
    public AlarmItemBuilder setTotalFlag(boolean totalFlag) {
        alarmItem.setTotalFlag(totalFlag);
        return this;
    }
    public AlarmItemBuilder setAllDayFlag(boolean allDayFlag) {
        alarmItem.setAllDayFlag(allDayFlag);
        return this;
    }
    public AlarmItemBuilder setDay(String day) {
        alarmItem.setDay(day);
        return this;
    }

    public AlarmItemBuilder setVolume(int volume) {
        alarmItem.setVolume(volume);
        return this;
    }

    public AlarmItemBuilder setAlarmSoundUri(String alarmSoundUri){
        alarmItem.setAlarmSoundUri(alarmSoundUri);
        return this;
    }

    public AlarmItemBuilder setAlarmSoundName(String alarmSoundName){
        alarmItem.setAlarmSoundName(alarmSoundName);
        return this;
    }
    public AlarmItemBuilder setBasicSoundFlag(boolean basicSoundFlag) {
        alarmItem.setBasicSoundFlag(basicSoundFlag);
        return this;
    }
    public AlarmItemBuilder setEarSoundFlag(boolean earSoundFlag) {
        alarmItem.setEarSoundFlag(earSoundFlag);
        return this;
    }
    public AlarmItemBuilder setVibFlag(boolean vibFlag) {
        alarmItem.setVibFlag(vibFlag);
        return this;
    }


    public AlarmItem build() {
        return this.alarmItem;
    }
}