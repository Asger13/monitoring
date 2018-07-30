package com.monitoring.rest.DAO;

import com.monitoring.rest.Model.Client;
import com.monitoring.rest.Model.Transport;
import com.monitoring.rest.Model.AgroField;
import com.monitoring.rest.Model.LastAllCoordinates;
import moduleArea.Map;
import coords.PointSet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import worldwind.geom.Angle;
import worldwind.geom.coords.UTMCoord;


import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class InterfaceDAOImpl implements InterfaceDAO {

    private JdbcTemplate jdbcTemplate;
    private PointSet ps;

    public InterfaceDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    //получение транспорта по Imei
    @Override
    public Transport getTransport(String imei) {
        String sql = "SELECT transport.type, transport.name, tracker.tracker_model FROM tracker,transport WHERE imei = " + imei + " AND id_transport=transport.id";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Transport>() {

            @Override
            public Transport extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                if (rs.next()) {
                    Transport transport = new Transport();
                    transport.setType(rs.getString("type"));
                    transport.setName(rs.getString("name"));
                    transport.setGosnumber(rs.getString("gosnumber"));
                    return transport;
                }
                return null;
            }

        });
    }
//последние координаты каждого транспорта каждого клиента
    @Override
    public List<LastAllCoordinates> list() {
        String sql = "select imei, id, ST_X(coordinate) AS latitude, ST_Y(coordinate) AS longtitude, attitude from point where id in (select  max(id) from point group by imei)";
        List<LastAllCoordinates> listCoordinatesAll = jdbcTemplate.query(sql, new RowMapper<LastAllCoordinates>() {

            @Override
            public LastAllCoordinates mapRow(ResultSet rs, int rowNum) throws SQLException {
                LastAllCoordinates lastAllCoordinates = new LastAllCoordinates();
                lastAllCoordinates.setLongtitude(rs.getDouble("longtitude"));
                lastAllCoordinates.setLattitude(rs.getDouble("latitude"));
                lastAllCoordinates.setAttitude(rs.getDouble("attitude"));
                lastAllCoordinates.setImei(rs.getString("imei"));
                return lastAllCoordinates;
            }
        });

        return listCoordinatesAll;
    }

//последние координаты всех транспортныъ средств заданного клиента
    @Override
    public List<LastAllCoordinates> listOneClient(int id) {
        String sql = "select imei, id, navigationdate, ST_X(coordinate) AS longtitude, ST_Y(coordinate) AS latitude, attitude from point where id in (select  max(id) from point group by imei) and imei in (select imei from tracker where id_client=" + id + ")";
        List<LastAllCoordinates> listCoordinatesAll = jdbcTemplate.query(sql, new RowMapper<LastAllCoordinates>() {

            @Override
            public LastAllCoordinates mapRow(ResultSet rs, int rowNum) throws SQLException {
                LastAllCoordinates lastAllCoordinates = new LastAllCoordinates();
                lastAllCoordinates.setLongtitude(rs.getDouble("longtitude"));
                lastAllCoordinates.setLattitude(rs.getDouble("latitude"));
                lastAllCoordinates.setAttitude(rs.getDouble("attitude"));
                lastAllCoordinates.setImei(rs.getString("imei"));
                Date date = new Date(rs.getTimestamp("navigationdate").getTime());
                long dateMillisec = date.getTime();
                lastAllCoordinates.setNavigationtime(dateMillisec);
                return lastAllCoordinates;
            }
        });
        return listCoordinatesAll;
    }

//получение списка клиентов
    @Override
    public List<Client> getListClients() {
        String sql = "SELECT * FROM client";
        List<Client> clientList = jdbcTemplate.query(sql, new RowMapper<Client>() {

            @Override
            public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setName(rs.getString("name"));
                client.setKeyName(rs.getString("key_name"));
                return client;
            }
        });
        return clientList;
    }

//получение полей клиентов по заданному ключевому названию клиента
    @Override
    public List<AgroField> getAgroFiledsClient(String keyName) {
        String sql = "SELECT id, name, area, renter\n" +
                "FROM public.agro_field \n" +
                "WHERE id_subdivision in (SELECT id FROM subdivision WHERE id_client in (SELECT id FROM client WHERE key_name ='" + keyName + "') )";
        List<AgroField> agroFieldList = jdbcTemplate.query(sql, new RowMapper<AgroField>() {

            @Override
            public AgroField mapRow(ResultSet rs, int rowNum) throws SQLException {
                AgroField agroField = new AgroField();
                agroField.setIdentifier(rs.getInt("id"));
                agroField.setName(rs.getString("name"));
                agroField.setArea(rs.getDouble("area"));
                agroField.setRenter(rs.getString("renter"));
                return agroField;
            }
        });
        return agroFieldList;
    }

   //добавление клиента
    @Override
    public void addClient(Client client) {
            String sql = "INSERT INTO client (name,key_name)"
                    + " VALUES (?, ?)";
            jdbcTemplate.update(sql, client.getName(), client.getKeyName());
    }
//получение списка ТС по заданному id клиента
    @Override
    public List<Transport> getTransportClient(int clientId) {
        String sql = "SELECT transport.type, transport.name, tracker.imei, transport.gosnumber FROM tracker,transport WHERE transport.id=tracker.id_transport and transport.id_client="+clientId;
        List<Transport> transportList = jdbcTemplate.query(sql, new RowMapper<Transport>() {

            @Override
            public Transport mapRow(ResultSet rs, int rowNum) throws SQLException {
                Transport transport = new Transport();
                transport.setType(rs.getString("type"));
                transport.setName(rs.getString("name"));
                transport.setTrackerIMEI(rs.getString("imei"));
                transport.setGosnumber(rs.getString("gosnumber"));
                return transport;
            }
        });
        return transportList;
    }

    @Override
    public double getAreaField(int fieldId) {
        String sql = "SELECT ST_X(coordinate) AS longtitude, ST_Y(coordinate) AS latitude, attitude  FROM point WHERE  point.id_agro_field="+fieldId;
        List<Map> mapList = jdbcTemplate.query(sql, new RowMapper<Map>() {

            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map mapSet = new Map();
                mapSet.setLatitude(rs.getDouble("latitude"));
                mapSet.setLongitude(rs.getDouble("longtitude"));
                mapSet.setAttitude(rs.getDouble("attitude"));
                return mapSet;
            }
        });

        UTMCoord utmCoord;
        List<Map> newMapList = new ArrayList<>();

        for(int i=0;i<mapList.size();i++){
            utmCoord = getUTMCoord(mapList.get(i).latitude,mapList.get(i).longitude);
            newMapList.add(new Map(utmCoord.getEasting(),utmCoord.getNorthing(),mapList.get(i).attitude));
        }
        double summa = new PointSet().setPoint(newMapList);
        double newDouble = new BigDecimal(summa).setScale(3, RoundingMode.UP).doubleValue();
        return newDouble;
    }

    public UTMCoord getUTMCoord( double latitude, double longitude )
    {
        UTMCoord utmCoord = UTMCoord.fromLatLon(Angle.fromDegreesLatitude(latitude), Angle.fromDegreesLongitude(longitude));
        return utmCoord;

    }

}



