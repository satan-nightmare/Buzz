package application;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void  main(String args[]) throws Exception{

        ServerSocket serverSocket = new ServerSocket(7777);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            UserThread user = new UserThread(clientSocket);
            Thread userThread = new Thread(user);
            //new thread for individual user started
            userThread.start();
        }


    }
}
