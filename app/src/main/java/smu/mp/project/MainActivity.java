package smu.mp.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import smu.mp.project.alarm.AlarmFragment;
import smu.mp.project.home.HomeFragment;
import smu.mp.project.reservation.ResFragment;
import smu.mp.project.todo.TodoFragment;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private AlarmFragment alarmFragment = new AlarmFragment();
    private TodoFragment todoFragment = new TodoFragment();

    private ResFragment resFragment = new ResFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.navigation);

        // 네비게이션 색상변경 코드
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },  // 선택됐을 때의 상태
                new int[] { -android.R.attr.state_checked }, // 선택되지 않았을 때의 상태
        };

        int[] colors = new int[] {
                Color.parseColor("#376ed4"), //선택됐을 때 색상
                Color.parseColor("#757575"),
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);


        navView.setItemIconTintList(colorStateList);
        navView.setItemTextColor(colorStateList);

        //네비게이션 색상변경 코드 이부분까지


        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.navigation_alarm) {
                switchFragment(alarmFragment);
                return true;
            } else if (itemId == R.id.navigation_todo) {
                switchFragment(todoFragment);
                return true;
            }
            else if (itemId == R.id.navigation_res) {
                switchFragment(resFragment);
                return true;
            }

            return false;
        });

        navView.setSelectedItemId(R.id.navigation_home);
    }

    private void switchFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }
}
