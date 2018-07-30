package com.monitoring.rest.Model;

public class Transport {

    private String type;
    private String name;
    private String imei;
    private String gosnumber;

    public Transport(){

    }

    public Transport(String type, String name, String imei, String gosnumber){
        this.type = type;
        this.name = name;
        this.imei = imei;
        this.gosnumber = gosnumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrackerIMEI(String imei) {
        this.imei = imei;
    }

    public void setGosnumber(String gosnumber) {
        this.gosnumber = gosnumber;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getTrackerIMEI() {
        return imei;
    }

    public String getGosnumber() {
        return gosnumber;
    }

}
