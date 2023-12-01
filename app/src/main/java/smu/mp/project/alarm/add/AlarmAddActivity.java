package smu.mp.project.alarm.add;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import smu.mp.project.R;
import smu.mp.project.alarm.list.AlarmFragment;
import smu.mp.project.alarm.list.AlarmItem;
import smu.mp.project.alarm.list.AlarmItemBuilder;

public class AlarmAddActivity extends AppCompatActivity implements View.OnClickListener {

    final int REQUEST_CODE_BASIC_SOUND = 1000;
    private static final int RINGTONE_PICKER_REQUEST_CODE = 1;

    Intent preIntent;
    AlarmItem newAlarm, updateAlarm;
    int dayTrue, dayFalse;
    String REQUEST_STATE, day;
    int alarmHour, alarmMinute, alarmVolume;
    TimePicker timePicker;
    EditText title;
    Switch allDaySwitch, basicSoundSwitch, earSoundSwitch, vibSwitch;
    boolean allDayFlag, basicSoundFlag, earSoundFlag, vibFlag;

    Button[] dayButton = new Button[8];
    boolean[] dayArr = new boolean[8];
    Button alarmsoundButton, saveButton, cancelButton;
    SeekBar volume;
    String alarmSoundUri, alarmSoundName;

    Uri selectedRingtoneUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alarm_add);

        newAlarm = new AlarmItem();

        dayTrue = getResources().getColor(R.color.mainColor);
        dayFalse = getResources().getColor(R.color.light_grey);
        alarmVolume = 100;
        alarmSoundUri="content://settings/system/ringtone";

        setTimePicker();
        setRingtonePicker();
        setObjectView();
        setVolumeChanged();

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        for (int i=1; i<dayButton.length; i++){
            dayButton[i].setOnClickListener(this);
        }

        allDaySwitch.setOnCheckedChangeListener(new switchListener());
        basicSoundSwitch.setOnCheckedChangeListener(new switchListener());
        earSoundSwitch.setOnCheckedChangeListener(new switchListener());
        vibSwitch.setOnCheckedChangeListener(new switchListener());

        preIntent = getIntent();
        REQUEST_STATE = preIntent.getStringExtra("REQUEST_STATE");

        if (REQUEST_STATE.equals("update")){
            updateAlarm = (AlarmItem) preIntent.getSerializableExtra("alarmItem");
            setAlarmView();
        }
    }

    public void setAlarmView(){
        timePicker.setCurrentHour(updateAlarm.getHour());
        timePicker.setCurrentMinute(updateAlarm.getMinute());
        title.setText(updateAlarm.getTitle());
        allDayFlag = updateAlarm.isAllDayFlag();
        allDaySwitch.setChecked(allDayFlag);
        if (!allDayFlag){
            setDayColumn();
        }
        volume.setProgress(updateAlarm.getVolume());
        basicSoundSwitch.setChecked(updateAlarm.isBasicSoundFlag());
        earSoundSwitch.setChecked(updateAlarm.isEarSoundFlag());
        vibSwitch.setChecked(updateAlarm.isVibFlag());
    }

    public void setAlarm(){
        day = setDayString();
        Log.d("AlarmAddActivity", "setAlarm() called");  // 이 줄을 추가해보세요.

        newAlarm = new AlarmItemBuilder()
                .setHour(alarmHour)
                .setMinute(alarmMinute)
                .setTitle(title.getText().toString())
                .setAllDayFlag(allDayFlag)
                .setDay(day)
                .setVolume(alarmVolume)
                .setAlarmSoundUri(alarmSoundUri)
                .setAlarmSoundName(alarmSoundName)
                .setBasicSoundFlag(basicSoundFlag)
                .setEarSoundFlag(earSoundFlag)
                .setVibFlag(vibFlag)
                .build();

        // 추가된 로그
        if (newAlarm == null) {
            Log.e("AlarmAddActivity", "newAlarm is null!");
        } else {
            Log.d("AlarmAddActivity", "New Alarm: " + newAlarm.toString());
            Log.d("AlarmAddActivity", "Alarm Sound URI: " + newAlarm.getAlarmSoundUri());
        }
    }

    public void setDayColumn(){
        day = updateAlarm.getDay();
        if (!day.equals("")){
            String[] daySplit = day.split(",");
            for (int i=0; i <daySplit.length; i++){
                clickDayButton(Integer.parseInt(daySplit[i]));
            }
            if (daySplit.length == 7) {
                allDaySwitch.setChecked(true);
                allDayFlag = true;
            }
        }
    }

    public String setDayString(){
        String day = "";
        for (int i=1; i<dayArr.length; i++){
            if (dayArr[i]){
                day += i + ",";
            }
        }
        if (day.length() >0){
            day = day.substring(0, day.length()-1);
        }
        return day;
    }

    public void clickDayButton(int i){
        dayButton[i].setBackgroundColor(dayArr[i] ? dayFalse : dayTrue);
        dayArr[i] = !dayArr[i];

        for (int j=1; j<dayArr.length; j++){
            if (!dayArr[j]){
                allDaySwitch.setChecked(false);
                allDayFlag = false;
                return;
            }
        }
        allDaySwitch.setChecked(true);
        allDayFlag = true;
    }

    public void setVolumeChanged(){
        alarmVolume = 100;
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alarmVolume = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.saveButton){
            setAlarm();
            if (REQUEST_STATE.equals("update")){
                newAlarm.setId(updateAlarm.getId());
            }
            Log.d("AlarmAddActivity", "Before setResult - New Alarm: " + newAlarm);

            Intent data = new Intent();
            data.putExtra("alarmItem", newAlarm);
            setResult(RESULT_OK, data);

            Log.d("AlarmAddActivity", "Saved Alarm: " + newAlarm.toString());

            finish();
        } else if (viewId == R.id.cancelButton) {
            showDialogCheckCancel();
        } else if (viewId == R.id.mon) {
            clickDayButton(2);
        } else if (viewId == R.id.tue) {
            clickDayButton(3);
        } else if (viewId == R.id.wen) {
            clickDayButton(4);
        } else if (viewId == R.id.thu) {
            clickDayButton(5);
        } else if (viewId == R.id.fri) {
            clickDayButton(6);
        } else if (viewId == R.id.sat) {
            clickDayButton(7);
        }
    }

    public void setTimePicker(){
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        alarmHour = timePicker.getCurrentHour();
        alarmMinute = timePicker.getCurrentMinute();
        timePicker.setOnTimeChangedListener(new timeChangedListener());
    }

    public void setRingtonePicker(){
        alarmsoundButton = findViewById(R.id.ringtoneButton);
        alarmsoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent soundIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                soundIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                soundIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);
                soundIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람음 선택");
                startActivityForResult(soundIntent, RINGTONE_PICKER_REQUEST_CODE);
            }
        });
    }

    public void setObjectView(){
        title = findViewById(R.id.title);

        allDaySwitch = findViewById(R.id.allDaySwitch);
        dayButton[1] = findViewById(R.id.sun);
        dayButton[2] = findViewById(R.id.mon);
        dayButton[3] = findViewById(R.id.tue);
        dayButton[4] = findViewById(R.id.wen);
        dayButton[5] = findViewById(R.id.thu);
        dayButton[6] = findViewById(R.id.fri);
        dayButton[7] = findViewById(R.id.sat);

        volume = findViewById(R.id.volume);
        basicSoundSwitch = findViewById(R.id.basicSoundSwitch);
        earSoundSwitch = findViewById(R.id.earphoneSoundSwitch);
        vibSwitch = findViewById(R.id.vibSwitch);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    public class switchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int viewId = buttonView.getId();
            if (viewId == R.id.allDaySwitch) {
                daySwitch(isChecked);
            } else if (viewId == R.id.basicSoundSwitch) {
                basicSoundSwitchAction(isChecked);
            } else if (viewId == R.id.earphoneSoundSwitch) {
                earSoundSwitchAction(isChecked);
            } else if (viewId == R.id.vibSwitch) {
                vibSwitchAction(isChecked);
            }
        }
    }

    private void basicSoundSwitchAction(boolean isChecked) {
        // basicSoundSwitch 상태에 따라 수행할 동작 구현
    }

    private void earSoundSwitchAction(boolean isChecked) {
        // earSoundSwitch 상태에 따라 수행할 동작 구현
    }

    private void vibSwitchAction(boolean isChecked) {
        // vibSwitch 상태에 따라 수행할 동작 구현
    }


    public void daySwitch(boolean isChecked){
        if (isChecked){
            for (int i=1;i<dayButton.length;i++){
                dayButton[i].setBackgroundColor(dayTrue);
                allDayFlag = true;
                dayArr[i] = true;
            }
        } else {
            for (int i=1; i<dayArr.length; i++){
                if (!dayArr[i]){
                    return;
                }
            }
            for (int i=1; i<dayButton.length; i++){
                dayButton[i].setBackgroundColor(dayFalse);
                allDayFlag = false;
                dayArr[i] = false;
            }
        }
    }

    public class timeChangedListener implements TimePicker.OnTimeChangedListener{
        @Override
        public void onTimeChanged(TimePicker view, int hour, int minute) {
            alarmHour = hour;
            alarmMinute = minute;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            saveSelectedRingtoneUri(ringtoneUri);

            String ringtoneName = getRingtoneName(ringtoneUri);

            saveSelectedRingtone(ringtoneName, ringtoneUri);

            // 선택한 알람음을 텍스트뷰에 표시
            showSelectedRingtone(ringtoneName);
        }
    }

    private String getRingtoneName(Uri ringtoneUri) {
        if (ringtoneUri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            if (ringtone != null) {
                return ringtone.getTitle(this);
            }
        }
        return "기본 알람음";
    }

    private void saveSelectedRingtone(String ringtoneName, Uri ringtoneUri) {
        this.alarmSoundName = ringtoneName;
        this.selectedRingtoneUri = ringtoneUri;
    }
    private void showSelectedRingtone(String ringtoneName){
        TextView alarmSoundNameTextView = findViewById(R.id.ringtoneText);
        alarmSoundNameTextView.setText("선택한 알람음: " + ringtoneName);
    }
    private void saveSelectedRingtoneUri(Uri ringtoneUri){
        if (ringtoneUri != null){
            this.selectedRingtoneUri = ringtoneUri;
        }
    }

    public void showDialogCheckCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CANCEL")
                .setMessage("알람 생성 및 수정을 취소하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(true);
        builder.setNegativeButton("아니오", null);
        builder.show();
    }




}