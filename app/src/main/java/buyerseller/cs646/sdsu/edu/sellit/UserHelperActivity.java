package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class UserHelperActivity extends AppCompatActivity implements UserMapFragment.SelectedLocation{
    private String selectedLatitude,selectedLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_helper);
        UserMapFragment userMapFragment = new UserMapFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_user_helper, userMapFragment)
                .commit();
    }

    public void getLatLon(String latitude, String longitude){
        selectedLatitude = latitude;
        selectedLongitude = longitude;
    }

    public void setLocationCoordinates(View button){
        if(selectedLatitude!=null&&selectedLongitude!=null) {
            Intent returnData = getIntent();
            Log.i("selectedLat,Longitude",selectedLatitude + " " + selectedLongitude);
            returnData.putExtra("Latitude", selectedLatitude);
            returnData.putExtra("Longitude", selectedLongitude);
            setResult(RESULT_OK, returnData);
            finish();
        }
        else{
            Toast.makeText(this,"Please Place Marker",Toast.LENGTH_SHORT).show();
        }
    }
}
