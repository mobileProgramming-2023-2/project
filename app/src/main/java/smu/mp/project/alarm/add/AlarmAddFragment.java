package smu.mp.project.alarm.add;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import smu.mp.project.R;
import smu.mp.project.alarm.list.AlarmFragment;
import smu.mp.project.alarm.list.AlarmItem;

public class AlarmAddFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_add, container, false);

        EditText editAlarm = view.findViewById(R.id.title);
        Button btnSave = view.findViewById(R.id.saveButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //알람을 설정하고, 설정된 알람 정보를 가져옴
                String alarmTime = editAlarm.getText().toString();

                //부모 프래그먼트가 AlarmFragment인지 확인
                if (getParentFragment() instanceof AlarmFragment) {
                    AlarmFragment alarmFragment = (AlarmFragment) getParentFragment();


                    // 알람 아이템을 생성하고 리스트에 추가
                    if (!alarmTime.isEmpty()) {
                        AlarmItem alarmItem = new AlarmItem(alarmTime);
                        alarmFragment.addItem(alarmItem);
                    }
                }
                // 이전 화면으로 돌아감
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}