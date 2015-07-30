package locmanager.dkovalev.com.locationmanager.assets;

import java.util.List;

public class Profile {
    private double lat;
    private double lng;



    private int radius;

    private String name;
    private List<String> settings;
    private List<Double> coords;

    public Profile(List<Double> coords, List<String> settings, String name, int radius) {
        this.coords = coords;
        this.settings = settings;
        this.name = name;
        this.radius = radius;
    }

    public Profile() {
    }

    public Profile(double lat, double lng, List<String> settings, String name, int radius) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.settings = settings;
        this.radius = radius;
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

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
