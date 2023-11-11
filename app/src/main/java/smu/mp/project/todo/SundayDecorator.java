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
    private int currentMonth = -1;  // 현재 보고 있는 달력 페이지의 월

    // 현재 페이지의 월을 설정하는 메소드
    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

        // 현재 페이지의 월과 같은 달의 일요일인지 확인
        return weekDay == Calendar.SUNDAY && calendar.get(Calendar.MONTH) == currentMonth;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED)); // 빨간색 적용
    }
}