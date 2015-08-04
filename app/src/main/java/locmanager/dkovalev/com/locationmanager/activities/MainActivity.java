package locmanager.dkovalev.com.locationmanager.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.assets.BackgroundIntentService;
import locmanager.dkovalev.com.locationmanager.assets.CustomAdapter;
import locmanager.dkovalev.com.locationmanager.assets.DBHandler;
import locmanager.dkovalev.com.locationmanager.assets.NewProfile;
import locmanager.dkovalev.com.locationmanager.assets.Settings;

public class MainActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static int GET_NEW_PROFILE = 0;

    private ListView placesListView;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location location;

    private double lat;
    private double lng;

    List<NewProfile> newProfiles;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;

    private BroadcastReceiver receiver;
    private CustomAdapter profileArrayAdapter;

    private AudioManager audioManager;
    private WifiManager wifiManager;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DBHandler();
        newProfiles = dbHandler.getNewProfiles();
        profileArrayAdapter = new CustomAdapter(MainActivity.this, R.layout.list_item, newProfiles);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        setupUI();
        //showAll();
        buildGoogleAPIClient();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
                if (resultCode == RESULT_OK) {
                    double resultValueLat = intent.getDoubleExtra("resultValueLat", 0.0);
                    double resultValueLng = intent.getDoubleExtra("resultValueLng", 0.0);
                    Boolean isCoordsFound = intent.getBooleanExtra("coordsFound", false);
                    String[] settings = intent.getStringArrayExtra("settings");
                    if (isCoordsFound) {
                        handleProfileWithExactCoords(resultValueLat, resultValueLng);
                    } else {
                        handleProfileWithRangedCoords(settings[0], settings[1]);
                    }
                    createNotification(MainActivity.this, resultValueLat, resultValueLng, isCoordsFound);
                    startLocationUpdates();
                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BackgroundIntentService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        createLocationRequest();
    }

    private void setupUI() {

        placesListView = (ListView) findViewById(R.id.lv_profiles);
        placesListView.setAdapter(profileArrayAdapter);
        placesListView.setEmptyView(findViewById(R.id.tv_list_view_is_empty));

        FloatingActionButton fab_addNewPlace = (FloatingActionButton) findViewById(R.id.fab_add_new_place);
        fab_addNewPlace.attachToListView(placesListView);
        fab_addNewPlace.setType(FloatingActionButton.TYPE_NORMAL);
        fab_addNewPlace.setColorNormal(getResources().getColor(R.color.primary));
        fab_addNewPlace.setColorPressed(getResources().getColor(R.color.primary_dark));
        fab_addNewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddNewPlaceActivity();
            }
        });

        FloatingActionButton fab_showMap = (FloatingActionButton) findViewById(R.id.fab_show_map);
        fab_showMap.attachToListView(placesListView);
        fab_showMap.setType(FloatingActionButton.TYPE_NORMAL);
        fab_showMap.setColorNormal(getResources().getColor(R.color.primary));
        fab_showMap.setColorPressed(getResources().getColor(R.color.primary_dark));
        fab_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
    }

    private synchronized void buildGoogleAPIClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (location == null) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {

                lat = location.getLatitude();
                lng = location.getLongitude();

                startBackgroundService();
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
        this.location = location;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();

            startBackgroundService();
        }
    }

    private void startBackgroundService() {
        Intent background = new Intent(this, BackgroundIntentService.class);
        background.putExtra("lat", lat);
        background.putExtra("lng", lng);
        startService(background);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(BackgroundIntentService.ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void createNotification(Context context, Double lat, Double lng, Boolean coordsFound) {
        String isFound = "Nope";

        if (coordsFound) {
            isFound = "Yeah";
        }


        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Notification")
                .setContentText(String.valueOf(lat + " " + lng + " " + isFound))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    private void startAddNewPlaceActivity() {
        Intent intent = new Intent(MainActivity.this, AddNewPlaceActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivityForResult(intent, GET_NEW_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_NEW_PROFILE:
                    String title = data.getStringExtra("title");
                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lng = data.getDoubleExtra("lng", 0.0);
                    String soundSettings = data.getStringExtra("soundSettings");
                    String wirelessSettings = data.getStringExtra("wirelessSettings");

                    int radius = 0;

                    Settings settings = new Settings(wirelessSettings, soundSettings);
                    settings.save();

                    NewProfile newProfile = new NewProfile(title, lat, lng, radius, settings);
                    newProfile.save();

                    profileArrayAdapter.add(newProfile);
                    profileArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private void startMapActivity() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleProfileWithExactCoords(double lat, double lng) {
        NewProfile newProfile = dbHandler.getNewProfileByLatLng(lat, lng);
        try {
            String soundSetting = newProfile.settings.soundSetting;
            String wirelessSetting = newProfile.settings.wirelessSetting;

            switch (soundSetting) {
                case "loud":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
                case "silent":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
                case "vibrate":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                default:
                    break;

            }
            switch (wirelessSetting){
                case "wifi_off":
                    wifiManager.setWifiEnabled(false);
                    break;
                case "wifi_on":
                    wifiManager.setWifiEnabled(true);
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void handleProfileWithRangedCoords(String soundSettings, String wirelessSettings) {
        try {
            switch (soundSettings) {
                case "loud":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
                case "silent":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
                case "vibrate":
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                default:
                    break;
            }
            switch (wirelessSettings){
                case "wifi_off":
                    wifiManager.setWifiEnabled(false);
                    break;
                case "wifi_on":
                    wifiManager.setWifiEnabled(true);
                    break;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showAll() {
        Select select = new Select();
        newProfiles = select.all().from(NewProfile.class).execute();
        StringBuilder stringBuilder = new StringBuilder();
        for (NewProfile newProfile : newProfiles) {
            stringBuilder.append("Name: ")
                    .append(newProfile.title)
                    .append("Lat: ")
                    .append(newProfile.lat)
                    .append("Lng: ")
                    .append(newProfile.lng)
                    .append("Radius: ")
                    .append(newProfile.radius)
                    .append("Settings: ")
                    .append(newProfile.settings)
                    .append("\n");
        }
        Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
    }
}
