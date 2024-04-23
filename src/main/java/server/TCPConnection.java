package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

//listener for client?
public class TCPConnection extends Thread {
    DataInputStream input;
    DataOutputStream output;
    Socket socket;
    private String response;
    public static final String BAD_DATA = "INVALID_DATA";

    public TCPConnection(Socket socket) {
        try {
            this.socket = socket;
            input = new DataInputStream(this.socket.getInputStream());
            output = new DataOutputStream(this.socket.getOutputStream());
            //this.start();
        } catch (IOException e) {
            System.out.println("Listening: " + e.getMessage());
        }
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getData() {
        try {
            return input.readUTF();
        }
        catch (IOException e) {
            System.out.println("TCPConnection: Can't get the client data. Reason: " +  e.getMessage());
        }

        return BAD_DATA;
    }

    @Override
    public void run() {
        try {
            //String data = input.readUTF();
            output.writeUTF(response);
            socket.close();
        }
        //TODO: Change these generic messages.
        catch (EOFException e){
            System.out.println("EOF: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
