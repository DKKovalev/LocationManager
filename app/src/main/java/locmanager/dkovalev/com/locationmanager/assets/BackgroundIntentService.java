package locmanager.dkovalev.com.locationmanager.assets;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;


public class BackgroundIntentService extends IntentService {

    public static final String ACTION = "package locmanager.dkovalev.com.locationmanager.assets";
    private final Handler handler = new Handler();
    Intent intent1;

    private double lat;
    private double lng;

    public BackgroundIntentService() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        handler.removeCallbacks(updateUI);
        handler.postDelayed(updateUI, 1000);

        intent1 = new Intent(ACTION);
        intent1.putExtra("resultCode", Activity.RESULT_OK);
        intent1.putExtra("resultValueLat", lat);
        intent1.putExtra("resultValueLng", lng);
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
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        intent1 = new Intent(ACTION);
    }
}
