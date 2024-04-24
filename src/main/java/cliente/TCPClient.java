package cliente;
import shd_utils.FileHelpers;

import java.net.*;
import java.io.*;

public class TCPClient {
    //TODO: Make the host and port usable by env.
    private static final int BASE_PORT = 7896;
    private static final String HOSTNAME = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static final String DOWNLOADS_PATH = "downloads/";

    //Client data.
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public TCPClient() {
        try {
            socket = new Socket(HOSTNAME, BASE_PORT);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            socket.setSoTimeout(5000);
        }
        catch (SocketException e) {
            System.out.println("Socket Error: " + e.getMessage());
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

    public String downloadFile(String contents, String fileName) {
        try {
            //output.writeInt(fileName.length());
            FileHelpers.createDirIfNotExists(DOWNLOADS_PATH);
            output.writeUTF(contents);

            long fileLength = input.readLong();
            if (fileLength == -1) {
                System.out.println("File not found on server.");
                return "";
            }

            FileOutputStream fileOutputStream = new FileOutputStream(DOWNLOADS_PATH + fileName);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = 0;

            while (totalBytesRead < fileLength && (bytesRead = input.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            fileOutputStream.close();
            System.out.println("File downloaded successfully.");

            if(input.available() > 0){
                return input.readUTF();
            }
            else {
                System.out.println("NO DATA!!!");
            }

            return "";
        } catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }

        return "";
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
