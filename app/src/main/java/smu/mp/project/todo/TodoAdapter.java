package smu.mp.project.todo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import smu.mp.project.R;

// 할 일(TodoItem) 목록을 ListView에 바인딩하는 클래스
public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private Context context;
    // 생성자
    public TodoAdapter(Context context, List<TodoItem> items) {
        super(context, 0, items);
        this.context = context;
    }

    // 각 TodoItem의 뷰를 반환하는 메소드

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
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
            sTime = currentItem.getStartTime();
            eTime = currentItem.getEndTime();
        }

        // 데이터를 각 TextView에 설정
        contentTextView.setText(currentItem.getContent());
        memoTextView.setText(currentItem.getMemo());
        sTimeTextView.setText(sTime);
        eTimeTextView.setText(eTime);

        // 할 일 항목 롱클릭 시 팝업 표시 Listener
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, position);
                return true;
            }
        });

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
    
    // 팝업메뉴 메소드

    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.todo_popup_menu, popupMenu.getMenu());
        
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId(); // 메뉴 아이템의 ID 가져오기

                if (itemId == R.id.menu_edit) {
                    // 편집 기능
                    Toast.makeText(context, "편집 선택", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    // 삭제 기능
                    Toast.makeText(context, "삭제 선택", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }

        });

        popupMenu.show();
    }
}