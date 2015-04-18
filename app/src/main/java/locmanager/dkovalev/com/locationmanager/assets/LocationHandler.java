package locmanager.dkovalev.com.locationmanager.assets;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class LocationHandler implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation;

    public Context context;

    private List<Double> coords;

    private Double lat;
    private Double lng;

    public synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    public void onStart() {

        googleApiClient.connect();
    }

    public void onResume() {

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void onPause() {
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    public void onStop() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (currentLocation != null) {
                /*coords.add(currentLocation.getLatitude());
                coords.add(currentLocation.getLongitude());*/
            }
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (currentLocation != null) {
            coords = new ArrayList<>();
            coords.add(currentLocation.getLatitude());
            coords.add(currentLocation.getLongitude());

            lat = currentLocation.getLatitude();
            lng = currentLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public List<Double> getCoords() {
        return coords;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }


}