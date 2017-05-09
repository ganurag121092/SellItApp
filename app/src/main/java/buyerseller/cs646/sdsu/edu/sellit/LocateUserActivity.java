package buyerseller.cs646.sdsu.edu.sellit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocateUserActivity extends BaseActivity implements OnMapReadyCallback {
    private String sellerName, buyerName;
    private Double buyerLat, buyerLon, sellerLat, sellerLon;
    List<UserModel> users;
    private GoogleMap mMap;
    private LatLng coordinates;
    private CameraUpdate newLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_user);
        Intent intent = getIntent();
        buyerName =intent.getStringExtra("Buyer");
        sellerName =intent.getStringExtra("Seller");
        buyerLat =intent.getDoubleExtra("BuyerLat",32.7157);
        buyerLon =intent.getDoubleExtra("BuyerLon",-117.1611);
        sellerLat =intent.getDoubleExtra("SellerLat",32.7157);
        sellerLon =intent.getDoubleExtra("SellerLon",-117.1611);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locateUserMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        users = new ArrayList<>();
        users.add(new UserModel(buyerName,buyerLat,buyerLon));
        users.add(new UserModel(sellerName,sellerLat,sellerLon));
        Iterator<UserModel> user = users.iterator();
        int count = 0;
        while(user.hasNext()){
            count++;
            UserModel currentUser = user.next();
            coordinates = new LatLng(currentUser.latitude,currentUser.longitude);
            if(count==1) {
                mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                        .setTitle(currentUser.name);
            }
            else{
                mMap.addMarker(new MarkerOptions()
                        .position(coordinates)).setTitle(currentUser.name);
            }
            newLocation= CameraUpdateFactory.newLatLngZoom(coordinates,5);
            mMap.moveCamera(newLocation);
        }
    }
}
