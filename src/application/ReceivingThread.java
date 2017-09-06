package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;

public class ReceivingThread implements Runnable {
    private Socket clientSocket;
    private Connection conn;
    private ObjectInputStream serverInputStream;
    public ReceivingThread(Socket clientSocket, Connection conn){
        this.clientSocket=clientSocket;
        this.conn=conn;
    }
    @Override
    public void run() {
        try {
            serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            while (true){   //reads any input from the client till apocalypse

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        (Packet)serverInputStream.readObject();
    }
}
