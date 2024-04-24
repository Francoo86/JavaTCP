package server.services;

import shd_utils.FileHelpers;

import java.io.*;

public class FileReceiverService {
    private static final int BUFFER_SIZE = 8196;
    private static final String UPLOADS = "uploads/";

    public void checkUploads() {
        FileHelpers.createDirIfNotExists(UPLOADS);
    }

    public boolean receiveFile(DataInputStream input, String fileName) {
        try {
            checkUploads();

            int bytes;
            FileOutputStream fileOutputStream = new FileOutputStream(UPLOADS + fileName);

            long size = input.readLong();

            System.out.println("Leyendo archivo: " + fileName);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (size > 0 && (bytes = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer,0, bytes);
                size -= bytes;
            }

            System.out.println("Finalizando lectura de " + fileName);

            fileOutputStream.close();

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
