package smu.mp.project.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import smu.mp.project.R;
import smu.mp.project.alarm.database.AlarmViewModel;
import smu.mp.project.alarm.list.AlarmItem;
import smu.mp.project.alarm.list.AlarmItemBuilder;
import smu.mp.project.databinding.ListItemBinding;

public class AlarmAdapter extends ListAdapter<AlarmItem, AlarmAdapter.AlarmViewHolder> {

    private OnItemClickListener itemClickListener;
    private OnSwitchCheckedChangeListener switchCheckedChangeListener;
    private Context context;

    // 생성자에서 Context 추가
    public AlarmAdapter(Context context){
        super(DIFF_CALLBACK);
        this.context = context;
    }
    private static final DiffUtil.ItemCallback<AlarmItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<AlarmItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull AlarmItem oldItem, @NonNull AlarmItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AlarmItem oldItem, @NonNull AlarmItem newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    oldItem.getHour() == newItem.getHour() &&
                    oldItem.getMinute() == newItem.getMinute() &&
                    oldItem.isTotalFlag() == newItem.isTotalFlag() &&
                    Objects.equals(oldItem.getDay(), newItem.getDay()) &&
                    Objects.equals(oldItem.getAlarmSoundUri(), newItem.getAlarmSoundUri()) &&
                    Objects.equals(oldItem.getAlarmSoundName(), newItem.getAlarmSoundName());
        }
    };

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListItemBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false);
        return new AlarmViewHolder(itemBinding);
    }

    public AlarmItem getAlarmAt(int position){
        return getItem(position);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmItem alarmItem = getItem(position);
        holder.listItemBinding.setAlarmItem(alarmItem);
        holder.listItemBinding.totalSwitch.setChecked(alarmItem.isTotalFlag());
    }


    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding listItemBinding;

        public AlarmViewHolder(@NonNull ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClicked(getItem(position));
                }
            });

            listItemBinding.totalSwitch.setOnCheckedChangeListener(null); // 리스너 일시적으로 해제

            listItemBinding.totalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AlarmItem alarmItem = getAlarmAt(position);
                    if (alarmItem != null) {
                        alarmItem.setTotalFlag(isChecked);
                        switchCheckedChangeListener.onSwitchCheckedChanged(alarmItem);

                        // 비동기 처리로 변경
                        updateAlarmStateAsync(alarmItem, isChecked);
                    }
                }
            });
        }
    }

    // 알람 상태를 비동기적으로 업데이트
    private static final int MY_PERMISSIONS_REQUEST_SET_ALARM = 1;

    private void updateAlarmStateAsync(AlarmItem alarmItem, boolean isActive) {
        new Thread(() -> {
            try {
                // Intent 호출 시 Context의 시작 방식 확인
                Intent alarmIntent = new Intent(context, AlarmManagerActivity.class);
                alarmIntent.putExtra("alarmItem", alarmItem);
                alarmIntent.putExtra("request", isActive ? "create" : "cancel");

                // Context를 사용하여 Activity를 시작하는 경우 FLAG_ACTIVITY_NEW_TASK 필요
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // 권한 체크 추가
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    // 권한 요청
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.SET_ALARM}, MY_PERMISSIONS_REQUEST_SET_ALARM);
                } else {
                    context.startActivity(alarmIntent);
                }
            } catch (Exception e) {
                e.printStackTrace(); // 로그를 기록
            }
        }).start();
    }

    public interface OnItemClickListener {
        void onItemClicked(AlarmItem alarmItem);
    }

    public interface OnSwitchCheckedChangeListener {
        void onSwitchCheckedChanged(AlarmItem alarmItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnSwitchCheckedChangeListener(OnSwitchCheckedChangeListener switchListener) {
        this.switchCheckedChangeListener = switchListener;
    }
}