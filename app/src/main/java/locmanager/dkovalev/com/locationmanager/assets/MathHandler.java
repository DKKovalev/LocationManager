package locmanager.dkovalev.com.locationmanager.assets;

public class MathHandler {

    public Double getDistance(double userLat, double userLng, double savedLat, double savedLng) {
        double theta = userLng - savedLng;

        double distance = Math.sin(degToRad(userLat))
                * Math.sin(degToRad(savedLat))
                + Math.cos(degToRad(userLat))
                * Math.cos(degToRad(savedLat))
                * Math.cos(degToRad(theta));
        distance = Math.acos(distance);
        distance = degToRad(distance);
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344;

        return distance;
    }

    private double degToRad(double rad) {
        return (rad * 180 / Math.PI);
    }
}
