package genericmenu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import server.services.CurrencyService;
import cliente.TCPClient;
import shd_utils.ParseHelpers;
import shd_utils.Services;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuApp {
    private static final Services[] SERVICES = Services.values();
    private final Scanner sc;

    private final TCPClient client;
    private boolean hasDownloadedCurrencies = false;

    //ONLY FOR TESTING PURPOSES.
    public MenuApp() {
        client = new TCPClient();
        sc = new Scanner(System.in);
    }

    private void displayOptions() {
        System.out.println("/******** MENU OPCIONES ***********/");
        System.out.println("1. Buscar una palabra en el diccionario.");
        System.out.println("2. Agregar una palabra al diccionario.");
        System.out.println("3. Cambiar moneda.");
        System.out.println("4. Enviar PDF.");
        System.out.println("5. Descargar PDF.");
        System.out.println("6. Ver archivos disponibles.");
        System.out.println("7. Cerrar programa.");
    }

    private void sendPDF() {
        JFileChooserPDF chooserPDF = new JFileChooserPDF();
        chooserPDF.showWindow();

        System.out.println("Mostrando pantalla de seleccion de archivos.");

        while(chooserPDF.isVisible()){
            try {
                Thread.sleep(1);
                if(chooserPDF.hasFinishedSelection()) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!chooserPDF.hasValidFile()) {
            System.out.println("El archivo no es válido...");
            return;
        }

        File selected = chooserPDF.getFile();
        System.out.println("Nombre de archivo nuevo: (Dejar en blanco si desea conservar)");
        String word = sc.next().trim();
        String advancedName = selected.getName();

        if(word.length() > 0) {
            String ext = FilenameUtils.getExtension(advancedName);
            advancedName = word + "." + ext;
        }

        System.out.println("El nombre para el archivo será: " + advancedName);
        String content = ParseHelpers.createContents(Services.PDF_UPLOAD_SERVICE, advancedName);

        String response = client.sendInput(content, selected);
        System.out.println("Estado del archivo: " + response);
    }

    private void requestPDFLibrary() {
        System.out.println("Solicitando listado de PDFs.");
        String content = ParseHelpers.createContents(Services.PDF_INFO_SERVICE, "TEMP");
        String response = client.sendMessage(content);
        System.out.println("Datos obtenidos de la biblioteca: " + response);
    }

    private void prepareWordSearch() {
        System.out.println("Introduzca la palabra a buscar:");
        String word = sc.next().trim();

        if(!ParseHelpers.isValidWord(word)) {
            System.out.println("La palabra tiene que ser mayor a 3 caracteres.");
            return;
        }

        String content = ParseHelpers.createContents(Services.SEARCH_WORD, word);
        String resp = client.sendMessage(content);

        if(resp.equals("NO_DEF")) {
            System.out.printf("La palabra %s no posee significados.", word);
            return;
        }

        System.out.printf("Definiciones de %s\n%s", word, resp);
        System.out.println();
        //System.out.printf("Y los sockets? %s\n", resp);
    }

    private void prepareWordAdding() {
        System.out.println("Introduzca la palabra a colocar significado.");
        String word = sc.nextLine().trim();
        System.out.println("Introduzca el signficado correspondiente.");
        String meaning = sc.nextLine().trim();

        if(!ParseHelpers.isValidWord(word) || !ParseHelpers.isValidWord(meaning)) {
            System.out.println("No se permiten definiciones vacias o palabras muy pequeñas (menores a 3 caracteres).");
            return;
        }

        String content = ParseHelpers.createContents(Services.ADD_MEANING, word, meaning);
        String resp = client.sendMessage(content);

        System.out.println(resp);
    }

    private void pdfDownload() {
        System.out.println("Introduzca el documento PDF a descargar (sin extension)");
        String pdfName = sc.next().trim();

        if(pdfName.length() == 0) {
            System.out.println("Ignorando entrada, volviendo al menu...");
            return;
        }

        pdfName = pdfName + ".pdf";

        String resp = ParseHelpers.createContents(Services.PDF_DOWNLOAD_SERVICE, pdfName);
        String content = client.downloadFile(resp, pdfName);

        System.out.println("Datos de la descarga: " + content);
    }

    private void prepareCurrencies() {
        String content;

        //revisar las monedas.
        if (!hasDownloadedCurrencies) {
            content = ParseHelpers.createContents(Services.CHANGE_CURRENCY, CurrencyService.AVAILABLE_COMMAND);
            String currencies = client.sendMessage(content);
            System.out.println(currencies);
            hasDownloadedCurrencies = true;
        }

        System.out.println("Introduzca la moneda base (las mostradas en pantalla):");
        String source = sc.nextLine();
        System.out.println("Introduzca la moneda para la conversión: ");
        String target = sc.nextLine();
        System.out.printf("Introduzca el monto de la moneda base (Moneda escogida: %s)\n", source);

        int amount = sc.nextInt();

        if(amount < 0){
            System.out.println("El monto no puede ser menor a 0.");
            return;
        }

        content = ParseHelpers.createContents(Services.CHANGE_CURRENCY, source, target, Integer.toString(amount));
        String resp = client.sendMessage(content);

        System.out.println(resp);
    }

    //you can't return onto while true without breaking it.
    //returns true to stop.
    private boolean doOptions() {
        int input = sc.nextInt();
        // System.out.printf("The selected input was: %s\n", input);
        if(input <= 0 || input > SERVICES.length) {
            return false;
        }

        //goofy ahh reset nextline.
        sc.nextLine();

        Services service = SERVICES[input - 1];

        System.out.println("Servicio actual: " + service.name());

        switch (service){
            case SEARCH_WORD:
                prepareWordSearch();
                break;
            case ADD_MEANING:
                prepareWordAdding();
                break;
            case CHANGE_CURRENCY:
                prepareCurrencies();
                break;
            case PDF_UPLOAD_SERVICE:
                sendPDF();
                break;
            case PDF_DOWNLOAD_SERVICE:
                //TODO: Implement.
                pdfDownload();
                break;
            case PDF_INFO_SERVICE:
                requestPDFLibrary();
                break;
            //HACK: Add this for avoiding thinking too much.
            case NULL_SERVICE:
                System.out.println("Saliendo del menu...");
                return true;
        }

        return false;
    }

    public void runMenu() {
        //crear el scanner.
        while(true) {
            displayOptions();
            try{
                if (doOptions()){
                    sc.close();
                    client.closeConn();
                    break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Esa entrada no es válida.");
                sc.next();
            }
        }
    }
}
