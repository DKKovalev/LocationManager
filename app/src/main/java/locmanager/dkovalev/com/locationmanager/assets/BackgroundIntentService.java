package locmanager.dkovalev.com.locationmanager.assets;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;


public class BackgroundIntentService extends IntentService {

    public static final String ACTION = "package locmanager.dkovalev.com.locationmanager.assets";
    private final Handler handler = new Handler();
    Intent intent1;

    private double lat;
    private double lng;

    private Boolean isCoordsFound;

    private NewProfile newProfile;

    public BackgroundIntentService() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        isCoordsFound = checkCoords();

        handler.removeCallbacks(updateUI);
        handler.postDelayed(updateUI, 1000);

        intent1 = new Intent(ACTION);
        intent1.putExtra("resultCode", Activity.RESULT_OK);
        intent1.putExtra("resultValueLat", lat);
        intent1.putExtra("resultValueLng", lng);
        intent1.putExtra("coordsFound", isCoordsFound);
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
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        intent1 = new Intent(ACTION);
    }

    private Boolean checkCoords() {
        Boolean placeFound = false;

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
}
