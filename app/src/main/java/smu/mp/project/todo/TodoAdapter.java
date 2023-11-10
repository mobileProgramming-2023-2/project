package smu.mp.project.todo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import smu.mp.project.R;

// 할 일(TodoItem) 목록을 ListView에 바인딩하는 클래스
public class TodoAdapter extends ArrayAdapter<TodoItem> {
    // 생성자
    public TodoAdapter(Context context, List<TodoItem> items) {
        super(context, 0, items);
    }

    // 각 TodoItem의 뷰를 반환하는 메소드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        TextView contentTextView = convertView.findViewById(R.id.content);  // 할 일 내용
        TextView memoTextView = convertView.findViewById(R.id.memo);  // 할 일 메모
        TextView sTimeTextView = convertView.findViewById(R.id.startTime);  // 시작 시간
        TextView eTimeTextView = convertView.findViewById(R.id.endTime);  // 종료 시간
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);  // 체크박스
        TodoItem currentItem = getItem(position);

        // 할 일 시간 표시 형식 설정
        String sTime, eTime;
        if (currentItem.getStartTime() == null && currentItem.getEndTime() == null) {
            sTime = eTime = "";
        }
        else {
            sTime = currentItem.getStartTime(); eTime = currentItem.getEndTime();
        }

        // 데이터를 각 TextView에 설정
        contentTextView.setText(currentItem.getContent());
        memoTextView.setText(currentItem.getMemo());
        sTimeTextView.setText(sTime);
        eTimeTextView.setText(eTime);

        // 체크박스 Listener
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    contentTextView.setPaintFlags(contentTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    contentTextView.setTextColor(Color.LTGRAY);
                } else {
                    contentTextView.setPaintFlags(contentTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    contentTextView.setTextColor(Color.BLACK);
                }
            }
        });

        return convertView;
    }
}