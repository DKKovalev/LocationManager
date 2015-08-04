package locmanager.dkovalev.com.locationmanager.assets;


import com.activeandroid.query.Select;

import java.util.List;

public class DBHandler {
    public List<NewProfile> getNewProfiles() {
        return new Select().all().from(NewProfile.class).execute();
    }

    public NewProfile getNewProfileByLatLng(double lat, double lng){
        return new Select().from(NewProfile.class).where("lat =?", lat).where("lng =?", lng).executeSingle();
    }
}
