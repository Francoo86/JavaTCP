package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class TCPConnection extends Thread {
    DataInputStream input;
    DataOutputStream output;
    Socket socket;

    public TCPConnection(Socket socket) {
        try {
            this.socket = socket;
            input = new DataInputStream(this.socket.getInputStream());
            output = new DataOutputStream(this.socket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Listening: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String data = input.readUTF();
            output.writeUTF(data);
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
