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
    // Method for getting Connection object ...
    private static Connection getDatabaseConnection() throws Exception{
            Connection conn = null;
            try {
                // db parameters
                String url = "jdbc:sqlite:./Databases/BuzzServer.db";
                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            return conn;
    }
}
