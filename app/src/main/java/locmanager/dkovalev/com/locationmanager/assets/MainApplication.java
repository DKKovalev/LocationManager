package locmanager.dkovalev.com.locationmanager.assets;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
