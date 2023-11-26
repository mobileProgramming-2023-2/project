package smu.mp.project.reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import smu.mp.project.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Arrays;
import java.util.HashMap;

public class BookFragment extends Fragment {
    final Button button[] = new Button[54];
    public static Integer[] buttonID = {
            R.id.A1, R.id.A2, R.id.A3, R.id.A4, R.id.A5, R.id.A6, R.id.A7, R.id.A8, R.id.A9,
            R.id.B1, R.id.B2, R.id.B3, R.id.B4, R.id.B5, R.id.B6, R.id.B7, R.id.B8, R.id.B9,
            R.id.C1, R.id.C2, R.id.C3, R.id.C4, R.id.C5, R.id.C6, R.id.C7, R.id.C8, R.id.C9,
            R.id.D1, R.id.D2, R.id.D3, R.id.D4, R.id.D5, R.id.D6, R.id.D7, R.id.D8, R.id.D9,
            R.id.E1, R.id.E2, R.id.E3, R.id.E4, R.id.E5, R.id.E6, R.id.E7, R.id.E8, R.id.E9,
            R.id.F1, R.id.F2, R.id.F3, R.id.F4, R.id.F5, R.id.F6, R.id.F7, R.id.F8, R.id.F9
    };

    public boolean boolArray[] = new boolean[54];
    public static HashMap<Integer, boolean[]> lectureReservedSeatsMap = new HashMap<>();
    public int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        ImageView iv_poster = view.findViewById(R.id.imageViewforPoster);
        Bundle args = getArguments();

        String lectureTime = getArguments().getString("lecture_time");
        TextView tvTime = view.findViewById(R.id.time);
        tvTime.setText(lectureTime);

        String location = getArguments().getString("lecture_location");
        TextView tvLoca = view.findViewById(R.id.location);
        tvLoca.setText(location);

        String application = getArguments().getString("lecture_application");
        TextView tvApply = view.findViewById(R.id.apply);
        tvApply.setText(application);

        final int getImage = args.getInt("image_id", 1);
        final int lectureId = args.getInt("lecture_id", -1);
        iv_poster.setImageResource(getImage);

        if (!lectureReservedSeatsMap.containsKey(lectureId)) {
            lectureReservedSeatsMap.put(lectureId, new boolean[54]);
        }

        boolean[] isReservedArray = lectureReservedSeatsMap.get(lectureId);

        for (int i = 0; i < buttonID.length; i++) {
            button[i] = view.findViewById(buttonID[i]);
            if (isReservedArray != null && i < isReservedArray.length && isReservedArray[i]) {
                button[i].setEnabled(false);
            }
            final int index = i;

            button[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!boolArray[index]) {
                        button[index].setSelected(true);
                        boolArray[index] = true;
                        count++;
                    } else {
                        button[index].setSelected(false);
                        boolArray[index] = false;
                        count--;
                    }
                }
            });
        }

        final Button btn = view.findViewById(R.id.buttonPay);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    StringBuilder selectedSeats = new StringBuilder();
                    for (int i = 0; i < boolArray.length; i++) {
                        if (boolArray[i]) {
                            char row = (char)('A' + i / 9); // 'A'부터 'F'까지의 줄
                            int seatNumber = i % 9 + 1; // 1부터 9까지의 좌석 번호
                            selectedSeats.append(row).append(seatNumber).append(", ");
                        }
                    }

                    if (selectedSeats.length() > 0) {
                        selectedSeats.setLength(selectedSeats.length() - 2);
                    }

                    boolean[] reservedSeats = lectureReservedSeatsMap.get(lectureId);
                    if (reservedSeats == null) {
                        reservedSeats = new boolean[54];
                        lectureReservedSeatsMap.put(lectureId, reservedSeats);
                    }
                    for (int i = 0; i < boolArray.length; i++) {
                        if (boolArray[i]) {
                            reservedSeats[i] = true;
                        }
                    }

                    FinishFragment finishFragment = new FinishFragment();
                    Bundle args = new Bundle();
                    if (getArguments() != null) {
                        args.putInt("final_image_id", getArguments().getInt("image_id", 1));
                        args.putString("lecture_title", getArguments().getString("lecture_title"));
                        args.putString("lecture_time", getArguments().getString("lecture_time"));
                        args.putString("lecture_location", getArguments().getString("lecture_location"));
                    }
                    args.putInt("count_key", count);
                    args.putString("selected_seats", selectedSeats.toString());
                    finishFragment.setArguments(args);

                    if (isAdded() && getFragmentManager() != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, finishFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    Arrays.fill(boolArray, false);
                    count = 0;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000) {
            count = 0;
            for (int i = 0; i < boolArray.length; i++) {
                boolArray[i] = false;
                if (!lectureReservedSeatsMap.get(getArguments().getInt("lecture_id", -1))[i]) {
                    button[i].setSelected(false);
                }
            }
        }
    }
}



