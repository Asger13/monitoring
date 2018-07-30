package com.monitoring.rest.DAO;

import com.monitoring.rest.Model.AgroField;
import com.monitoring.rest.Model.Client;
import com.monitoring.rest.Model.LastAllCoordinates;

import com.monitoring.rest.Model.Transport;

import java.util.List;

public interface InterfaceDAO {

    public Transport getTransport(String imei);
    public List<LastAllCoordinates> list();
    public List<LastAllCoordinates> listOneClient(int id);
    public List<AgroField> getAgroFiledsClient (String keyName);
    public List<Client> getListClients ();
    public void addClient(Client client);
    public List<Transport> getTransportClient(int clientId);
    public double getAreaField(int fieldId);
}
