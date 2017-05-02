package buyerseller.cs646.sdsu.edu.sellit;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserMapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener{
    private MapView mapView;
    private GoogleMap googleMap;
    private String selectedLat, selectedLon;
    private int zoomLevel=6;

    public UserMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_map, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mapView = (MapView) v.findViewById(R.id.newUserMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        double latitude = 0.0;
        double longitude = 0.0;
        Geocoder location = new Geocoder(getActivity().getBaseContext());

        try {
            List<Address> state = location.getFromLocationName("San Diego, California,USA",3);
            for (Address stateLocation: state) {
                if (stateLocation.hasLatitude())
                    latitude = stateLocation.getLatitude();
                if (stateLocation.hasLongitude())
                    longitude = stateLocation.getLongitude();
            }
        } catch (Exception error) {
            Log.e("rew", "Address lookup Error", error);
        }


        Log.d("Inside New Map view", latitude + " " + longitude);
        LatLng stateLatLng = new LatLng(latitude, longitude);
        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(stateLatLng, zoomLevel);
        googleMap.moveCamera(newLocation);
    }

    public void onMapClick(LatLng location) {
        Log.i("rew", "new Location " + location.latitude + " longitude " + location.longitude);
        LatLng selectedLocation = new LatLng(location.latitude, location.longitude);
        selectedLat = String.valueOf(location.latitude);
        selectedLon = String.valueOf(location.longitude);
        ((UserHelperActivity)getActivity()).getLatLon(selectedLat,selectedLon);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Marker in Position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));
    }

    //Defining the interface which is implemented by the UserHelper Activity class
    public interface SelectedLocation{
        public void getLatLon(String latitude, String longitude);
    }
}
