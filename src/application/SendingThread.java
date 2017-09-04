package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

public class SendingThread implements Runnable {
    private Socket clientSocket;
    private Connection conn;
    private ObjectOutputStream serverOutputStream;
    public SendingThread(Socket clientSocket, Connection conn){
        this.clientSocket=clientSocket;
        this.conn=conn;
    }
    @Override
    public void run() {
        try {
            serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
