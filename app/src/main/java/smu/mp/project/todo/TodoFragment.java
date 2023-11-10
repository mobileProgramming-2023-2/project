package smu.mp.project.todo;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import smu.mp.project.R;

//TODO:  시간 설정 예외처리 / 할 일 항목 디자인 / 체크박스 및 체크시 줄 / 할 일 삭제 / 할 일 추가 안됨 오류 / 할 일 누르면 다이얼로그 / 캘린더 커스텀

// 할 일 목록 관리하는 UI Fragment
public class TodoFragment extends Fragment {
    private static final String PREFS_TODO = "prefs_todo";
    private static final String KEY_TODO_LIST = "todo_list";
    private List<TodoItem> todoItems;  // 할 일 항목 저장하는 리스트
    private TodoAdapter adapter;  // 할 일 목록(todoItems)을 ListView에 바인딩하는 어댑터
    private String selectedStartTime; // 시작 시간 저장
    private String selectedEndTime;   // 종료 시간 저장

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        // 할 일 목록을 ListView에 연결
        todoItems = new ArrayList<>();
        adapter = new TodoAdapter(getActivity(), todoItems);
        ListView listView = rootView.findViewById(R.id.todoListView);
        listView.setAdapter(adapter);

        // 할 일 목록 로드
        loadTodoList();
        
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

        final EditText editTextTodoContent = dialogView.findViewById(R.id.editTextTodoContent);
        final EditText editTextTodoMemo = dialogView.findViewById(R.id.editTextTodoMemo);
        final TextView textViewStartTime = dialogView.findViewById(R.id.textViewStartTime);
        final TextView textViewEndTime = dialogView.findViewById(R.id.textViewEndTime);

        // Dialog 생성
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();

        // 닫기(X) 버튼 Listener
        ImageButton closeButton = dialogView.findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 확인(체크) 버튼 Listener
        ImageButton checkButton = dialogView.findViewById(R.id.buttonCheck);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextTodoContent.getText().toString();
                String memo = editTextTodoMemo.getText().toString();
                String startTime = textViewStartTime.getText().toString();
                String endTime = textViewEndTime.getText().toString();

                // 예외 처리
                if (content.isEmpty()) {  // 할 일 내용 비어 있으면 경고
                    new AlertDialog.Builder(getContext())
                            .setTitle("경고")
                            .setMessage("할 일을 입력하세요.")
                            .setPositiveButton("확인", null) //
                            .show();
                }
                else if ((selectedStartTime == null && selectedEndTime != null) ||
                        (selectedStartTime != null && selectedEndTime == null)) {  // 시간이 하나만 선택되어 있으면 경고
                    new AlertDialog.Builder(getContext())
                            .setTitle("경고")
                            .setMessage("시간을 모두 선택해주세요.")
                            .setPositiveButton("확인", null)
                            .show();
                } else {  // 모든 입력이 제대로 되어 있으면 할 일 항목 추가
                    TodoItem newItem = new TodoItem(content, memo, selectedStartTime, selectedEndTime);
                    todoItems.add(newItem);
                    saveTodoList();
                    adapter.notifyDataSetChanged();
                    // 할 일 추가 확인 메세지
                    Toast.makeText(getContext(), "할 일이 추가되었습니다", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        // 시작 시간 설정 Listener
        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(textViewStartTime);
            }
        });
        
        // 종료 시간 설정 Listener
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
        // 현재 시간을 기본 값으로 표시
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // TimePickerDialog 생성 및 리스너 설정
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {  // '확인' 버튼 Listener
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        // 사용자가 선택한 시간으로 Calendar 객체 설정
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minuteOfHour);
                        // 설정된 시간을 포맷하여 TextView에 표시
                        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
                        timeTextView.setText(formattedTime);
                        // 선택된 시간 저장
                        if(timeTextView.getId() == R.id.textViewStartTime) {
                            selectedStartTime = formattedTime;
                        } else if(timeTextView.getId() == R.id.textViewEndTime) {
                            selectedEndTime = formattedTime;
                        }
                    }
                }, hour, minute, false);

        // '취소' 버튼 Listener
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(timeTextView.getId() == R.id.textViewStartTime) {
                    selectedStartTime = null;
                } else if(timeTextView.getId() == R.id.textViewEndTime) {
                    selectedEndTime = null;
                }
            }
        });

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }

    // 할 일 목록을 SharedPreferences에 저장하는 메소드
    private void saveTodoList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_TODO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String todoListJson = gson.toJson(todoItems);  // Gson 라이브러리 사용하여 할 일 목록을 JSON 형태로 변환
        editor.putString(KEY_TODO_LIST, todoListJson);  // JSON 문자열을 SharedPreferences에 저장
        editor.apply();

        adapter.notifyDataSetChanged();
    }

    // 할 일 목록을 SharedPreferences에서 로드하는 메소드
    private void loadTodoList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_TODO, Context.MODE_PRIVATE);
        String todoListJson = sharedPreferences.getString(KEY_TODO_LIST, "");

        // JSON 비어있지 않으면 파싱하여 할 일 목록에 할당
        if (!todoListJson.isEmpty() && todoItems.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TodoItem>>() {}.getType();
            todoItems = gson.fromJson(todoListJson, type);
            adapter.addAll(todoItems);
        }
    }
}
