package smu.mp.project.todo;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

// 캘린더 일요일을 빨간색으로 표시
public class SundayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int currentMonth = CalendarDay.today().getMonth();

        // 현재 달의 일요일인지 확인
        return weekDay == Calendar.SUNDAY && calendar.get(Calendar.MONTH) == currentMonth;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}