package locmanager.dkovalev.com.locationmanager.assets;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import locmanager.dkovalev.com.locationmanager.R;
import locmanager.dkovalev.com.locationmanager.activities.MainActivity;

public class LocationUpdateService extends Service {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private AudioManager audioManager;
    private YAnotherLocationHandler locationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        locationHandler = new YAnotherLocationHandler();
        locationHandler.context = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Toast.makeText(this, "Created", Toast.LENGTH_LONG).show();
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        continuouslyChecking();
        locationHandler.buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationHandler.connectToGoogleApi();
        return super.onStartCommand(intent, flags, startId);
    }

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void continuouslyChecking() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }

    private void createNotification(Context context, Double lat, Double lng) {
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Notification")
                .setContentText(String.valueOf(lat) + ' ' + String.valueOf(lng))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    private class YAnotherLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        protected GoogleApiClient googleApiClient;
        protected Location location;

        public Context context;

        @Override
        public void onConnected(Bundle bundle) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                createNotification(LocationUpdateService.this, location.getLatitude(), location.getLongitude());
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

        public void connectToGoogleApi() {
            googleApiClient.connect();
        }
    }
}
