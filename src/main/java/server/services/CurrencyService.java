package server.services;

import server.serializables.CurrencyResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

//TODO: Implement.
public class CurrencyService {
    public static String AVAILABLE_COMMAND = "SHOW_AVAILABLE";
    private static String BASE_CURRENCY = "USD";

    private ExchangeAPIClient apiClient;

    private HashMap<String, Double> currencies;
    private Set<String> availableCurrencies;
    private void initializeService() {
        apiClient = new ExchangeAPIClient();

        CurrencyResponse data = apiClient.getCurrencyData(BASE_CURRENCY);

        if(data == null){
            System.out.println("[EXCHANGE CLIENT] Service not available, fallback to the CLP currency file.");
            readCurrencyFile();
            return;
        }

        System.out.println("Currency service was setup using the ExchangeRate API!");

        availableCurrencies = data.getRates().keySet();
        //Set<String> currencies = data.getRates().keySet();

    }
    private void readCurrencyFile() {
        currencies = new HashMap<>();

        try{
            URL url = getClass().getResource("CLPCurrencies");
            File fileObj = new File(url.getPath());

            Scanner reader = new Scanner(fileObj);

            while(reader.hasNextLine()){
                String data = reader.nextLine();
                String[] split = data.split(":");
                if(split.length < 2) continue;

                String type = split[0].trim();
                Double currencyMul = Double.parseDouble(split[1].trim());

                currencies.put(type, currencyMul);
            }

            availableCurrencies = currencies.keySet();
            System.out.println("Currency service was setup by using the file!");
        }
        catch (FileNotFoundException e) {
            System.out.println("Currencies file wasn't found.");
        }
        catch (NullPointerException e) {
            System.out.println("NullPointerException found: " + e.getMessage());
        }
    }

    public CurrencyService(){
        //readCurrencyFile();
        initializeService();
    }

    public double convertExchange(String source, String target, double sourceMul){
        //avoid dumb calculations.
        if(source == target){
            return 1d;
        }

        CurrencyResponse data = apiClient.getCurrencyData(source);

        if(data == null) {
            return -1d;
        }

        Map<String, Double> rates = data.getRates();
        return sourceMul * rates.get(target);
    }

    public Set<String> getAvailableCurrencies(){
        return availableCurrencies;
    }

    public boolean isValidCurrency(String type) {
        return availableCurrencies.contains(type);
    }
}
