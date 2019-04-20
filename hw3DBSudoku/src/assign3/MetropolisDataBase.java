package assign3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static assign3.MyDBInfo.*;

public class MetropolisDataBase {

    // Population search type identifiers
    public static final int POPULATION_MORE_THAN = -10;
    public static final int POPULATION_LESS_OR_EQUAL = -11;

    // Metropolis and continent name match types
    public static final int MATCH_TYPE_EXACT = -20;
    public static final int MATCH_TYPE_PARTIAL = -21;

    private String user = MYSQL_USERNAME;
    private String pass = MYSQL_PASSWORD;
    private String server = MYSQL_DATABASE_SERVER;
    private String dataBase = MYSQL_DATABASE_NAME;

    private Connection con;
    private Statement stmt;

    public MetropolisDataBase(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection
                    ( "jdbc:mysql://" + server, user, pass);
            stmt = con.createStatement();
            stmt.executeQuery("USE " + dataBase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean addData(String metropolis, String continent, long population){
        if(metropolis.isEmpty() || continent.isEmpty() || population < 0){
            return false;
        }
        try {
            stmt.executeUpdate("INSERT INTO metropolises VALUES (\""
                    + metropolis + "\", \"" + continent + "\", " + population + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String generateQuery(String metropolis, String continent, long population, int popSearch, int nameSearch){
        String query = "SELECT * FROM metropolises";
        if(!(metropolis.isEmpty() && continent.isEmpty() && population == -1)){
            query += "\nwhere " ;
        }
        boolean isMultiCondition = false;
        if(!metropolis.isEmpty()){
            if(nameSearch == MATCH_TYPE_EXACT){
                query += "metropolis = \'" + metropolis + "\'";
            } else {
                query += "metropolis like \'%" + metropolis + "%\'";
            }
            query += '\n';
            isMultiCondition = true;
        }
        if(!continent.isEmpty()){
            if(isMultiCondition){
                query += "and ";
            }
            if(nameSearch == MATCH_TYPE_EXACT){
                query += "continent = \'" + continent + "\'";
            } else {
                query += "continent like \'%" + continent + "%\'";
            }
            query += '\n';
            isMultiCondition = true;
        }
        if(population != -1){
            if(isMultiCondition){
                query += "and ";
            }
            if(popSearch == POPULATION_MORE_THAN){
                query += "population > " + population;
            } else {
                query += "population <= " + population;
            }
        }
        return query;
    }

    public List<SingleEntry> searchData(String metropolis, String continent, long population, int popSearch, int nameSearch){
        List<SingleEntry> result = new ArrayList<>();
        try {
            String query = generateQuery(metropolis, continent, population, popSearch, nameSearch);
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                SingleEntry se = new SingleEntry(rs.getString("metropolis"),
                        rs.getString("continent"), rs.getLong("population"));
                result.add(se);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void searchData(String metropolis, String continent, int popSearch, int nameSearch){
        searchData(metropolis, continent, -1, popSearch, nameSearch);
    }

    public static void main(String[] args){
        MetropolisDataBase mdb = new MetropolisDataBase();
        mdb.addData("Tbilisi", "Europe", 4_000_000);
        List<SingleEntry> res = mdb.searchData("Tbilisi", "Europe",
                1_000_000, mdb.POPULATION_MORE_THAN, mdb.MATCH_TYPE_EXACT);
        System.out.println("SIZE: " + res.size() + " res: " + res.get(0).getMetropolis() + " "
                + res.get(0).getContinent() + " " + res.get(0).getPopulation());
    }

}



