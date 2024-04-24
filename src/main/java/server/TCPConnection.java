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

    public void run() {
        try {
            while(true) {
                String resp;
                String data = input.readUTF();

                if(input.available() > 0) {
                    resp = server.getParsedResponse(data, input);
                }
                else {
                    resp = server.getParsedResponse(data);
                }

                output.writeUTF(resp);
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
