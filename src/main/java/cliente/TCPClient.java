package cliente;
import java.net.*;
import java.io.*;

public class TCPClient {
    //TODO: Make the host and port usable by env.
    private static final int BASE_PORT = 7896;
    private static final String HOSTNAME = "localhost";
    private static final int BUFFER_SIZE = 4096;

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

    //i hate this
    public String sendInput(String message, File file) {
        try {
            //clear the message.
            message = message.trim();

            System.out.println("SENT DATA: " + message);

            //Sends the request.
            output.writeUTF(message);

            sendFile(file);

            //if any response
            return input.readUTF();
        }
        catch(IOException e) {
            System.out.printf("IOError: %s", e);
        }

        return "";
    }

    public void sendFile(File file) {
        try{
            int bytes;
            FileInputStream fileInputStream = new FileInputStream(file);

            output.writeLong(file.length());
            // break file into chunks
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((bytes=fileInputStream.read(buffer))!=-1){
                output.write(buffer,0,bytes);
                output.flush();
            }

            fileInputStream.close();
        }
        catch (IOException e) {
            System.out.println("TCPClient: Error with " + file.getName() + ". Reason => " + e.getMessage());
        }
    }

    public void sendFile(String fileName) {
        File file = new File(fileName);
        sendFile(file);
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
