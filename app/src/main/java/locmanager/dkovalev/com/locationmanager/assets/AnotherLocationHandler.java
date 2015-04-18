package locmanager.dkovalev.com.locationmanager.assets;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class AnotherLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient googleApiClient;
    protected Location location;

    private Double lat;
    private Double lng;

    private List<Double> coords;

    public Context context;

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public Double getLng() {
        return lng;
    }

    public Double getLat() {
        return lat;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void connectToGoogleApi(){
        googleApiClient.connect();
    }
}
