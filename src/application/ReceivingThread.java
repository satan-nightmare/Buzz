package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//this class also conatins server database methods

public class ReceivingThread implements Runnable {
    private Socket clientSocket;
    private Connection conn;
    private ObjectInputStream serverInputStream;
    private ObjectOutputStream objectOutputStream;
    private LocalDB db;
    public ReceivingThread(Socket clientSocket, Connection conn,LocalDB db){
        this.clientSocket=clientSocket;
        this.conn=conn;
        this.db=db;
    }

    public ReceivingThread(Socket clientSocket, Connection conn,LocalDB db,ObjectOutputStream objectOutputStream){
        this.clientSocket=clientSocket;
        this.conn=conn;
        this.db=db;
        this.objectOutputStream=objectOutputStream;
    }



    @Override
    public void run() {
        try {
            if(clientSocket==null)
                System.out.println("Maa ki");
            System.out.println("Before");
            serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("After");
            while (true){   //reads any input from the client till apocalypse
                Packet p = (Packet)serverInputStream.readObject();
                System.out.println("Packet received");

                if(p.operation.equals("login")){
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    //objectOutputStream=new ObjectOutputStream(clientSocket.getOutputStream());
                    Server.socketMap.put(p.string1,objectOutputStream);
                }else if(p.operation.equals("send")){
                    System.out.println("Send packet received "+p.list.get(0).receiver);
                    p.list.get(0);
                    System.out.println(p.list.get(0).text);
                    p.operation="receive";
                    if(Server.socketMap.get(p.list.get(0).receiver)!=null) {
                        SendingThread sendingThread = new SendingThread(Server.socketMap.get(p.list.get(0).receiver), p);
                        Thread t=new Thread(sendingThread);
                        t.start();
                        System.out.println("Message sent");
                    }else{
                        Statement stmt= null;
                        try {
                            stmt = conn.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String query="insert into Messages (sender,receiver,message,time) values('"+p.list.get(0).sender+"','"+p.list.get(0).receiver+"','"+p.list.get(0).text+"','"+df.format(p.list.get(0).date)+")";
                        try {
                            stmt.executeUpdate(query);
                        } catch (SQLException e) {
                            System.out.println("Cannot store message to database");
                            e.printStackTrace();
                        }
                    }
                }else if(p.operation.equals("receive")){
                    System.out.println("Message received"+p.list.get(0).text);
                    db.receiveMessage(p.list.get(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
