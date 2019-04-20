package assign3;

public class SingleEntry {
    private String metropolis;
    private String continent;
    private long population;

    public SingleEntry(String metropolis, String continent, long population){
        this.metropolis = metropolis;
        this.continent = continent;
        this.population = population;
    }

    public long getPopulation() {
        return population;
    }

    public String getContinent() {
        return continent;
    }

    public String getMetropolis() {
        return metropolis;
    }
}
