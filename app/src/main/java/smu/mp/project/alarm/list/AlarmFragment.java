package smu.mp.project.alarm.list;

import android.content.Context;
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

import java.util.ArrayList;

import smu.mp.project.R;

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
                int count = adapter.getCount();
                items.add(new AlarmItem("LIST" + Integer.toString(count + 1)));
                adapter.notifyDataSetChanged();
            }
        });

        /**
        Button btn_delete = (Button) view.findViewById(R.id.btn_delete) ;
        btn_delete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;

                if (count > 0) {
                    checked = listview.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        items.remove(checked) ;
                        listview.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }); **/

        return view;
    }
}
