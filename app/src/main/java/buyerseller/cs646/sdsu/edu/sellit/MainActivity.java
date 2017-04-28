package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button mLoginOption, mGuestOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginOption = (Button)this.findViewById(R.id.loginOptionId);
        mLoginOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,UserLoginActivity.class);
                startActivity(intent);
            }
        });
        mGuestOption = (Button)this.findViewById(R.id.guestOptionId);
        mGuestOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,CategoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
