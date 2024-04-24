package server.services;

import java.io.*;

public class FileReceiverService {
    private static final int BUFFER_SIZE = 8196;

    public boolean receiveFile(DataInputStream input, String fileName) {
        try {
            int bytes;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            long size = input.readLong();
            byte[] buffer = new byte[BUFFER_SIZE];

            while (size > 0 && (bytes = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer,0, bytes);
                size -= bytes;
            }

            return true;
        } catch (IOException e) {
            System.out.println("FRS: Can't receive the file properly because => " + e.getMessage());
        }

        return false;
    }

    public String fileResponse(DataInputStream input, String fileName) {
        return String.format("El archivo %s ", fileName) + ((receiveFile(input, fileName))
                ? "recibido correctamente." : " no se ha procesado correctamente.");
    }
}
