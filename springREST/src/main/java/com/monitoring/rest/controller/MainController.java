package com.monitoring.rest.controller;

import com.monitoring.rest.DAO.InterfaceDAO;
import com.monitoring.rest.DAO.LastCoordinateDAO;
import com.monitoring.rest.Model.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping(value = "/myservice")
public class MainController {

    @Autowired
    private LastCoordinateDAO lastCoordinateDAO;

    @Autowired
    private InterfaceDAO interfaceDAO;

    Gson gson = new Gson();
    private static final String produces = "application/json;charset=UTF-8";
    private static final String origin = "http://localhost:4200";

    /*@RequestMapping(value= "/{time}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public MyDataObject getMyData(@PathVariable long time) {
        return new MyDataObject(Calendar.getInstance(), "Это ответ метода GET!");
    }*/

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/coordinate/{imei}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public String getMyDataString(@PathVariable String imei) {
        LastCoordinate lastCoordinate = lastCoordinateDAO.get(imei);
        String json = gson.toJson(lastCoordinate);
        return json;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/transport/{imei}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public String getMyDataTransport(@PathVariable String imei) {
        Transport transport = interfaceDAO.getTransport(imei);
        String json = gson.toJson(transport);
        return json;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/coordinate/{imei}/{unixtime1}/{unixtime2}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public String getMyDataList(@PathVariable String imei, @PathVariable BigInteger unixtime1,  @PathVariable BigInteger unixtime2) {
        List<LastCoordinate> listCoordinate = lastCoordinateDAO.list(imei,unixtime1, unixtime2);
        String json = gson.toJson(listCoordinate);
        return json;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/coordinate/all", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public String getMyDataListAll() {
        List<LastAllCoordinates> listCoordinateAll = interfaceDAO.list();
        String json = gson.toJson(listCoordinateAll);
        return json;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/transports/{id}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public String getMyDataListOneClient(@PathVariable int id) {
        List<LastAllCoordinates> listCoordinateAll = interfaceDAO.listOneClient(id);
        String json = gson.toJson(listCoordinateAll);
        return json;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/client", method = RequestMethod.GET,  produces = produces)
    @ResponseBody
    public List<Client> getMyDataListClient() {
        List<Client> clientList = interfaceDAO.getListClients();
        return clientList;
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/transport/client/{id}", method = RequestMethod.GET,  produces = produces)
    @ResponseBody
    public List<Transport> getTransportForClient(@PathVariable int id) {
        List<Transport> transportList = interfaceDAO.getTransportClient(id);
        return transportList;
    }
    //отправка не в json вызывает ошибку, скорее всего из за типа double в area
    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/client/field/{key_name}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public  String getMyDataAgroFieldList(@PathVariable String key_name) {
        List<AgroField> agroFieldList = interfaceDAO.getAgroFiledsClient(key_name);
        String json = gson.toJson(agroFieldList);
        return json;
    }
    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/agrofield/area/{id}", method = RequestMethod.GET, produces = produces)
    @ResponseBody
    public  double getAreaField(@PathVariable int id) {
        return interfaceDAO.getAreaField(id);
    }

    @CrossOrigin(origins = origin)
    @RequestMapping(value= "/client/add", method = RequestMethod.POST)
    @ResponseBody
    public String addClient(@RequestBody Client client) {
        interfaceDAO.addClient(client);
        return "complete";
    }

}
