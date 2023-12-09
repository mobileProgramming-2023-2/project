package smu.mp.project.todo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import smu.mp.project.R;


// 할 일(TodoItem) 목록을 ListView에 바인딩하는 어댑터 클래스
public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private Context context;
    private TodoFragment.TodoItemDeleteListener deleteListener;
    private TodoFragment.TodoItemEditListener editListener;
    private TodoFragment todoFragment;

    // 생성자
    public TodoAdapter(Context context, List<TodoItem> items,
                       TodoFragment.TodoItemDeleteListener deleteListener,
                       TodoFragment.TodoItemEditListener editListener,
                       TodoFragment todoFragment) {
        super(context, 0, items);
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
        this.todoFragment = todoFragment;
    }

    // 각 TodoItem에 대한 뷰 설졍
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }

        // 뷰 바인딩
        TextView contentTextView = convertView.findViewById(R.id.content);
        TextView memoTextView = convertView.findViewById(R.id.memo);
        TextView sTimeTextView = convertView.findViewById(R.id.startTime);
        TextView eTimeTextView = convertView.findViewById(R.id.endTime);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        TodoItem currentItem = getItem(position);
//        final int defaultTextColor = contentTextView.getCurrentTextColor();

        // 체크박스 상태에 따른 텍스트 뷰 설정
        if (currentItem.isChecked()) {
            contentTextView.setPaintFlags(contentTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            contentTextView.setTextColor(Color.LTGRAY);
        } else {
            contentTextView.setPaintFlags(contentTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            contentTextView.setTextColor(Color.parseColor("#8a000000"));
        }

        // 데이터 설정
        contentTextView.setText(currentItem.getContent());
        memoTextView.setText(currentItem.getMemo());
        sTimeTextView.setText(currentItem.getStartTime() != null ? currentItem.getStartTime() : "");
        eTimeTextView.setText(currentItem.getEndTime() != null ? currentItem.getEndTime() : "");

        // 롱클릭 리스너 설정
        convertView.setOnLongClickListener(v -> {
            showPopupMenu(v, position);
            return true;
        });

        // 체크박스 상호작용 설정
        checkBox.setOnCheckedChangeListener(null); // 리스너 초기화
        checkBox.setChecked(currentItem.isChecked());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentItem.setChecked(isChecked);
            todoFragment.saveTodoList();

            if (isChecked) {
                contentTextView.setPaintFlags(contentTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                contentTextView.setTextColor(Color.LTGRAY);
            } else {
                contentTextView.setPaintFlags(contentTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                contentTextView.setTextColor(Color.parseColor("#8a000000"));
            }
        });


        return convertView;
    }

    // 팝업 메뉴 표시 메소드
    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.todo_popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit) {
                editListener.onEditTodoItem(position);
                return true;
            } else if (itemId == R.id.menu_delete) {
                deleteListener.onDeleteTodoItem(position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}