package smu.mp.project.reservation;

import smu.mp.project.R;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.animation.ValueAnimator;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieAnimationView;

public class FinishFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String lectureTitle = args.getString("lecture_title");
            String selectedSeats = args.getString("selected_seats");
            String location = args.getString("lecture_location");
            String lectureTime = args.getString("lecture_time");

            TextView tv_lectureTitle = view.findViewById(R.id.lecture_title);
            tv_lectureTitle.setText("<" + lectureTitle + ">");
            TextView tv_selectedSeats = view.findViewById(R.id.selected_seats);
            tv_selectedSeats.setText(selectedSeats);
            TextView tv_location = view.findViewById(R.id.location2);
            tv_location.setText(location);
            TextView tv_time = view.findViewById(R.id.time2);
            tv_time.setText(lectureTime);

            LottieAnimationView lottieView = view.findViewById(R.id.check);
            lottieView.playAnimation();
            lottieView.setAnimation(R.raw.check);
            lottieView.setVisibility(View.VISIBLE);
            lottieView.setRepeatCount(ValueAnimator.INFINITE);
        }

        Button back_home = view.findViewById(R.id.back);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ResFragment 인스턴스 생성
                ResFragment resFragment = new ResFragment();

                // MainActivity의 FragmentManager를 사용하여 ResFragment로 화면 전환
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, resFragment)
                            .commit();
                }
            }
        });




        return view;
    }
}
