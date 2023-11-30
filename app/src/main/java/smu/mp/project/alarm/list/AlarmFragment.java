package smu.mp.project.alarm.list;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Calendar;

import smu.mp.project.R;
import smu.mp.project.alarm.add.AlarmAddFragment;
import smu.mp.project.alarm.add.AlarmReceiver;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AlarmFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView listview;
    ArrayList<AlarmItem> items;

    private String mParam1;
    private String mParam2;

    private TextView batteryInfoTextView;
    private BroadcastReceiver batteryLevelReceiver;

    public AlarmFragment() {
        // Required empty public constructor
    }

    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        items = new ArrayList<AlarmItem>() ;
        items.add(new AlarmItem("08:00"));
        items.add(new AlarmItem("12:00"));
        items.add(new AlarmItem("18:00"));

        ArrayAdapter<AlarmItem> adapter = new ArrayAdapter<AlarmItem>(getActivity(),
                android.R.layout.simple_list_item_single_choice, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item, null);
                }

                TextView timeView = view.findViewById(R.id.textView);
                ImageButton deleteButton = view.findViewById(R.id.deleteButton);

                timeView.setText(items.get(position).getTime());
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        items.remove(position);
                        notifyDataSetChanged();
                    }
                });

                return view;
            }
        };

        listview = view.findViewById(R.id.listview);
        listview.setAdapter(adapter) ;

        ImageButton btn_add = (ImageButton) view.findViewById(R.id.btn_add) ;
        btn_add.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // 알람 추가 프래그먼트를 생성하고 화면에 표시
                AlarmAddFragment alarmAddFragment = new AlarmAddFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, alarmAddFragment);
                transaction.addToBackStack(null);
                transaction.commit();

//                int count = adapter.getCount();
//                items.add(new AlarmItem("LIST" + Integer.toString(count + 1)));
//                adapter.notifyDataSetChanged();
            }
        });

        batteryInfoTextView = view.findViewById(R.id.batteryInfoTextView);
        setupBatteryInfoReceiver();

        return view;
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

    // AlarmItem을 리스트에 추가하고 알람을 설정하는 메서드
    public void addItem(AlarmItem alarmItem) {
        items.add(alarmItem);
        ArrayAdapter<AlarmItem> adapter = (ArrayAdapter<AlarmItem>) listview.getAdapter();
        adapter.notifyDataSetChanged();

        setAlarm(alarmItem.getTime());
    }

    // 알람을 설정하는 메서드
    private void setAlarm(String time) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
