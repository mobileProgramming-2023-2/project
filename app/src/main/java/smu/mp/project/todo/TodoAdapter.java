package smu.mp.project.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        // 할 일 시간
        TextView timeTextView = convertView.findViewById(R.id.time);
        TodoItem currentItem = getItem(position);
        String TimeRange;
        if (currentItem.getStartTime() == null && currentItem.getEndTime() == null) {
            TimeRange = "";  // 시간 모두 선택 안 되어 있으면 빈 문자열로 표시
        }
        else {
            TimeRange = currentItem.getStartTime() + " - " + currentItem.getEndTime();
        }

        // 각 TextView에 설정
        contentTextView.setText(currentItem.getContent());
        memoTextView.setText(currentItem.getMemo());
        timeTextView.setText(TimeRange);

        return convertView;
    }
}