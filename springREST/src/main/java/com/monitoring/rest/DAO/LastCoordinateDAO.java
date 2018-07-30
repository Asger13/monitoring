package com.monitoring.rest.DAO;

import com.monitoring.rest.Model.LastCoordinate;

import java.math.BigInteger;
import java.util.List;

public interface LastCoordinateDAO {

    public LastCoordinate get(String imei);
    public List<LastCoordinate> list(String imei, BigInteger unixtime1, BigInteger unixtime2);
}
