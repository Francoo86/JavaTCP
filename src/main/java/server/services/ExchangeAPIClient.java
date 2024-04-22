package server.services;

import server.serializables.CurrencyResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//Limit this guy to ExchangeRATE
public class ExchangeAPIClient {
    public static String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private static int SUCCESS_CODE = 200;
    private Map<String, CurrencyResponse> cachedResponses = new HashMap<>();

    private CurrencyResponse connectData(String type){
        type = type.toLowerCase();
        try {
            URL url = new URL(API_URL + type);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != SUCCESS_CODE) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            };

            //try to read this json ahh thing.
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                String output = sb.toString();
                CurrencyResponse data = new Gson().fromJson(output, CurrencyResponse.class);
                cachedResponses.put(type.toUpperCase(), data);

                return data;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CurrencyResponse getCurrencyData(String type){
        CurrencyResponse cached = cachedResponses.get(type);

        if(cached == null) {
            System.out.println("[ExchangeClient]: Obteniendo los datos para la moneda " + type);
            return connectData(type);
        }

        return cached;
        //return connectData(type);
    };
}
