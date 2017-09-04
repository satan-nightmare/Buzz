package application;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {

    public static void  main(String args[]) throws Exception{

        ServerSocket serverSocket = new ServerSocket(7777);
        Connection con = getDatabaseConnection();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ReceivingThread receivingThread=new ReceivingThread(clientSocket,con);
            SendingThread sendingThread=new SendingThread(clientSocket,con);
            Thread receive = new Thread(receivingThread);
            Thread send = new Thread(sendingThread);
            receive.start();
            send.start();
        }


    }
    private static Connection getDatabaseConnection() throws Exception{
        Class.forName("java.sql.DriverManager");
        Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/BuzzServer","root","");
        return con;
    }
}
