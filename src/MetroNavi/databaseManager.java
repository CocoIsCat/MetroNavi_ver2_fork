/*
* databaseManager
* 데이터베이스 관련된 클래스
* */
package MetroNavi;

import java.sql.*;
import java.util.ArrayList;

class databaseManager {

    private static Connection conn = null;

    /*conn 생성*/
    public static void connectDatabase() {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String dbURL = "jdbc:mysql://localhost:3306/?user=root";
        String userName = "root";
        String password = "19980316";
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbURL, userName, password);
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
    }

    public static ArrayList<Integer> searchLineNumDB(String stationName) {
        ArrayList<Integer> dstLineNum = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT line_id FROM Subway.sub_line_name_info WHERE station_name = \"%s\" AND city_id = 1000", stationName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                dstLineNum.add(resultSet.getInt("line_id"));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return dstLineNum;
    }

    public static ArrayList<SubwayData> getSubLineNameInfoDB(String stationName) {
        ArrayList<SubwayData> stations = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT * FROM Subway.sub_line_name_info WHERE station_name = \"%s\" AND city_id = 1000", stationName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                stations.add(new SubwayData(
                        resultSet.getInt("station_id"),
                        resultSet.getString("station_name"),
                        resultSet.getString("station_code"),
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("line_id"),
                        resultSet.getInt("before_station"),
                        resultSet.getInt("next_station")
                ));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return stations;
    }

    public static ArrayList<TimeTable> getScheduleDB(SubwayData parent, SubwayData child) {
        ArrayList<TimeTable> schedules = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery;
            strQuery = String.format("SELECT station_detail_id, line_direction, subway_type, week_type, schedule_name, hour, minute, line_id " +
                            "FROM Subway.sub_tt_line_%d WHERE station_detail_id = %d AND hour - %d <= 1 AND ((hour * 60 + minute) " +
                            "- (%d * 60 + %d)) >= 0 AND week_type = \'%s\' AND line_direction = %d LIMIT 3 ",
                    child.lineId, child.stationDetailId, parent.schedule.hour, parent.schedule.hour, parent.schedule.minute,
                    parent.schedule.weekType, child.lineDirection, parent.schedule.typeName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery);
            while(resultSet.next()) {
                schedules.add(new TimeTable(
                        resultSet.getInt("hour"),
                        resultSet.getInt("minute"),
                        resultSet.getString("week_type"),
                        resultSet.getString("schedule_name"),
                        resultSet.getString("subway_type")
                ));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return schedules;
    }

    public static SubwayData getStationWithDetailIdDB(int stationDetailId) {
        SubwayData station = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT * FROM Subway.sub_line_name_info WHERE station_detail_id = %d AND city_id = 1000", stationDetailId);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                station = new SubwayData(
                        resultSet.getInt("station_id"),
                        resultSet.getString("station_name"),
                        resultSet.getString("station_code"),
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("line_id"),
                        resultSet.getInt("before_station"),
                        resultSet.getInt("next_station")
                );
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return station;
    }

    public static double getCongestDB(String time, SubwayData station) {
        double result = 0.0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT %s FROM Subway.sub_congest_data WHERE station_name = \"%s\" AND line_id = %d AND line_direction = %d AND week_type = \"%s\"", time, station.stationName, station.lineId, station.lineDirection, ScheduleManager.weekType);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                result = resultSet.getDouble(time);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return result;
    }
}