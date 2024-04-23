package cliente;
import java.net.*;
import java.io.*;

public class TCPClient {
    //TODO: Make the host and port usable by env.
    private static final int BASE_PORT = 7896;
    private static final String HOSTNAME = "localhost";

    //Client data.
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public TCPClient() {
        try {
            socket = new Socket(HOSTNAME, BASE_PORT);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            //socket.setSoTimeout(5000);
        }
        catch (SocketException e) {
            System.out.println("Can't initialize socket!!! Aborting...");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendMessage(String message){
        try {
            //clear the message.
            message = message.trim();

            //Sends the request.
            output.writeUTF(message);

            //if any response
            return input.readUTF();
        }
        catch(IOException e) {
            System.out.printf("IOError: %s", e);
        }

        return "";
    }

    public void closeConn() {
        try{
            socket.close();
        }
        catch (IOException e) {
            System.out.println("Can't close connection, reason: " + e.getMessage());
        }
    }
}
