package kr.or.hanium.mojjak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText idText = (EditText) findViewById(R.id.id_field);
        EditText pwText = (EditText) findViewById(R.id.pw_field);
        Button loginButton = (Button) findViewById(R.id.login_button);
        TextView registerView = (TextView) findViewById(R.id.register_button);

        loginButton.setOnClickListener(this);
        registerView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                break;
            case R.id.register_button:
                break;
        }
    }
}
