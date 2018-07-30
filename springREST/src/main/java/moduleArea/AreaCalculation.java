package moduleArea;

import worldwind.geom.Angle;
import worldwind.geom.coords.UTMCoord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AreaCalculation {

    private Connection conn;
    private List<Map> dataset = new ArrayList<>();
    private List<Map> result = new ArrayList<>();
    private List<String> str = new ArrayList<>();
    private UTMCoord utmCoord;

    public UTMCoord getUTMCoord( double latitude, double longitude )
    {
        UTMCoord utmCoord = UTMCoord.fromLatLon(Angle.fromDegreesLatitude(latitude), Angle.fromDegreesLongitude(longitude));
        return utmCoord;

    }

    public void testConnection() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

                e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/test", "postgres",
                    "11");

        } catch (SQLException e) {
               e.printStackTrace();
        }
    }

    public List<Map> Request(){
        try {
        Double.parseDouble("24.11122311");
        testConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, date_time, id_driver, id_tow, ST_AsText(location), speed, power, input_raw_5, input_raw_6, input_raw_7, input_raw_8, input_raw_9, input_raw_10, input_raw_1, input_raw_2, input_raw_3, input_raw_4, input_value_1, input_value_2, input_value_3, input_value_4, heading, distance, is_valid, inputs_converted, receive_time, fuel, transport_id, filtered_fuel, filtered_speed, input_value_5, input_value_6, input_value_7, input_value_8, input_value_9, input_value_10, field_id, campaign_id\n" +
                    "\tFROM monitoring_1.point\n" +
                    "\tORDER BY id DESC LIMIT 2");
            while (rs.next()) {

                String sdada = rs.getString(5);
                String replaced = sdada.replaceAll("[()]", "");
                String replaced1 = replaced.replaceAll("[a-zA-Zа-яА-Я]*", "");
                String[] check = replaced1.split(" ");
                double rnd = 125 + (int)(Math.random() *2 + 3) + Math.random();
                dataset.add(new Map(Double.parseDouble(check[0]),Double.parseDouble(check[1]),rnd));
                check = null;
                }
            rs.close();
            st.close();

            for(int i=0;i<dataset.size();i++){
                utmCoord = getUTMCoord(dataset.get(i).latitude,dataset.get(i).longitude);
                result.add(new Map(utmCoord.getEasting(),utmCoord.getNorthing(),dataset.get(i).attitude));
            }
            return result;
        }
        catch (SQLException e){
            e.printStackTrace();
            return  null;
        }
    }


}
