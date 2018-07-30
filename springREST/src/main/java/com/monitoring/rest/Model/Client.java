package com.monitoring.rest.Model;

public class Client {

    private int id;
    private String name;
    private String keyName;

    public Client(){

    }

    public Client(int id, String name, String keyName){
        this.id = id;
        this.name = name;
        this.keyName = keyName;
    }

    public void setId(int id){this.id = id;}

    public void setName(String name) {
        this.name = name;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }


    public int getId(){return id; }
    public String getName() {
        return name;
    }

    public String getKeyName() {
        return keyName;
    }

}
