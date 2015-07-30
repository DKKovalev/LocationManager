package locmanager.dkovalev.com.locationmanager.activities;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.assets.Profile;

public class MapsActivity extends FragmentActivity {

    private GoogleMap googleMap;

    private Profile profile;

    private HashMap<Marker, Profile> markerProfileHashMap;
    private ArrayList<Profile> profiles;
    private ArrayList<String> settings;

    private double lat;
    private double lng;

    private Circle circle;

    private List<Double> coords;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        profiles = new ArrayList<Profile>();
        settings = new ArrayList<>();

        profile = new Profile();

        settings.add("placeholder");
        profile.setSettings(settings);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coords = new ArrayList<>();
            lat = bundle.getDouble("lat", 0.0);
            lng = bundle.getDouble("lng", 0.0);
            coords.add(lat);
            coords.add(lng);
        }

        setUpMapIfNeeded();

        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                float[] distance = new float[2];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), circle.getCenter().latitude, circle.getCenter().longitude, distance);
                if (distance[0] < circle.getRadius()) {
                    Toast.makeText(getBaseContext(), "'Ello: " + distance[0] + " radius: " + circle.getRadius(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        markerProfileHashMap = new HashMap<>();
        profiles.add(new Profile(lat, lng, settings, "Hello", 1000));

        plotMarkers(profiles);
    }

    private void plotMarkers(ArrayList<Profile> profiles) {
        if (profiles.size() > 0) {
            for (final Profile profile : profiles) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(profile.getLat(), profile.getLng()));

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(profile.getLat(), profile.getLng()))
                        .radius(1000)
                        .fillColor(Color.parseColor("#50ff0000"))
                        .strokeWidth(0);

                circle = googleMap.addCircle(circleOptions);

                marker = googleMap.addMarker(markerOptions);
                markerProfileHashMap.put(marker, profile);
            }
        }
    }
}
