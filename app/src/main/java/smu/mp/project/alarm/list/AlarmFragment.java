package smu.mp.project.alarm.list;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import smu.mp.project.R;
import smu.mp.project.alarm.AlarmAdapter;
import smu.mp.project.alarm.AlarmManagerActivity;
import smu.mp.project.alarm.add.AlarmAddActivity;
import smu.mp.project.alarm.database.AlarmViewModel;
import smu.mp.project.databinding.FragmentAlarmBinding;


public class AlarmFragment extends Fragment {
    public static final int CREATE_ALARM_REQUEST = 1;
    public static final int UPDATE_ALARM_REQUEST = 2;
    public AlarmViewModel alarmViewModel;
    public FragmentAlarmBinding fragmentAlarmBinding;
    Context context;
    RecyclerView recyclerView;
    AlarmAdapter alarmAdapter;
    TextView noAlarmText;

    private TextView batteryInfoTextView;
    private BroadcastReceiver batteryLevelReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAlarmBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container,false);
        fragmentAlarmBinding.setAlarmClick(this);
        View view = fragmentAlarmBinding.getRoot();

        noAlarmText = view.findViewById(R.id.noAlarmText);

        alarmAdapter = new AlarmAdapter(getActivity());

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(alarmAdapter);

        attachItemTouchHelperToAdapter();

        alarmAdapter.setOnItemClickListener(alarmItem -> {
            Intent updateIntent = new Intent(getActivity(), AlarmAddActivity.class);
            updateIntent.putExtra("alarmItem", alarmItem);
            updateIntent.putExtra("REQUEST_STATE", "update");
            startActivityForResult(updateIntent, UPDATE_ALARM_REQUEST);
        });

        alarmAdapter.setOnSwitchCheckedChangeListener(alarmItem -> {
            updateAlarmManager(alarmItem, alarmItem.isTotalFlag() ? "create" : "cancel");
        });

        batteryInfoTextView = view.findViewById(R.id.batteryInfoTextView);
        setupBatteryInfoReceiver();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.getAllAlarms().observe(getViewLifecycleOwner(), new Observer<List<AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmItem> alarmItems) {
                if (alarmItems != null) {
                    alarmAdapter.submitList(alarmItems);

                    if (alarmItems.size() > 0) showAlarmList();
                    else hideAlarmList();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void createAlarmClick(View view){
        Intent createAlarmIntent = new Intent(getActivity(), AlarmAddActivity.class);
        createAlarmIntent.putExtra("REQUEST_STATE", "create");
        startActivityForResult(createAlarmIntent, CREATE_ALARM_REQUEST);
    }

    public void updateAlarmManager(AlarmItem alarmItem, String request){
        Intent alarmIntent = new Intent(getActivity(), AlarmManagerActivity.class);
        alarmIntent.putExtra("alarmItem", alarmItem);
        alarmIntent.putExtra("request", request);
        startActivity(alarmIntent);
    }

    public void attachItemTouchHelperToAdapter(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showDialogDeleteAlarm(viewHolder);
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode != -1) return;
        if (data == null) return;

        AlarmItem alarmItem = (AlarmItem) data.getSerializableExtra("alarmItem");

        switch (requestCode){
            case CREATE_ALARM_REQUEST:
                alarmItem.setTotalFlag(true);
                alarmViewModel.insert(alarmItem);
                Toast.makeText(context,
                        alarmItem.getHour() + "시 " + alarmItem.getMinute() + "분에 알람을 설정하였습니다.",
                        Toast.LENGTH_SHORT).show();
                break;
            case UPDATE_ALARM_REQUEST:
                alarmItem.setTotalFlag(true);
                alarmViewModel.update(alarmItem);
                Toast.makeText(context,
                        "알람을 수정했습니다.",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showDialogDeleteAlarm(@NonNull RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("알람 삭제")
                .setMessage("해당 알람을 삭제하시겠습니까?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setCancelable(false)
                .setPositiveButton("삭제", (dialog, which) -> {
                    AlarmItem alarmItem = alarmAdapter.getAlarmAt(viewHolder.getBindingAdapterPosition());
                    updateAlarmManager(alarmItem, "cancel");
                    alarmViewModel.delete(alarmItem);
                    Toast.makeText(getContext(), "알람을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    alarmAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                    Toast.makeText(getContext(), "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupBatteryInfoReceiver() {
        batteryLevelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int batteryPct = level * 100 / (int)scale;
                batteryInfoTextView.setText("Battery Level: " + batteryPct + "%");

                // 배터리 수준에 따라 색상 변경 (채도가 낮춰진 색상)
                if (batteryPct >= 50) {
                    batteryInfoTextView.setTextColor(Color.parseColor("#4CAF50"));
                } else if (batteryPct >= 20) {
                    batteryInfoTextView.setTextColor(Color.parseColor("#FFEB3B"));
                } else {
                    batteryInfoTextView.setTextColor(Color.parseColor("#F44336"));
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(batteryLevelReceiver);
    }

    public void showAlarmList(){
        noAlarmText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void hideAlarmList(){
        noAlarmText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
