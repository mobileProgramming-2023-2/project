package smu.mp.project.reservation;
import smu.mp.project.R;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ResFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_res, container, false);

        GridView gv = view.findViewById(R.id.gridView01);
        MyGridAdapter gAdapter = new MyGridAdapter(getActivity());
        gv.setAdapter(gAdapter);


        ImageView bannerImage = view.findViewById(R.id.banner);


        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snoway.sookmyung.ac.kr/login.jsp"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

    public class MyGridAdapter extends BaseAdapter {
        private final Context context;

        Integer[] posterID = {R.drawable.show1, R.drawable.show2, R.drawable.show3,
                R.drawable.show4, R.drawable.show5, R.drawable.show6,
                R.drawable.show7, R.drawable.show8, R.drawable.show9};
        Integer[] posterTitle = {R.string.show1, R.string.show2, R.string.show3,
                R.string.show4, R.string.show5, R.string.show6,
                R.string.show7, R.string.show8, R.string.show9};

        Integer[] lectureTime = {R.string.show1time, R.string.show2time,
                R.string.show3time,R.string.show4time,R.string.show5time,
                R.string.show6time,R.string.show7time,R.string.show8time,
                R.string.show9time,};

        Integer[] location = {R.string.show1location, R.string.show2location,
                R.string.show3location,R.string.show4location,R.string.show5location,
                R.string.show6location,R.string.show7location,R.string.show8location,
                R.string.show9location,};

        Integer[] application = {R.string.show1apply, R.string.show2apply,
                R.string.show3apply,R.string.show4apply,R.string.show5apply,
                R.string.show6apply,R.string.show7apply,R.string.show8apply,
                R.string.show9apply,};


        public MyGridAdapter(Context c) {
            context = c;
        }

        @Override
        public int getCount() {
            return posterID.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 550));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5, 5, 10, 10);

            imageView.setImageResource(posterID[position]);
            final int pos = position;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    ImageView ivPoster = dialogView.findViewById(R.id.imageViewPoster);

                    if (ivPoster != null) {
                        ivPoster.setImageResource(posterID[pos]);
                        dlg.setTitle(context.getString(posterTitle[pos]));
                        dlg.setView(dialogView);
                        dlg.setNegativeButton("닫기", null);
                        dlg.setPositiveButton("예약", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BookFragment bookFragment = new BookFragment();
                                Bundle args = new Bundle();
                                args.putInt("image_id", posterID[pos]);
                                args.putInt("lecture_id", pos);
                                args.putString("lecture_title", context.getString(posterTitle[pos]));
                                args.putString("lecture_time", context.getString(lectureTime[pos]));
                                args.putString("lecture_location", context.getString(location[pos]));
                                args.putString("lecture_application", context.getString(application[pos]));
                                bookFragment.setArguments(args);

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frame_container, bookFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                        dlg.show();
                    } else {
                        Toast.makeText(getActivity(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return imageView;
        }
    }
}