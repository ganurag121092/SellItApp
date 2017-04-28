package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserLoginActivity extends AppCompatActivity {
    Button mLoginButton, mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mLoginButton = (Button)this.findViewById(R.id.loginBtn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserLoginActivity.this,CategoryActivity.class);
                startActivity(intent);
            }
        });
        mRegisterButton = (Button)this.findViewById(R.id.registerBtn);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserLoginActivity.this,UserRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
