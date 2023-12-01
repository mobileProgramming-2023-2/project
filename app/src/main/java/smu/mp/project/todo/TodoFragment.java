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
import android.widget.Button;
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

//TODO: 스크롤 / 체크박스 오류 / 캘린더에 투두 수

// 할 일 목록 관리하는 UI Fragment
public class TodoFragment extends Fragment {
    // Preference 파일과 키 이름 정의
    private static final String PREFS_TODO = "prefs_todo";
    private static final String KEY_TODO_LIST = "todo_list";

    // 할 일 목록과 관련된 변수들
    private List<TodoItem> todoItems;  // 할 일 항목을 저장하는 리스트
    private TodoAdapter adapter;  // 할 일 목록을 ListView에 바인딩하는 어댑터
    private Map<String, List<TodoItem>> todoMap; // 날짜별 할 일 목록을 저장하는 맵

    // 사용자가 선택한 시간과 날짜를 저장하는 변수들
    private String selectedStartTime; // 선택한 시작 시간
    private String selectedEndTime;   // 선택한 종료 시간
    private String selectedDate;      // 선택한 날짜

    // 할 일 항목 삭제 리스너 인터페이스
    public interface TodoItemDeleteListener {
        void onDeleteTodoItem(int position);
    }

    // 할 일 항목 편집 리스너 인터페이스
    public interface TodoItemEditListener {
        void onEditTodoItem(int position);
    }

    // 할 일 항목 삭제 및 편집 리스너 구현
    private TodoItemDeleteListener deleteListener = this::deleteTodoItem;
    private TodoItemEditListener editListener = this::editTodoItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        // 캘린더 뷰 커스텀
        initializeCalendarView(rootView);

        // 할 일 목록을 ListView에 연결
        initializeTodoListView(rootView);

        // '오늘' 버튼 클릭 시 오늘 날짜로 재설정
        Button todayButton = rootView.findViewById(R.id.todayButton);
        todayButton.setOnClickListener(v -> updateForToday());

        // (+)버튼 클릭 시 할 일 입력 Dialog 표시
        ImageButton addItemButton = rootView.findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> showDialog());

        return rootView;
    }

    // 캘린더 뷰 초기화
    private void initializeCalendarView(View rootView) {
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
    }

    // 할 일 목록을 표시하는 ListView를 초기화하는 메소드
    private void initializeTodoListView(View rootView) {
        // 할 일 목록과 어댑터 초기화
        todoItems = new ArrayList<>();
        adapter = new TodoAdapter(getActivity(), todoItems, deleteListener, editListener, this);

        // ListView 참조 및 어댑터 설정
        ListView listView = rootView.findViewById(R.id.todoListView);
        listView.setAdapter(adapter);

        // 할 일 목록에 롱 클릭 리스너 설정
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view, position);
                return true;
            }
        });

        // 저장된 할 일 목록 로드
        loadTodoList();
    }

    // 오늘 날짜로 재설정
    private void updateForToday() {
        CalendarDay today = CalendarDay.today();

        MaterialCalendarView calendarView = getView().findViewById(R.id.calendarView);
        calendarView.setSelectedDate(today);
        calendarView.setCurrentDate(today);

        selectedDate = formatDate(today);
        updateTodoListForSelectedDate(selectedDate);
        setFormattedDateText((TextView) getView().findViewById(R.id.todoListTitle), today);
    }

    // 다른 fragment로부터 복귀 시 오늘 날짜로 설정
    @Override
    public void onResume() {
        super.onResume();
        updateForToday();
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
        List<TodoItem> itemsForDate = todoMap.get(selectedDate);
        if (itemsForDate == null || position >= itemsForDate.size()) {
            return;
        }
        TodoItem itemToEdit = itemsForDate.get(position);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.todo_dialog_add, null);

        final EditText editTextTodoContent = dialogView.findViewById(R.id.editTextTodoContent);
        final EditText editTextTodoMemo = dialogView.findViewById(R.id.editTextTodoMemo);
        final TextView textViewStartTime = dialogView.findViewById(R.id.textViewStartTime);
        final TextView textViewEndTime = dialogView.findViewById(R.id.textViewEndTime);

        editTextTodoContent.setText(itemToEdit.getContent());
        editTextTodoMemo.setText(itemToEdit.getMemo());

        // 시작 및 종료 시간 설정
        if (itemToEdit.getStartTime() != null && !itemToEdit.getStartTime().isEmpty()) {
            textViewStartTime.setText(itemToEdit.getStartTime());
        }
        else {
            textViewStartTime.setText("시작 시간");
        }

        if (itemToEdit.getEndTime() != null && !itemToEdit.getEndTime().isEmpty()) {
            textViewEndTime.setText(itemToEdit.getEndTime());
        }
        else {
            textViewEndTime.setText("종료 시간");
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();

        ImageButton closeButton = dialogView.findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ImageButton checkButton = dialogView.findViewById(R.id.buttonCheck);
        checkButton.setOnClickListener(v -> {
            String content = editTextTodoContent.getText().toString();
            String memo = editTextTodoMemo.getText().toString();
            String startTime = textViewStartTime.getText().toString();
            String endTime = textViewEndTime.getText().toString();

            if (content.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("경고")
                        .setMessage("할 일을 입력하세요.")
                        .setPositiveButton("확인", null)
                        .show();
                return;
            }

            // 예외 처리: 시작 시간과 종료 시간 중 하나만 설정된 경우
            if ((startTime.equals("시작 시간") && !endTime.equals("종료 시간")) ||
                    (!startTime.equals("시작 시간") && endTime.equals("종료 시간"))) {
                new AlertDialog.Builder(getContext())
                        .setTitle("경고")
                        .setMessage("시간을 모두 선택해주세요.")
                        .setPositiveButton("확인", null)
                        .show();
                return;
            }

            // 할 일 항목 업데이트
            itemToEdit.setContent(content);
            itemToEdit.setMemo(memo);
            itemToEdit.setStartTime(startTime.equals("시작 시간") ? null : startTime);
            itemToEdit.setEndTime(endTime.equals("종료 시간") ? null : endTime);

            saveTodoList();
            updateTodoListForSelectedDate(selectedDate);

            dialog.dismiss();
            Toast.makeText(getContext(), "할 일이 편집되었습니다.", Toast.LENGTH_SHORT).show();
        });

        textViewStartTime.setOnClickListener(v -> showTimePickerDialog(textViewStartTime));
        textViewEndTime.setOnClickListener(v -> showTimePickerDialog(textViewEndTime));

        dialog.show();
    }

    // 할 일 항목 삭제 메소드
    private void deleteTodoItem(final int position) {
        List<TodoItem> itemsForDate = todoMap.get(selectedDate);
        if (itemsForDate != null && position < itemsForDate.size()) {
            itemsForDate.remove(position);
            saveTodoList();
            updateTodoListForSelectedDate(selectedDate);

            Toast.makeText(getContext(), "할 일이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
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

                    dialog.dismiss();

                    // 할 일 추가 확인 메세지
                    Toast.makeText(getContext(), "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
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
    public void saveTodoList() {
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