package assign3;

import java.sql.*;
public class ExampleAccess {
    static String account = "root"; // replace with your account
    static String password = "zgarbi007"; // replace with your password
    static String server = "127.0.0.1";
    static String database = "lkiti17_schema"; // replace with your db


    public static void main(String[] args) {
        /*try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection
                    ( "jdbc:mysql://" + server, account ,password);
            Statement stmt = con.createStatement();
            stmt.executeQuery("USE " + database);
            ResultSet rs = stmt.executeQuery("SELECT * FROM metropolises");
            while(rs.next()) {
                String name = rs.getString("metropolis");
                long pop = rs.getLong("population");
                System.out.println(name + "\t" + pop);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
}