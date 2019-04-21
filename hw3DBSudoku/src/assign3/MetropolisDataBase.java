package assign3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static assign3.MyDBInfo.*;

public class MetropolisDataBase {

    // Population search type identifiers
    public static final int POPULATION_MORE_THAN = -10;
    public static final int POPULATION_LESS_OR_EQUAL = -11;


    public static final String POPULATION_MORE_THAN_NAME = "larger than";
    public static final String POPULATION_LESS_OR_EQUAL_NAME = "smaller than or equal to";

    // Metropolis and continent name match types
    public static final int MATCH_TYPE_EXACT = -20;
    public static final int MATCH_TYPE_PARTIAL = -21;

    public static final String MATCH_TYPE_EXACT_NAME = "Exact Match";
    public static final String MATCH_TYPE_PARTIAL_NAME = "Partial Match";


    public static final int DATA_SIZE = 3;

    private String user = MYSQL_USERNAME;
    private String pass = MYSQL_PASSWORD;
    private String server = MYSQL_DATABASE_SERVER;
    private String dataBase = MYSQL_DATABASE_NAME;

    private Connection con;
    private Statement stmt;

    private List<SingleEntry> data;

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
        data = new ArrayList<>();
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
        data.clear();
        data.add(new SingleEntry(metropolis, continent, population));
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

    public int getRows(){
        return data.size();
    }

    public int getCols(){
        return DATA_SIZE;
    }

    private List<SingleEntry> getData(String query){
        List<SingleEntry> result = new ArrayList<>();
        try {
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

    public List<SingleEntry> getCurrentData(){
        return new ArrayList<>(data);
    }

    public void searchData(String metropolis, String continent, long population, int popSearch, int nameSearch){
        List<SingleEntry> result = getData(generateQuery(metropolis, continent, population, popSearch, nameSearch));
        data = new ArrayList<>(result);
    }

    public void searchData(String metropolis, String continent, int popSearch, int nameSearch){
        searchData(metropolis, continent, -1, popSearch, nameSearch);
    }

    public static void main(String[] args){
        MetropolisDataBase mdb = new MetropolisDataBase();
        mdb.searchData("Tbilisi", "Europe",
                1_000_000, mdb.POPULATION_MORE_THAN, mdb.MATCH_TYPE_PARTIAL);
        System.out.println("SIZE: " + mdb.getRows() + " res: " + mdb.getCurrentData().get(0).getMetropolis() + " "
                + mdb.getCurrentData().get(0).getContinent() + " " + mdb.getCurrentData().get(0).getPopulation());
    }

}



