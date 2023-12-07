package smu.mp.project.home;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import smu.mp.project.R;
import android.os.Handler;

public class SpringAnimation {
    private Context context;
    private ViewGroup layout;
    private List<ImageView> petals;
    private Handler handler = new Handler();

    // 생성자 추가
    public SpringAnimation(Context context, ViewGroup layout) {
        this.context = context;
        this.layout = layout;
        this.petals = new ArrayList<>();
    }

    private Runnable createPetalRunnable = new Runnable() {
        @Override
        public void run() {
            createPetal();
            handler.postDelayed(this, 500); // 0.5초마다 새로운 꽃잎 생성
        }
    };


    public void startAnimation() {
        handler.post(createPetalRunnable); // 애니메이션 시작
    }

    private void createPetal() {
        ImageView petal = new ImageView(context);
        petal.setImageResource(R.drawable.flower);

        int petalSize = new Random().nextInt(15) + 30; // 최소크기 30dp 무작워 +10dp
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                convertDpToPixel(petalSize, context),
                convertDpToPixel(petalSize, context)
        );
        petal.setLayoutParams(params);

        layout.addView(petal);
        petals.add(petal);

        float startX = new Random().nextFloat() * layout.getWidth();
        float midX1 = new Random().nextFloat() * layout.getWidth();
        float midX2 = new Random().nextFloat() * layout.getWidth();
        float endX = new Random().nextFloat() * layout.getWidth();
        float startY = -convertDpToPixel(petalSize, context);
        float endY = layout.getHeight() + convertDpToPixel(petalSize, context);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(petal, "translationX", startX, midX1, midX2, endX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(petal, "translationY", startY, endY);
        ObjectAnimator animatorRotate = ObjectAnimator.ofFloat(petal, "rotation", 0f, new Random().nextInt(360));

        animatorX.setDuration(new Random().nextInt(40000) + 20000);
        animatorY.setDuration(new Random().nextInt(5000) + 10000);
        animatorRotate.setDuration(new Random().nextInt(1000) + 10000);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY, animatorRotate);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private int convertDpToPixel(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public void stopAnimation() {
        handler.removeCallbacks(createPetalRunnable);
        for (ImageView petal : petals) {
            layout.removeView(petal);
        }
        petals.clear();
    }
}
