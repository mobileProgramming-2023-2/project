package smu.mp.project.library.reservationPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MyGridAdapter extends BaseAdapter {
    Context context;
    int reservedSeat = -1; // No seat is reserved initially

    public MyGridAdapter(Context c) {
        context = c;
    }

    public int getCount(){
        return 24; // 24 seats
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(200, 200));
            button.setPadding(8, 8, 8, 8);
        } else {
            button = (Button) convertView;
        }

        button.setText("Seat " + (position + 1));
        button.setBackgroundColor(position == reservedSeat ? Color.BLUE : Color.parseColor("#B3AAAAAA"));
        button.setTextColor(position == reservedSeat ? Color.WHITE : Color.BLACK);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservedSeat == -1) {
                    // If no seat is reserved, reserve this one
                    reserveSeat(position);
                } else if (reservedSeat == position) {
                    // If this seat is already reserved, ask to cancel
                    cancelSeat(position);
                } else {
                    // If another seat is reserved, ask to switch
                    switchSeat(position);
                }
            }
        });

        return button;
    }

    private void reserveSeat(int position) {
        reservedSeat = position;
        notifyDataSetChanged();
        Toast.makeText(context,  (position + 1) + "번 좌석이 예약되었습니다.", Toast.LENGTH_LONG).show();
    }

    private void cancelSeat(int position) {
        AlertDialog.Builder dig = new AlertDialog.Builder(context);
        dig.setTitle("좌석 예약 취소");
        dig.setMessage((position + 1) + "번 좌석 예약을 취소하시겠습니까?");
        dig.setPositiveButton("예약 취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                reservedSeat = -1;
                notifyDataSetChanged();
                Toast.makeText(context,  (position + 1) + "번 좌석 예약이 취소되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
        dig.setNegativeButton("취소", null);
        dig.show();
    }

    private void switchSeat(int position) {
        AlertDialog.Builder dig = new AlertDialog.Builder(context);
        dig.setTitle("좌석 변경");
        dig.setMessage("이미 " + (reservedSeat + 1) + "번 좌석을 예약하셨습니다." + (position + 1) + "번 좌석으로 변경하시겠습니까?");
        dig.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                reserveSeat(position);
            }
        });
        dig.setNegativeButton("취소", null);
        dig.show();
    }
}
