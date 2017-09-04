package application;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserThread implements Runnable {
    private Socket userSocket;
    private ObjectOutputStream serverOutputStream;
    public UserThread(Socket userSocket){
        this.userSocket=userSocket;
    }
    @Override
    public void run() {
        try {
            serverOutputStream=new ObjectOutputStream(userSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
