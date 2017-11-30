package kr.or.hanium.shareseoul.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.interfaces.LoginService;
import kr.or.hanium.shareseoul.models.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        overridePendingTransition(0, 0);  // 전환 애니메이션 없애기

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        TextView btnToRegister = findViewById(R.id.btn_to_register);
        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);

        final String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LoginService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())  // JSON Converter 지정
                    .build();
            LoginService loginService = retrofit.create(LoginService.class);

            loginService.getLogin(email, password).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    Login login = response.body();
                    if (login.getResult()) {
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("userId", login.getId());
                        editor.putString("userEmail", email);
                        editor.apply();

                        finish();
                        Toast.makeText(LoginActivity.this, "로그인되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
