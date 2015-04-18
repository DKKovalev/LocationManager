package locmanager.dkovalev.com.locationmanager.assets;

import java.util.List;

public class Profile {
    private double lat;
    private double lng;

    private String name;
    private List<String> settings;
    private List<Double> coords;

    public Profile(List<Double> coords, List<String> settings, String name) {
        this.coords = coords;
        this.settings = settings;
        this.name = name;
    }

    public Profile() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSettings() {
        return settings;
    }

    public void setSettings(List<String> settings) {
        this.settings = settings;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        return "Lat " + lat + '\'' + "lng " + lng + '\'' + "name " + name;
    }
}
