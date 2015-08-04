package locmanager.dkovalev.com.locationmanager.activities;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.assets.DBHandler;
import locmanager.dkovalev.com.locationmanager.assets.NewProfile;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private HashMap<Marker, NewProfile> markerNewProfileHashMap;
    private List<NewProfile> newProfiles;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbHandler = new DBHandler();
        newProfiles = dbHandler.getNewProfiles();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }

            mMap.setMyLocationEnabled(true);

        }
    }

    private void setUpMap() {
        markerNewProfileHashMap = new HashMap<>();
        plotMarkers(newProfiles);
    }

    private void plotMarkers(List<NewProfile> newProfiles) {
        if (newProfiles.size() > 0) {
            for (NewProfile newProfile : dbHandler.getNewProfiles()) {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(newProfile.lat, newProfile.lng));
                Marker marker = mMap.addMarker(markerOptions);
                markerNewProfileHashMap.put(marker, newProfile);
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(newProfile.lat, newProfile.lng))
                        .radius(500)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(1f)
                        .fillColor(Color.argb(50, 50, 50, 50)));
            }
        }
    }
}
