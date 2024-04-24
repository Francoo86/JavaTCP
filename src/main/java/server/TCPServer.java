package server;

import server.services.CurrencyService;
import server.services.DictionaryService;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import dao.Word;
import dao.WordDefinition;
import server.services.FileService;
import shd_utils.ParseHelpers;
import shd_utils.Services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class TCPServer {
    private ServerSocket socket;
    //setup services.
    private DictionaryService dictService;
    private CurrencyService currencyService;
    private FileService fileService;

    //inmutable ahh array.
    private static final Services[] services = Services.values();

    public Services getService(String num) {
        int castNum = Integer.parseInt(num);
        return services[castNum];
    }

    public List<String> getInfo(String data) {
        List<String> contents = ParseHelpers.parseContents(data);
        return contents;
    }

    public TCPServer(String url, int port) {
        try {
            socket = new ServerSocket(port);
            ConnectionSource source = new JdbcConnectionSource(url);

            dictService = new DictionaryService(source);
            currencyService = new CurrencyService();
            fileService = new FileService();

            System.out.printf("Setting up server at port %s.\n", port);
        }
        catch (IOException e) {
            System.out.printf("[SERVER] Socket (at port %s): %s\n", port, e.getMessage());
        }
        catch (SQLException e) {
            System.out.println("[SERVER] SQL: " + e.getMessage());
        }
    }

    //TODO: Move this functionality.
    public String formatLookupWordResp(String word){
        Word tempWord = dictService.lookupWord(word.toLowerCase());
        if(tempWord == null || tempWord.getDefinitions().isEmpty()) {
            return "NO_DEF";
        }

        StringBuilder sb = new StringBuilder();
        List<WordDefinition> defs = tempWord.getDefinitions();

        for(int i = 0; i < defs.size(); i++) {
            WordDefinition def = defs.get(i);
            String fullString = String.format("%s. %s\n", i + 1, def.getDef());
            sb.append(fullString);
        }

        return sb.toString();
    }

    public String formatAddDictionary(String word, String meaning) {
        boolean check = dictService.addDefinition(word.toLowerCase(), meaning);
        return String.format("El significado de %s%s fue añadido.", word, check ? "" : " no");
    }

    public String formatCurrencyResponse(List<String> contents){
        //Mostrar monedas disponibles al usuario.
        if (contents.get(0).equals(CurrencyService.AVAILABLE_COMMAND)){
            Set<String> currencies = currencyService.getAvailableCurrencies();
            return ParseHelpers.parseSetAsString("Monedas disponibles", currencies);
        }

        String source = contents.get(0).toUpperCase();
        String target = contents.get(1).toUpperCase();

        if(!currencyService.isValidCurrency(source) || !currencyService.isValidCurrency(target)){
            return String.format("Una o 2 de las monedas solicitadas no es valida.");
        }

        double amount = Double.parseDouble(contents.get(2));
        double converted = currencyService.convertExchange(source, target, amount);

        //this should never happen!!!!!
        if(converted == -1d) {
            return String.format("No se pudo realizar la conversión.");
        }

        return String.format("%s en %s equivale a %s en %s ", amount, source, converted, target);
    }

    private String formatPDFLibrary() {
        Set<String> data = fileService.getUploadedFiles();
        if(data.size() == 0) {
            return "No hay archivos en la biblioteca.";
        }

        return ParseHelpers.parseSetAsString("Archivos obtenidos", data);
    }

    public String handleServices(List<String> contents) {
        Services serv = getService(contents.get(0));

        contents.remove(0);

        return switch (serv) {
            case SEARCH_WORD -> formatLookupWordResp(contents.get(0));
            case ADD_MEANING -> formatAddDictionary(contents.get(0), contents.get(1));
            case CHANGE_CURRENCY -> formatCurrencyResponse(contents);
            case PDF_INFO_SERVICE -> formatPDFLibrary();
            default -> "NOT_IMPLEMENTED";
        };
    }

    //overload because its funny to do coupled stuff.
    public String getParsedResponse(String utfData, DataInputStream input) {
        System.out.println("THE RECEIVED DATA: " + utfData);
        List<String> contents = ParseHelpers.parseContents(utfData);
        Services serv = getService(contents.get(0));
        contents.remove(0);

        return switch (serv) {
            case PDF_UPLOAD_SERVICE -> fileService.fileResponse(input, contents.get(0));
            default -> "NOT_IMPLEMENTED";
        };
    }

    public String getParsedResponse(String utfData){
        List<String> contents = ParseHelpers.parseContents(utfData);
        String serviceResponse = handleServices(contents);
        return serviceResponse;
    }

    public void listenClients() {
        if (socket == null) {
            System.out.println("[SERVER] Socket not initialized properly, ending program...");
            return;
        }

        System.out.println("SERVER: Listening to clients.");

        try {
            while (true) {
                Socket clientSocket = socket.accept();
                TCPConnection conn = new TCPConnection(clientSocket, this);
            }
        }
        catch (IOException e) {
            System.out.println("[SERVER] Socket (IO): " + e.getMessage());
        }
    }

    public String getParsedResponse(String data, DataOutputStream output) {
        boolean wasSentSuccess = fileService.sendFileToClient(output, data);
        if(!wasSentSuccess) {
            return "FS => The file can't be sent.";
        }

        return "The " + data + " has been sent succesfully.";
    }
}
