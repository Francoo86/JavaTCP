package server;

import shd_utils.Services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

//listener for client?
public class TCPConnection extends Thread {
    DataInputStream input;
    DataOutputStream output;
    Socket socket;
    //Coupled ahh thing, thanks deadline!!!!
    private TCPServer server;
    public static final String BAD_DATA = "INVALID_DATA";

    public TCPConnection(Socket clientSocket, TCPServer server) {
        try {
            socket = clientSocket;
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            this.server = server;
            this.start();
        } catch (IOException e) {
            System.out.println("Listening: " + e.getMessage());
        }
    }

    public void useServices() throws EOFException, IOException {
        String resp;
        String data = input.readUTF();
        System.out.println("Is there more available? " + input.available());

        List<String> info = server.getInfo(data);
        Services serv = server.getService(info.get(0));

        if(serv == Services.PDF_DOWNLOAD_SERVICE) {
            resp = server.getParsedResponse(info.get(1), output);
            output.writeUTF(resp);
            System.out.println("Response get: " + resp);
            return;
        }

        if(input.available() > 0) {
            resp = server.getParsedResponse(data, input);
        }
        else {
            resp = server.getParsedResponse(data);
        }

        output.writeUTF(resp);
    }

    public void run() {
        try {
            while(true) {
                useServices();
                //System.out.println("Received data:" + data);
            }
        }
        //TODO: Change these generic messages.
        catch (EOFException e){
            System.out.println("EOF: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        finally {
            try{
                System.out.println("Closing client socket on server!!!");
                socket.close();
            }
            catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }
        }
    }
}
