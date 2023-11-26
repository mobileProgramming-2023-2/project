package smu.mp.project.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ImageView;

public class SummerAnimation {

    public static void startSunRotation(ImageView sunImage) {
        sunImage.setVisibility(View.VISIBLE);

        sunImage.post(new Runnable() {
            @Override
            public void run() {
                // 피벗 포인트를 이미지의 중심으로 설정
                sunImage.setPivotX(sunImage.getWidth() / 2.0f);
                sunImage.setPivotY(sunImage.getHeight() / 2.0f);

                // 이미지뷰의 위치를 화면의 오른쪽 상단 모서리로 이동
                sunImage.setTranslationX(-sunImage.getWidth() / 2.0f);
                sunImage.setTranslationY(-sunImage.getHeight() / 2.0f);

                ObjectAnimator rotation = ObjectAnimator.ofFloat(sunImage, "rotation", 0, 40f);
                rotation.setDuration(5000);
                rotation.setRepeatMode(ValueAnimator.REVERSE);
                rotation.setRepeatCount(ValueAnimator.INFINITE); // 무한 반복
                rotation.start();
            }
        });
    }

    public static void stopSunRotation(ImageView sunImage) {
        sunImage.setVisibility(View.GONE);
        sunImage.clearAnimation(); // 애니메이션 중지
    }
}
