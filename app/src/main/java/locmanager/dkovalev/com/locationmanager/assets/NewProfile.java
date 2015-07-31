package locmanager.dkovalev.com.locationmanager.assets;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "NewProfile")
public class NewProfile extends Model {
    @Column(name = "title")
    public String title;

    @Column(name = "lat")
    public Double lat;

    @Column(name = "lng")
    public Double lng;

    @Column(name = "settings")
    public Settings settings;

    @Column(name = "radius")
    public int radius;

    public NewProfile() {
        super();
    }

    public NewProfile(String title, Double lat, Double lng, int radius, Settings settings) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.settings = settings;
    }

    @Override
    public String toString() {
        return title
                + " "
                + lat
                + " "
                + lng
                + " "
                + radius
                + ""
                + settings;
    }
}
