package locmanager.dkovalev.com.locationmanager.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.assets.Profile;
import locmanager.dkovalev.com.locationmanager.R;

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

    private Profile profile;

    private ArrayList<Profile> profiles;
    private ArrayList<String> settings;

    private double lat;
    private double lng;

    private List<Double> coords;

    private String soundProfile;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coords = new ArrayList<>();
            lat = bundle.getDouble("lat", 0.0);
            lng = bundle.getDouble("lng", 0.0);
            coords.add(lat);
            coords.add(lng);
        }

        profile = new Profile();
        profiles = new ArrayList<>();
        settings = new ArrayList<>();

        setupUI();

        Toast.makeText(AddNewPlaceActivity.this, readJSON(), Toast.LENGTH_LONG).show();
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
                //settings = new ArrayList<>();
                RadioButton soundSettingButton = (RadioButton) v;
                switch (soundSettingButton.getId()) {
                    case R.id.silent_radio_button:
                        soundProfile = "silent";
                        settings.add(soundProfile);
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.loud_radio_button:
                        soundProfile = "loud";
                        settings.add(soundProfile);
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.vibrate_radio_button:
                        soundProfile = "vibrate";
                        settings.add(soundProfile);
                        Toast.makeText(AddNewPlaceActivity.this, soundProfile, Toast.LENGTH_SHORT).show();
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
                    settings.add("wifi_off");
                }
            }
        });

        profile.setSettings(settings);

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


        profile.setRadius(radius);

        createObjectByGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(AddNewPlaceActivity.this, "Enter place name", Toast.LENGTH_LONG).show();
                } else {

                    profiles.add(new Profile(coords, settings, objectNameEditText.getText().toString(), radius));

                    try {
                        saveProfiles(profiles);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                profiles.add(new Profile(geocoding(addressEditText.getText().toString()), settings, objectNameEditText.getText().toString(), radius));
                try {
                    saveProfiles(profiles);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(AddNewPlaceActivity.this, "File created", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    //BEGIN
    private void saveProfiles(List<Profile> profiles) throws IOException {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/locman");
        dir.mkdirs();
        File file = new File(dir, "yet_another_profiles.json");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileWriter fileWriter = new FileWriter(file);
        JsonWriter jsonWriter = new JsonWriter(fileWriter);
        jsonWriter.setIndent("   ");

        writeProfilesArray(jsonWriter, profiles);
        jsonWriter.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void writeProfilesArray(JsonWriter jsonWriter, List<Profile> profiles) throws IOException {
        jsonWriter.beginArray();
        for (Profile profile : profiles) {
            writeProfile(jsonWriter, profile);
        }

        jsonWriter.endArray();
    }

    private void writeProfile(JsonWriter jsonWriter, Profile profile) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(profile.getName());
        jsonWriter.name("coords");
        writeCoordsArray(jsonWriter, profile.getCoords());
        jsonWriter.name("radius").value(profile.getRadius());
        jsonWriter.name("settings");
        writeSettings(jsonWriter, profile.getSettings());
        jsonWriter.endObject();
    }

    private void writeCoordsArray(JsonWriter jsonWriter, List<Double> coords) throws IOException {
        jsonWriter.beginArray();
        for (Double value : coords) {
            jsonWriter.value(value);
        }
        jsonWriter.endArray();
    }

    private void writeSettings(JsonWriter jsonWriter, List<String> settings) throws IOException {
        jsonWriter.beginArray();
        for (String value : settings) {
            jsonWriter.value(value);
        }
        jsonWriter.endArray();
    }

    //END

    private String readJSON() {
        String json = null;
        try {
            File root = Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/locman", "yet_another_profiles.json");
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
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

    private List<Double> geocoding(String addressName){
        Geocoder geocoder = new Geocoder(this);
        List<Double> coords;
        coords = new ArrayList<>();
        try {
            if (geocoder.isPresent()) {
                List<Address> addresses = geocoder.getFromLocationName(addressName, 1);
                Address address = addresses.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();

                coords.add(lat);
                coords.add(lng);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return coords;
    }
}


