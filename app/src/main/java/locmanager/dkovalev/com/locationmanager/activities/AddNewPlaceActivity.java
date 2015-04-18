package locmanager.dkovalev.com.locationmanager.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.assets.LocationHandler;
import locmanager.dkovalev.com.locationmanager.assets.Profile;
import locmanager.dkovalev.com.locationmanager.R;

public class AddNewPlaceActivity extends ActionBarActivity {

    private EditText objectNameEditText;
    private Button createObjectByGPSButton;

    private Profile profile;

    private ArrayList<Profile> profiles;
    private ArrayList<String> settings;

    private ListView listView;

    private LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        locationHandler = new LocationHandler();
        locationHandler.context = this;

        profile = new Profile();
        profiles = new ArrayList<>();

        objectNameEditText = (EditText) findViewById(R.id.text_object_name);
        createObjectByGPSButton = (Button) findViewById(R.id.button_add_place_by_gps);

        listView = (ListView) findViewById(R.id.lv_profiles);

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, profiles);
        listView.setAdapter(arrayAdapter);

        settings = new ArrayList<>();
        settings.add("placeholder");

        profile.setSettings(settings);

        createObjectByGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectNameEditText.getText().toString() == null) {
                    Toast.makeText(AddNewPlaceActivity.this, "Enter place name", Toast.LENGTH_LONG).show();
                } else {
                    locationHandler.startLocationUpdates();

                    profiles.add(new Profile(locationHandler.getCoords(), settings, objectNameEditText.getText().toString()));

                    arrayAdapter.notifyDataSetChanged();

                    try {
                        saveProfiles(profiles);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(AddNewPlaceActivity.this, "File created", Toast.LENGTH_LONG).show();
                }
            }
        });

        locationHandler.buildGoogleApiClient();
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

    @Override
    protected void onStart() {
        super.onStart();
        locationHandler.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHandler.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHandler.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


