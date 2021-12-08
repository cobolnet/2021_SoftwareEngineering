package com.example.tagtheplace;


import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class PlaceRepository {

    private static final String conStr =
            "jdbc:jtds:sqlserver://서버주소:1433;" +
            "databaseName=데이터베이스이름;" +
            "user=azureuser;" +
            "password=비밀번호;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "hostNameInCertificate=*.database.windows.net;" +
            "loginTimeout=30;" +
            "ssl=require;";

    public static List<PlaceData> getPlaceByTag(String string){
        List<PlaceData> list = new ArrayList<>();


        try (Connection con = DriverManager.getConnection(conStr);) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String query = "SELECT * from Place Where Tag = '" + string + "'";

            PlaceData placeData = new PlaceData();

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                placeData.id = rs.getInt("Id");
                placeData.name = rs.getString("Name");
                placeData.tag = rs.getString("Tag");
                placeData.like = rs.getInt("Like");
                placeData.dislike = rs.getInt("Dislike");
                placeData.lat = rs.getFloat("Lat");
                placeData.lng = rs.getFloat("Lng");
                list.add(placeData);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static PlaceData getPlaceById(int id) {
        PlaceData placeData = new PlaceData();
        try (Connection con = DriverManager.getConnection(conStr);) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String query = "SELECT * from Place Where Id = " + id + ";";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                placeData.id = rs.getInt("Id");
                placeData.name = rs.getString("Name");
                placeData.tag = rs.getString("Tag");
                placeData.like = rs.getInt("Like");
                placeData.dislike = rs.getInt("Dislike");
                placeData.lat = rs.getFloat("Lat");
                placeData.lng = rs.getFloat("Lng");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return placeData;
    }

    public static void insertDataToDB(PlaceData placeData) {
        try (Connection con = DriverManager.getConnection(conStr);) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String query = "INSERT INTO Place values ('" + placeData.name + "', '" +
                    placeData.tag + "', " +
                    placeData.like + ", " +
                    placeData.dislike + ", " +
                    placeData.lng + ", " +
                    placeData.lat + ");";

            PreparedStatement pst = con.prepareStatement(query);
            pst.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void increaseLikeById(int id) {
        try (Connection con = DriverManager.getConnection(conStr);) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String query = "UPDATE Place SET [Like] = [Like] + 1 WHERE ID = " + id + ";";

            PreparedStatement pst = con.prepareStatement(query);
            pst.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void increaseDislikeById(int id) {
        try (Connection con = DriverManager.getConnection(conStr);) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String query = "UPDATE Place SET DisLike = DisLike + 1 WHERE ID = " + id + ";";

            PreparedStatement pst = con.prepareStatement(query);
            pst.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
