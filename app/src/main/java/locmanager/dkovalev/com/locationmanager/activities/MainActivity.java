package locmanager.dkovalev.com.locationmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import locmanager.dkovalev.com.locationmanager.assets.AnotherLocationHandler;
import locmanager.dkovalev.com.locationmanager.assets.GeocodingAsset;
import locmanager.dkovalev.com.locationmanager.assets.LocationUpdateService;
import locmanager.dkovalev.com.locationmanager.R;

public class MainActivity extends ActionBarActivity {

    private Button showLatLngButton;
    private Button setProfileSilentButton;
    private Button setProfileLoudButton;
    private Button setProfileVibrateButton;
    private EditText addressFieldText;
    private TextView latlngTextView;
    private String address;
    private AudioManager audioManager;

    private TextView latTV;
    private TextView lngTV;
    protected Location location;
    private Double lat;
    private Double lng;
    private YAnotherLocationHandler yAnotherLocationHandler;

    private Button newActivityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        showLatLngButton = (Button) findViewById(R.id.button_show_latlng);
        setProfileSilentButton = (Button) findViewById(R.id.button_profile_silent);
        setProfileLoudButton = (Button) findViewById(R.id.button_profile_loud);
        setProfileVibrateButton = (Button) findViewById(R.id.button_profile_vibrate);


        yAnotherLocationHandler = new YAnotherLocationHandler();
        latTV = (TextView) findViewById(R.id.textView2);
        lngTV = (TextView) findViewById(R.id.textView3);
        yAnotherLocationHandler.context = this;
        yAnotherLocationHandler.buildGoogleApiClient();

        newActivityButton = (Button) findViewById(R.id.button);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        addressFieldText = (EditText) findViewById(R.id.edittext_address_field);
        latlngTextView = (TextView) findViewById(R.id.textView);

        newActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewPlaceActivity.class);
                startActivity(intent);
            }
        });

        showLatLngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = addressFieldText.getText().toString();
                GeocodingAsset geocodingAsset = new GeocodingAsset();
                geocodingAsset.getLatLngFromAddress(address, getApplicationContext(), new GeocoderHandler());
            }
        });

        setProfileSilentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(MainActivity.this, "Mode SILENT", Toast.LENGTH_SHORT).show();
            }
        });

        Intent locationServiceIntent = new Intent(this, LocationUpdateService.class);
        startService(locationServiceIntent);

        new Drawer()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_action_map).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_action_places).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_action_settings).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_action_about).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem != null) {
                            if (iDrawerItem.getIdentifier() == 1) {
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                    }
                })
                .build();
    }

    @Override
    protected void onStart() {

        yAnotherLocationHandler.connectToGoogleApi();
        super.onStart();
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String address;
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                    break;
                default:
                    address = null;
            }

            latlngTextView.setText(address);
        }
    }

    private class YAnotherLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        protected GoogleApiClient googleApiClient;
        protected Location location;

        public Context context;

        @Override
        public void onConnected(Bundle bundle) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                latTV.setText(String.valueOf(location.getLatitude()));
                lngTV.setText(String.valueOf(location.getLongitude()));
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            googleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        protected synchronized void buildGoogleApiClient() {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        public void connectToGoogleApi(){
            googleApiClient.connect();
        }

    }
}
