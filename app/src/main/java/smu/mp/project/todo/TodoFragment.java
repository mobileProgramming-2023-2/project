package smu.mp.project.todo;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.Calendar;

import smu.mp.project.R;

public class TodoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        
        // (+)버튼 클릭시 할 일 입력 Dialog 표시
        ImageButton addItemButton = rootView.findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return rootView;
    }

    // 할 일 입력 Dialog 표시 메소드
    private void showDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_todo, null);

        final EditText editTextTodoTitle = dialogView.findViewById(R.id.editTextTodoTitle);
        final EditText editTextTodoDescription = dialogView.findViewById(R.id.editTextTodoMemo);
        final TextView textViewStartTime = dialogView.findViewById(R.id.textViewStartTime);
        final TextView textViewEndTime = dialogView.findViewById(R.id.textViewEndTime);

        // Dialog 생성
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();

        // 닫기(X) 버튼 설정
        ImageButton closeButton = dialogView.findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 확인(체크) 버튼 설정
        ImageButton checkButton = dialogView.findViewById(R.id.buttonCheck);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 할 일의 제목과 설명을 가져옵니다.
                String title = editTextTodoTitle.getText().toString();
                String description = editTextTodoDescription.getText().toString();
                // TODO: 여기에 할 일을 저장하는 로직을 구현합니다.
                // 예를 들어, 데이터베이스에 저장하거나, API로 서버에 업로드합니다.

                dialog.dismiss();
            }
        });

        // 시작 시간 클릭 리스너
        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(textViewStartTime);
            }
        });
        
        // 종료 시간 클릭 리스너
        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(textViewEndTime);
            }
        });

        dialog.show();
    }

    // 시간 설정 TimePickerDialog 표시 메소드
    private void showTimePickerDialog(final TextView timeTextView) {
        // 현재 시간을 기본 값으로 사용
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 사용자가 선택한 시간을 TextView에 설정
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedTime.getTime());
                        timeTextView.setText(formattedTime);
                    }
                }, hour, minute, false);

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        timePickerDialog.show();
    }

}
