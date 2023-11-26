package smu.mp.project.todo;

import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Locale;

// 캘린더 연도와 월의 날짜 형식 설정
public class CustomTitleFormatter implements TitleFormatter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM", Locale.getDefault());
    @Override
    public CharSequence format(CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
