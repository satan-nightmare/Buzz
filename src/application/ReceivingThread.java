package application;

import javafx.application.Platform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//this class also conatins server database methods

public class ReceivingThread implements Runnable {
    private Socket clientSocket;
    private Connection conn;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private LocalDB db;
    private MainController controller;
    private String user;
    public ReceivingThread(Socket clientSocket, Connection conn,MainController controller){
        this.clientSocket=clientSocket;
        this.conn=conn;
        this.controller=controller;
        try {
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReceivingThread(Socket clientSocket, Connection conn,ObjectOutputStream objectOutputStream){
        this.clientSocket=clientSocket;
        this.conn=conn;
        this.db=null;
        this.objectOutputStream=objectOutputStream;
        try {
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        try {

            while (true){   //reads any input from the client till apocalypse
                Packet p = (Packet)objectInputStream.readObject();
                System.out.println("Packet received");
                if(p.operation.equals("login")){
                    user=p.string1;
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    Server.socketMap.put(p.string1,objectOutputStream);

                    p.operation = "receive";
                    String query = "Select * from Messages where receiver = '"+p.string1+"'";   //First get all the messages stored on database.
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while(rs.next()){
                        Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("time"));  //Parse the sate to string
                        Message message = new Message(rs.getString("message"),rs.getString("sender"),rs.getString("receiver"),date);
                        p.list.add(message);
                    }

                    SendingThread sendingThread = new SendingThread(objectOutputStream,p);
                    Thread send = new Thread(sendingThread);
                    send.start();

                    query = "Delete from Messages where receiver = '"+p.string1+"'";    //Then delete the messages when they are sent to the user.
                    stmt.executeUpdate(query);

                    query = "update User set isActive=1 where username='"+p.string1+"'";
                    stmt.executeUpdate(query);

                }else if(p.operation.equals("send")){

                    p.operation="receive";
                    if(Server.socketMap.get(p.list.get(0).receiver)!=null) {
                        SendingThread sendingThread = new SendingThread(Server.socketMap.get(p.list.get(0).receiver), p);
                        Thread t=new Thread(sendingThread);
                        t.start();
                        System.out.println("User online message sent");
                    }else{
                        Statement stmt = null;
                        try {
                            stmt = conn.createStatement();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String query="insert into Messages (sender,receiver,message,time) values('"+p.list.get(0).sender+"','"+p.list.get(0).receiver+"','"+p.list.get(0).text+"','"+df.format(p.list.get(0).date)+"')";
                        System.out.println(query);
                            stmt.executeUpdate(query);
                        } catch (SQLException e) {
                            System.out.println("Cannot store message to database");
                            e.printStackTrace();
                        }
                    }

                }else if(p.operation.equals("receive")){
                    System.out.println("Messege Received");
                    if(p.peopleList.size()>0)
                    for(int i=0;i<p.list.size();++i) {
                        final int temp=i;
                        Platform.runLater(() -> {
                            controller.receiveMessage(p.list.get(temp));
                        });
                    }
                }else if(p.operation.equals("logout")){
                    Server.socketMap.remove(p.string1);
                    clientSocket.close();
                }else if(p.operation.equals("onlinerequest")){
                    Statement stmt=null;
                    stmt = conn.createStatement();
                    ResultSet rs=null;
                    for(People user: p.peopleList){
                        String query="select * from User where username='"+user.userName+"'";
                        rs=stmt.executeQuery(query);
                        if(rs.next() && rs.getInt("isActive")==1)
                            user.isActive=true;
                    }
                    p.operation="onlineresponse";
                    SendingThread sendingThread = new SendingThread(Server.socketMap.get(p.string1), p);
                    Thread t=new Thread(sendingThread);
                    t.start();
                }else if(p.operation.equals("onlineresponse")){
                    Platform.runLater(()->{ controller.updateStatus(p.peopleList);});
                }else if(p.operation.equals("searchQuery")){
                    Statement stmt = null;
                    try {
                        //System.out.println("Search: "+p.string1);
                        stmt = conn.createStatement();
                        String query="SELECT * FROM User WHERE userName LIKE '"+p.string1+"%' OR name LIKE '"+p.string1+"%'";
                        ResultSet rs = stmt.executeQuery(query);
                        p.operation="searchResults";
                        while(rs.next())
                            p.peopleList.add(new People(rs.getString("name"),rs.getString("userName"),rs.getString("email"),rs.getBoolean("isSetProfilePic")));
                        SendingThread sendingThread = new SendingThread(Server.socketMap.get(p.string2), p);
                        Thread t=new Thread(sendingThread);
                        t.start();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else if(p.operation.equals("searchResults")){
                    Platform.runLater(() -> {
                        controller.setSearchResults(p);
                    });
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Server.socketMap.remove(user);
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            String query = "update User set isActive=0 where username='"+user+"'";
            try {
                stmt.executeUpdate(query);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("fuck");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("fuck1");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("fuck2");
            e.printStackTrace();
        }
    }

}
