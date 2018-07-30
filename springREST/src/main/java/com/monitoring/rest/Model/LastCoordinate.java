package com.monitoring.rest.Model;

import java.sql.Timestamp;

public class LastCoordinate {

    private double longtitude;
    private double lattitude;
    private double attitude;
    //private Timestamp navigationDate;

    public LastCoordinate(){

    }

    public LastCoordinate(double longtitude, double lattitude, double attitude /*Timestamp navigationDate*/){
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.attitude = attitude;
        //this.navigationDate = navigationDate;
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

    /*public void setNavigationDate(Timestamp navigationDate) {
        this.navigationDate = navigationDate;
    }*/


}

