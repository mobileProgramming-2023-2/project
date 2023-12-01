package smu.mp.project.alarm;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
    public AlarmAdapter(){
        super(DIFF_CALLBACK);
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

    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder{
        ListItemBinding listItemBinding;

        public AlarmViewHolder(@NonNull ListItemBinding listItemBinding){
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClicked(getItem(position));
                }
            });
            listItemBinding.totalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getBindingAdapterPosition();
                if (switchCheckedChangeListener != null && position != RecyclerView.NO_POSITION) {
                    AlarmItem alarmItem = getAlarmAt(position);
                    alarmItem.setTotalFlag(isChecked);
                    switchCheckedChangeListener.onSwitchCheckedChanged(alarmItem);

                    int color = isChecked ? Color.parseColor("#2A6DF3") : Color.parseColor("#dddddd");
                    listItemBinding.hour.setTextColor(color);
                    listItemBinding.minute.setTextColor(color);
                }
            });
        }
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
