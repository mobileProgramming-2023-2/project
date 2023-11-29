package smu.mp.project.todo;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import smu.mp.project.R;

//TODO: 스크롤 생성 이후부터 할 일 추가 바로 안됨 / 체크박스 오류 / 할 일 삭제 및 편집 / 토스트 메시지 위치 / 캘린더에 투두 수

// 할 일 목록 관리하는 UI Fragment
public class TodoFragment extends Fragment {
    private static final String PREFS_TODO = "prefs_todo";
    private static final String KEY_TODO_LIST = "todo_list";
    private List<TodoItem> todoItems;  // 할 일 항목 저장하는 리스트
    private TodoAdapter adapter;  // 할 일 목록(todoItems)을 ListView에 바인딩하는 어댑터
    private String selectedStartTime; // 시작 시간 저장
    private String selectedEndTime;   // 종료 시간 저장
    private String selectedDate;  // 날짜 저장
    private Map<String, List<TodoItem>> todoMap; // 날짜별 할 일 목록을 저장하는 맵

    public TodoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        // 캘린더 뷰 커스텀 //
        MaterialCalendarView calendarView = rootView.findViewById(R.id.calendarView);
        TextView todoListTitle = rootView.findViewById(R.id.todoListTitle);

        // 데코레이터 인스턴스 생성
        SaturdayDecorator saturdayDecorator = new SaturdayDecorator();
        SundayDecorator sundayDecorator = new SundayDecorator();
        TodayDecorator todayDecorator = new TodayDecorator();

        // 현재 달을 초기 달로 설정
        int currentMonth = CalendarDay.today().getMonth();
        saturdayDecorator.setCurrentMonth(currentMonth);
        sundayDecorator.setCurrentMonth(currentMonth);

        // 현재 날짜와 요일을 TextView에 설정
        setFormattedDateText(todoListTitle, CalendarDay.today());

        // 현재 날짜로 selectedDate 초기화
        selectedDate = formatDate(CalendarDay.today());

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = formatDate(date); // 날짜를 변경할 때마다 selectedDate 업데이트
            updateTodoListForSelectedDate(selectedDate); // 해당 날짜에 맞는 할 일 목록 업데이트
            setFormattedDateText(todoListTitle, date); // 해당 날찌로 TextView에 설정
        });

        calendarView.addDecorators(saturdayDecorator, sundayDecorator, todayDecorator);
        calendarView.invalidateDecorators();
        calendarView.setTitleFormatter(new CustomTitleFormatter());

        // 달력 페이지 변경 리스너
        calendarView.setOnMonthChangedListener((widget, date) -> {
            int newMonth = date.getMonth();
            saturdayDecorator.setCurrentMonth(newMonth);
            sundayDecorator.setCurrentMonth(newMonth);
            calendarView.invalidateDecorators(); // 데코레이터 업데이트
        });

        // 할 일 목록을 ListView에 연결
        todoItems = new ArrayList<>();
        adapter = new TodoAdapter(getActivity(), todoItems);
        ListView listView = rootView.findViewById(R.id.todoListView);
        listView.setAdapter(adapter);

        // 할 일 목록 로드
        loadTodoList();

        // (+)버튼 클릭 시 할 일 입력 Dialog 표시
        ImageButton addItemButton = rootView.findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // 할 일 항목 롱클릭 시 삭제/편집 팝업메뉴 표시
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view, position);
                return true;
            }
        });

        return rootView;
    }
    
    // 다시 투두 프래그먼트로 돌아올 때, 오늘 날짜로 재설정
    @Override
    public void onResume() {
        super.onResume();

        CalendarDay today = CalendarDay.today();

        MaterialCalendarView calendarView = getView().findViewById(R.id.calendarView);
        calendarView.setSelectedDate(today);
        calendarView.setCurrentDate(today);

        selectedDate = formatDate(today);
        updateTodoListForSelectedDate(selectedDate);
        setFormattedDateText((TextView) getView().findViewById(R.id.todoListTitle), today);
    }

    // 날짜와 요일을 todoListTitle에 설정하는 메소드
    private void setFormattedDateText(TextView textView, CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDay());
        String dayOfWeek = new SimpleDateFormat("EE", Locale.getDefault()).format(calendar.getTime());
        String formattedDate = String.format(Locale.getDefault(), "%d.%s TODO", date.getDay(), dayOfWeek);

        // 애니메이션 적용
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.todo_title);
        textView.startAnimation(slideUpAnimation);

        textView.setText(formattedDate);
    }

    // 팝업 메뉴 표시 메소드
    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.todo_popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId(); // 메뉴 아이템의 ID 가져오기

                if (itemId == R.id.menu_edit) {
                    // 편집 기능
                    // position을 사용하여 해당 항목을 편집
                    editTodoItem(position);
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    // 삭제 기능
                    // position을 사용하여 해당 항목을 삭제
                    deleteTodoItem(position);
                    return true;
                }

                return false;
            }
        });

        popupMenu.show();
    }

    // 할 일 항목 편집 메소드
    private void editTodoItem(final int position) {
        // 편집 로직
    }

    // 할 일 항목 삭제 메소드
    private void deleteTodoItem(final int position) {
        // 삭제 로직
    }

    // 할 일 입력 Dialog 표시 메소드
    private void showDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.todo_dialog_add, null);

        final EditText editTextTodoContent = dialogView.findViewById(R.id.editTextTodoContent);
        final EditText editTextTodoMemo = dialogView.findViewById(R.id.editTextTodoMemo);
        final TextView textViewStartTime = dialogView.findViewById(R.id.textViewStartTime);
        final TextView textViewEndTime = dialogView.findViewById(R.id.textViewEndTime);

        selectedStartTime = null;
        selectedEndTime = null;

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

                // 예외 처리
                if (content.isEmpty()) {  // 할 일 내용 비어 있으면 경고
                    new AlertDialog.Builder(getContext())
                            .setTitle("경고")
                            .setMessage("할 일을 입력하세요.")
                            .setPositiveButton("확인", null) //
                            .show();
                } else if ((selectedStartTime == null && selectedEndTime != null) ||
                        (selectedStartTime != null && selectedEndTime == null)) {  // 시간이 하나만 선택되어 있으면 경고
                    new AlertDialog.Builder(getContext())
                            .setTitle("경고")
                            .setMessage("시간을 모두 선택해주세요.")
                            .setPositiveButton("확인", null)
                            .show();
                } else {  // 모든 입력이 제대로 되어 있으면 할 일 항목 추가
                    TodoItem newItem = new TodoItem(content, memo, selectedStartTime, selectedEndTime, selectedDate);
                    List<TodoItem> itemsForDate = todoMap.getOrDefault(selectedDate, new ArrayList<>());
                    itemsForDate.add(newItem);
                    todoMap.put(selectedDate, itemsForDate);
                    saveTodoList();

                    updateTodoListForSelectedDate(selectedDate);
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

        timePickerDialog.setCancelable(false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }

    // 날짜를 String 형태로 포맷팅하는 메소드
    private String formatDate(CalendarDay date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.getDate());
    }

    // 선택된 날짜에 맞는 할 일 목록을 업데이트하는 메소드
    private void updateTodoListForSelectedDate(String date) {
        List<TodoItem> itemsForDate = todoMap.getOrDefault(date, new ArrayList<>());
        adapter.clear();
        adapter.addAll(itemsForDate);
        adapter.notifyDataSetChanged();
    }

    // 할 일 목록을 저장하는 메소드
    private void saveTodoList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_TODO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String todoMapJson = gson.toJson(todoMap); // todoMap을 JSON으로 변환
        editor.putString(KEY_TODO_LIST, todoMapJson);
        editor.apply();
    }

    // 할 일 목록을 불러오는 메소드
    private void loadTodoList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_TODO, Context.MODE_PRIVATE);
        String todoMapJson = sharedPreferences.getString(KEY_TODO_LIST, "");
        if (!todoMapJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<TodoItem>>>() {}.getType();
            todoMap = gson.fromJson(todoMapJson, type); // JSON을 Map으로 변환
        } else {
            todoMap = new HashMap<>();
        }
        updateTodoListForSelectedDate(selectedDate);
    }
}
