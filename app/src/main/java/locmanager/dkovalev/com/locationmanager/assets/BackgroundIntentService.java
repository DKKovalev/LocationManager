package locmanager.dkovalev.com.locationmanager.assets;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.query.Select;

import java.util.List;


public class BackgroundIntentService extends IntentService {

    public static final String ACTION = "package locmanager.dkovalev.com.locationmanager.assets";
    private final Handler handler = new Handler();
    Intent intent1;

    private double lat;
    private double lng;

    private Boolean isCoordsFound;

    private Boolean isCoordsFound1;

    private MathHandler mathHandler;

    public BackgroundIntentService() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        isCoordsFound = checkExactCoords();
        isCoordsFound1 = false;

        handler.removeCallbacks(updateUI);
        handler.postDelayed(updateUI, 1000);

        intent1 = new Intent(ACTION);
        intent1.putExtra("resultCode", Activity.RESULT_OK);
        intent1.putExtra("resultValueLat", lat);
        intent1.putExtra("resultValueLng", lng);
        intent1.putExtra("coordsFound", isCoordsFound);
        intent1.putExtra("settings", checkRangedCoords());
        intent1.putExtra("isCoordsFound", isCoordsFound1);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
    }

    private Runnable updateUI = new Runnable() {
        @Override
        public void run() {

            handler.removeCallbacks(updateUI);
            handler.postDelayed(updateUI, 1000);

            intent1 = new Intent(ACTION);
            intent1.putExtra("resultCode", Activity.RESULT_OK);
            intent1.putExtra("resultValueLat", lat);
            intent1.putExtra("resultValueLng", lng);
            intent1.putExtra("coordsFound", isCoordsFound);
            intent1.putExtra("isCoordsFound", isCoordsFound1);
            intent1.putExtra("settings", checkRangedCoords());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        intent1 = new Intent(ACTION);
    }

    private Boolean checkExactCoords() {
        Boolean placeFound;

        try {
            Double latToFind = getProfileByLat().lat;
            Double lngToFind = getProfileByLng().lng;
            if (lat == latToFind && lng == lngToFind) {
                placeFound = true;
            } else {
                placeFound = false;
            }
            return placeFound;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] checkRangedCoords() {

        String[] settings = new String[2];
        mathHandler = new MathHandler();

        for (NewProfile newProfile : getNewProfiles()) {
            double distance = mathHandler.getDistance(lat, lng, newProfile.lat, newProfile.lng);
            if (distance < 500) {

                isCoordsFound = true;
                settings[0] = newProfile.settings.soundSetting;
                settings[1] = newProfile.settings.wirelessSetting;
            }
        }
        return settings;
    }

    private NewProfile getProfileByLat() {
        try {
            return new Select().from(NewProfile.class).where("lat = ?", lat).executeSingle();
        } catch (Exception e) {
            return null;
        }
    }

    private NewProfile getProfileByLng() {
        try {
            return new Select().from(NewProfile.class).where("lng = ?", lng).executeSingle();
        } catch (Exception e) {
            return null;
        }
    }

    private List<NewProfile> getNewProfiles() {
        return new Select().all().from(NewProfile.class).execute();
    }
}
