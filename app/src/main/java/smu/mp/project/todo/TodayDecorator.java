package smu.mp.project.todo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

// 오늘 날짜 디자인
public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today;
    private final Drawable backgroundDrawable;
    private final ForegroundColorSpan whiteSpan;

    public TodayDecorator() {
        today = CalendarDay.today();
        // 검정색 원으로 배경 설정
        ShapeDrawable circleDrawable = new ShapeDrawable(new OvalShape());
        circleDrawable.getPaint().setColor(Color.BLACK); // 검정색 설정
        circleDrawable.setIntrinsicWidth(60); // 원의 크기 설정
        circleDrawable.setIntrinsicHeight(60);
        backgroundDrawable = circleDrawable;
        whiteSpan = new ForegroundColorSpan(Color.WHITE);  // 숫자를 하얀색으로 설정
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 오늘 날짜인지 확인
        return today.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // 배경과 텍스트 색상 적용
        view.setSelectionDrawable(backgroundDrawable);
        view.addSpan(whiteSpan);
    }
}