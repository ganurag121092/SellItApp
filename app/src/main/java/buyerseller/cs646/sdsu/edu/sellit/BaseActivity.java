package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by vk on 4/29/17.
 */

public class BaseActivity extends AppCompatActivity {
    private String TAG="BaseActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "im inside the onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_buy_sell, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent mIntent;
        int mItemId = item.getItemId();
        item.setChecked(true);
        // code for setting the Modes  and so that appropriate touch events can be called for different  Modes
        if(mItemId == R.id.Buymenu) {
            Log.i(TAG, "Need to go to UserRegistrationPage" + mItemId);
            mIntent = new Intent(BaseActivity.this,UserRegistrationActivity.class);
            startActivity(mIntent);
            return true;
        }
        else if (mItemId == R.id.Sellmenu) {
            mIntent = new Intent(BaseActivity.this,UserRegistrationActivity.class);
            startActivity(mIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
