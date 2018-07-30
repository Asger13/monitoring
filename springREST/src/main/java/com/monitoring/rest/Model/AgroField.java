package com.monitoring.rest.Model;

public class AgroField {

    private int id;
    private String name;
    private double  area;
    private String renter;

    public AgroField(){

    }

    public AgroField(int id, String name, double area,String renter ){
        this.id = id;
        this.name = name;
        this.area = area;
        this.renter = renter;
    }

    public void setIdentifier(int id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public int getIdentifier() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getArea() {
        return area;
    }

    public String getRenter() {
        return renter;
    }


}
