package com.monitoring.rest.Model;

import java.math.BigInteger;
import java.sql.Timestamp;

public class LastAllCoordinates {

    private double longtitude;
    private double lattitude;
    private double attitude;
    private String imei;
    private long navigationtime;

    public LastAllCoordinates(){

    }

    public LastAllCoordinates(double longtitude, double lattitude, double attitude, String imei, long navigationtime){
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.attitude = attitude;
        this.imei = imei;
        this.navigationtime = navigationtime;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setAttitude(double attitude) {
        this.attitude = attitude;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setNavigationtime(long navigationtime) {
        this.navigationtime = navigationtime;
    }


}
