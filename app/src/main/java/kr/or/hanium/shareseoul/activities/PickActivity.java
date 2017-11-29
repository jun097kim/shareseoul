package kr.or.hanium.shareseoul.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import kr.or.hanium.shareseoul.R;

public class PickActivity extends AppCompatActivity implements View.OnClickListener {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        ImageView ivBike = findViewById(R.id.iv_bike);
        ImageView ivRestaurant = findViewById(R.id.iv_restaurant);
        ImageView ivBathroom = findViewById(R.id.iv_bathroom);

        ivBike.setOnClickListener(this);
        ivRestaurant.setOnClickListener(this);
        ivBathroom.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (view.getId()) {
            case R.id.iv_bike:
                intent.putExtra("picked", "bikes");
                break;
            case R.id.iv_restaurant:
                intent.putExtra("picked", "restaurants");
                break;
            case R.id.iv_bathroom:
                intent.putExtra("picked", "bathrooms");
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;

        private BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        private void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        private void showGuide() {
            toast = Toast.makeText(activity, "종료하려면 한번 더 누르세요.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
