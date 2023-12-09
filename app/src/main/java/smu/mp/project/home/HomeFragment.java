package smu.mp.project.home;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;

import smu.mp.project.R;

public class HomeFragment extends Fragment {

    private SpringAnimation springAnimation;

    private String[] quotes = {
            "1퍼센트의 가능성, 그것이 나의 길이다.",
            "하루하루를 마지막이라고 생각하라.\n 그러면 예측할 수 없는 시간은 \n 그대에게 더 많은 시간을 줄 것이다.",
            "꿈을 계속 간직하고 있으면, \n 반드시 실현할 때가 온다.",
            "미래를 신뢰하지 마라, 죽은 과거는 묻어버려라, \n 그리고 살아있는 현재에 행동하라.",
            "인간은 항상 시간이 모자란다고 불평을 하면서 \n마치 시간이 무한정 있는 것처럼 행동한다.",
            "햇빛은 하나의 초점에 모아질 때만 \n 불꽃을 피우는 것이다.",
            "끝을 맺기를 처음과 같이 하면 실패가 없다.",
            "인생에 뜻을 세우는 데 있어 늦은 때라곤 없다"
    };

    private String currentSeason = "겨울";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FrameLayout homeLayout = view.findViewById(R.id.home);
        springAnimation = new SpringAnimation(getContext(), homeLayout); // 인스턴스 초기화


        TextView speechBubble = view.findViewById(R.id.speechBubble);
        String randomQuote = getRandomQuote();
        speechBubble.setText(randomQuote);

        ImageView characterImage = view.findViewById(R.id.characterImage);
        characterImage.setOnClickListener(v -> {
            if (speechBubble.getVisibility() == View.VISIBLE) {
                speechBubble.setVisibility(View.GONE);
            } else {
                speechBubble.setVisibility(View.VISIBLE);
                speechBubble.setText(getRandomQuote()); // 명언 업데이트
            }
        });

        updateSeasonView(view, currentSeason);


        //FrameLayout homeLayout = view.findViewById(R.id.home);
        registerForContextMenu(homeLayout);
        registerForContextMenu(characterImage);

        return view;
    }


    private void updateSeasonView(View view, String season) {
        ImageView characterImage = view.findViewById(R.id.characterImage);
        ImageView sunImage = view.findViewById(R.id.sunImage);
        TextView speechBubble = view.findViewById(R.id.speechBubble);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setVisibility(View.GONE);
        SummerAnimation.stopSunRotation(sunImage);
        springAnimation.stopAnimation();
        speechBubble.getBackground().setAlpha(180);

        switch (season) {
            case "봄":
                view.setBackgroundResource(R.drawable.spring_background);
                characterImage.setImageResource(R.drawable.spring_character);
                speechBubble.setTextColor(Color.BLACK);
                springAnimation.startAnimation();
                break;
            case "여름":
                view.setBackgroundResource(R.drawable.summer_background);
                characterImage.setImageResource(R.drawable.summer_character);
                speechBubble.setTextColor(Color.BLACK);
                SummerAnimation.startSunRotation(sunImage);
                break;
            case "가을":
                view.setBackgroundResource(R.drawable.autumn_background2);
                characterImage.setImageResource(R.drawable.autumn_character);
                speechBubble.setTextColor(Color.BLACK);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation(R.raw.autumnanimation);
                lottieAnimationView.setRepeatCount(ValueAnimator.INFINITE);
                lottieAnimationView.setSpeed(0.8f);
                lottieAnimationView.playAnimation();
                break;
            case "겨울":
                view.setBackgroundResource(R.drawable.winter_background);
                characterImage.setImageResource(R.drawable.winter_character);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation(R.raw.snowanimation);
                lottieAnimationView.setRepeatCount(ValueAnimator.INFINITE);
                lottieAnimationView.playAnimation();
                break;
        }

        view.invalidate();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (menu.size() == 0) {
            menu.add(0, 1, 0, "봄");
            menu.add(0, 2, 0, "여름");
            menu.add(0, 3, 0, "가을");
            menu.add(0, 4, 0, "겨울");
        }
    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                currentSeason = "봄";
                break;
            case 2:
                currentSeason = "여름";
                break;
            case 3:
                currentSeason = "가을";
                break;
            case 4:
                currentSeason = "겨울";
                break;
            default:
                return super.onContextItemSelected(item);
        }
        updateSeasonView(getView(), currentSeason);
        return true;
    }

    private String getRandomQuote() {
        return quotes[new Random().nextInt(quotes.length)];
    }
}
