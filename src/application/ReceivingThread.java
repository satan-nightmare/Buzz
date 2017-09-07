package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
//this class also conatins server database methods
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

            while (true){//reads any input from the client till apocalypse
                Packet p = (Packet) serverInputStream.readObject();
                if(p.operation=="login"){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Database methods
    public boolean login(Packet p){
        String username = p.string1;
        String password = p.string2;
        return true;
    }
}
