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
    private LocalDB db;
    public ReceivingThread(Socket clientSocket, Connection conn,LocalDB db){
        this.clientSocket=clientSocket;
        this.conn=conn;
        this.db=db;
    }
    @Override
    public void run() {
        try {
            serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            while (true){   //reads any input from the client till apocalypse
                Packet p = (Packet)serverInputStream.readObject();
                if(p.operation=="login"){
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    Server.socketMap.put(p.string1,objectOutputStream);
                }
                if(p.operation=="send"){
                    p.list.get(0);
                    p.operation="receive";
                    if(Server.socketMap.get(p.string2)!=null) {
                        SendingThread sendingThread = new SendingThread(Server.socketMap.get(p.string2), p);
                    }else{
                        Statement stmt= null;
                        try {
                            stmt = conn.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String query="insert into Messages values('"+p.list.get(0).sender+"','"+p.list.get(0).receiver+"','"+p.list.get(0).text+"','"+df.format(p.list.get(0).date)+")";
                        try {
                            stmt.executeUpdate(query);
                        } catch (SQLException e) {
                            System.out.println("Cannot store message to database");
                            e.printStackTrace();
                        }
                    }
                }
                if(p.operation=="receive"){
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
