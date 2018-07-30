package moduleArea;

public class Map {

    public double latitude;
    public double longitude;
    public double attitude;


    public Map(){

    }

    public Map(double latitude, double longitude, double attitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.attitude = attitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAttitude() {
        return attitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAttitude(double attitude) {
        this.attitude = attitude;
    }
}
