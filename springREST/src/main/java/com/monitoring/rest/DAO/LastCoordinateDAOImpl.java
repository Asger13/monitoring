package com.monitoring.rest.DAO;

import com.monitoring.rest.Model.LastCoordinate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;


import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class LastCoordinateDAOImpl implements LastCoordinateDAO{

    private JdbcTemplate jdbcTemplate;

    public LastCoordinateDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    //последняя координата конкретного ТС по его IMEI
    @Override
    public LastCoordinate get(String imei) {
        String sql = "SELECT id, ST_X(coordinate) AS longtitude, ST_Y(coordinate) AS latitude, attitude FROM point WHERE imei=" + imei+" ORDER BY id DESC LIMIT 1 ";
        return jdbcTemplate.query(sql, new ResultSetExtractor<LastCoordinate>() {

            @Override
            public LastCoordinate extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                if (rs.next()) {
                    LastCoordinate lastCoordinate = new LastCoordinate();
                    lastCoordinate.setLongtitude(rs.getDouble("longtitude"));
                    lastCoordinate.setLattitude(rs.getDouble("latitude"));
                    lastCoordinate.setAttitude(rs.getDouble("attitude"));
                    //lastCoordinate.setNavigationDate(rs.getTimestamp("navigationdate"));
                    return lastCoordinate;
                }
                return null;
            }
        });
    }

    //вывод координат по конкретному imei за определенный период
    @Override
    public List<LastCoordinate> list(String imei, BigInteger unixtime1, BigInteger unixtime2 ) {

        String sql = "SELECT ST_X(coordinate) AS longtitude, ST_Y(coordinate) AS latitude, attitude, navigationdate FROM point WHERE imei = '"+imei+"' AND navigationdate BETWEEN to_timestamp("+unixtime1+") AND to_timestamp("+unixtime2+") ORDER BY navigationdate ASC";
        List<LastCoordinate> listCoordinates = jdbcTemplate.query(sql, new RowMapper<LastCoordinate>() {

            @Override
            public LastCoordinate mapRow(ResultSet rs, int rowNum) throws SQLException {
                LastCoordinate lastCoordinateForList = new LastCoordinate();
                lastCoordinateForList.setLongtitude(rs.getDouble("longtitude"));
                lastCoordinateForList.setLattitude(rs.getDouble("latitude"));
                lastCoordinateForList.setAttitude(rs.getDouble("attitude"));
                //lastCoordinateForList.setNavigationDate(rs.getTimestamp("navigationdate"));
                return lastCoordinateForList;
            }
        });
        return listCoordinates;
    }

    }






