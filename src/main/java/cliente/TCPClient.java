package cliente;
import java.net.*;
import java.io.*;

public class TCPClient {
    //TODO: Make the host and port usable by env.
    private static final int BASE_PORT = 7000;
    private static final String HOSTNAME = "localhost";

    //We need this to send messages into dictionary.
    private static final int MAX_BUFFER = 1000;

    //Client data.
    private DatagramSocket socket;

    public TCPClient() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
        }
        catch (SocketException e) {
            System.out.println("Can't initialize socket!!! Aborting...");
        }
    }

    public String sendMessage(String message){
        try {
            //clear the message.
            message = message.trim();
            InetAddress address = InetAddress.getByName(HOSTNAME);
            DatagramPacket req = new DatagramPacket(message.getBytes(), message.length(), address, BASE_PORT);

            //Sends the request.
            socket.send(req);

            byte[] buff = new byte[MAX_BUFFER];
            DatagramPacket resp = new DatagramPacket(buff, MAX_BUFFER);

            //if any response.
            socket.receive(resp);

            return new String(resp.getData());
        }
        catch(IOException e) {
            System.out.printf("IOError: %s", e);
        }

        return "";
    }

    public void closeConn() {
        socket.close();
    }
}
