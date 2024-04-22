package server.serializables;

import java.util.HashMap;

//vietnam flashbacks
public class CurrencyResponse {
    private String provider;
    private String WARNING_UPGRADE_TO_V6;
    private String terms;
    //This is only the one relevant for the project.
    private String base;
    private String date;
    private int time_last_updated;
    //Also this one.
    private HashMap<String, Double> rates;

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public String getCurrencyBase() {
        return base;
    }
}
