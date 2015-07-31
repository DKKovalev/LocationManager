package locmanager.dkovalev.com.locationmanager.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.assets.NewProfile;
import locmanager.dkovalev.com.locationmanager.assets.Profile;
import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.assets.Settings;

public class AddNewPlaceActivity extends ActionBarActivity {

    private EditText objectNameEditText;
    private Button createObjectByGPSButton;
    private Button createObjectByGeocodingButton;
    private TextView radiusText;
    private RadioButton silentRadioButton;
    private RadioButton loudRadioButton;
    private RadioButton vibrateRadioButton;
    private CheckBox wifiCheckBox;

    private View.OnClickListener onClickListener;

    private double lat;
    private double lng;

    private String soundProfile;
    private String wirelessProfile;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lat = bundle.getDouble("lat", 0.0);
            lng = bundle.getDouble("lng", 0.0);
        }
        setupUI();
    }

    private void setupUI() {

        objectNameEditText = (EditText) findViewById(R.id.text_object_name);
        createObjectByGPSButton = (Button) findViewById(R.id.button_add_place_by_gps);
        createObjectByGeocodingButton = (Button) findViewById(R.id.button_add_place_by_geocoding);
        radiusText = (TextView) findViewById(R.id.text_seek_bar_scale);
        radiusText.setText("0");


        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RadioButton soundSettingButton = (RadioButton) v;

                switch (soundSettingButton.getId()) {
                    case R.id.silent_radio_button:
                        soundProfile = "silent";
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.loud_radio_button:
                        soundProfile = "loud";
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.vibrate_radio_button:
                        soundProfile = "vibrate";
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        silentRadioButton = (RadioButton) findViewById(R.id.silent_radio_button);
        silentRadioButton.setOnClickListener(onClickListener);
        loudRadioButton = (RadioButton) findViewById(R.id.loud_radio_button);
        loudRadioButton.setOnClickListener(onClickListener);
        vibrateRadioButton = (RadioButton) findViewById(R.id.vibrate_radio_button);
        vibrateRadioButton.setOnClickListener(onClickListener);

        wifiCheckBox = (CheckBox) findViewById(R.id.check_box_wifi);
        wifiCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiCheckBox.isChecked()) {
                    wirelessProfile = "wifi_off";
                }
            }
        });

        final SeekBar setRadiusBar = (SeekBar) findViewById(R.id.seek_bar_radius);
        setRadiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusText.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                radiusText.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusText.setText(String.valueOf(seekBar.getProgress()));
                radius = seekBar.getProgress();
            }
        });

        createObjectByGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(AddNewPlaceActivity.this, "Enter place name", Toast.LENGTH_LONG).show();
                } else {
                    Settings settings = new Settings(wirelessProfile, soundProfile);
                    settings.save();

                    NewProfile newProfile = new NewProfile(objectNameEditText.getText().toString(), lat, lng, radius, settings);
                    newProfile.save();

                    Toast.makeText(AddNewPlaceActivity.this, "File created", Toast.LENGTH_LONG).show();
                }
            }
        });

        createObjectByGeocodingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddressAlertDialog();
            }
        });


    }

    private void createAddressAlertDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        Drawable drawable = new ColorDrawable(Color.BLACK);
        drawable.setAlpha(130);
        dialog.getWindow().setBackgroundDrawable(drawable);

        Button submitAddressButton = (Button) dialog.findViewById(R.id.submit_button);
        final EditText addressEditText = (EditText) dialog.findViewById(R.id.edit_text_address);

        submitAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = new Settings(soundProfile, wirelessProfile);
                settings.save();

                NewProfile newProfile = new NewProfile(objectNameEditText.getText().toString()
                        , geocoding(addressEditText.getText().toString())[0]
                        , geocoding(addressEditText.getText().toString())[1]
                        , radius
                        , settings);
                newProfile.save();

                Toast.makeText(AddNewPlaceActivity.this, "File created", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private Double[] geocoding(String addressName) {
        Geocoder geocoder = new Geocoder(this);
        Double[] coords;
        coords = new Double[2];
        try {
            if (geocoder.isPresent()) {
                List<Address> addresses = geocoder.getFromLocationName(addressName, 1);
                Address address = addresses.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();

                coords[0] = lat;
                coords[1] = lng;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coords;
    }
}


