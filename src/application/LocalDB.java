package application;

import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *  Class to handle all the local database queries
 *  Current DB: sqlite
 */

public class LocalDB {

    private Connection conn;
    private MainController controller;
    private Main main;

    public LocalDB(MainController controller){
        // Set MainController reference
        this.controller=controller;
        this.main=main;
        connect();
    }

    public void setMain(Main main){
        this.main=main;
    }

    public void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:./Databases/BuzzUser.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ResultSet DBquery(String query){
        Statement stmt=null;
        try {
            stmt=conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Local database error:\n"+query);
            return null;
        }
    }

    private void DBupdate(String query){
        Statement stmt=null;
        try {
            stmt=conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Local database error:\n"+query);
        }
    }

    // Method to fetch all messages from local DB and update the messages Observable list

    public void updateAllMessages(People user, ObservableList<Message> messageList) throws SQLException {
        String query="select * from Messages where sender='"+user.userName+"' or receiver='"+user.userName+"';";
        ResultSet rs = DBquery(query);
        messageList.clear();
        while(rs.next()){
            Date date=null;
            // Need to parse sqlite time to Java Date object
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("time"));
            } catch (ParseException e) {
                System.out.println("Error parsing date");

                e.printStackTrace();
            }
            messageList.add(new Message(rs.getString("text"),rs.getString("sender"),rs.getString("receiver"),date));
        }
    }

    // Method to fetch all users from local DB and update the users Observable list

    public void setUsers() throws SQLException {
        /*Statement stmt=null;
        stmt=conn.createStatement();*/
        String query="select * from Users";
        ResultSet rs = DBquery(query);
        while(rs.next()){
            controller.peopleList.add(new People(rs.getString("name"),rs.getString("username"),rs.getString("email"),rs.getBoolean("isSetProfilePic")));
        }
    }

    public void sendMessage(Message message) throws SQLException {
        storeMessage(message);
        if(main.isConnected){
            Packet packet = new Packet();
            packet.operation="send";
            packet.list.add(message);
            SendingThread sendingThread = new SendingThread(main.objectOutputStream,packet);
            Thread t=new Thread(sendingThread);
            t.start();
            System.out.println("Messege sent");
        }
    }

    public void addUser(People user){
        if(user instanceof Group){
            String query = "Insert into User (username,name,isSetProfilePic,isGroup) values ('"+user.userName+"','"+user.name+"',0,1)";
            DBupdate(query);
            query = "Create table '"+user.userName+"' (username,isAdmin)";
            DBupdate(query);
        }else{
            String query = "Insert into User (username,name,email,isSetProfilePic,isGroup) values ('"+user.userName+"','"+user.name+"','"+user.email+"',0,1)";
            DBupdate(query);
        }
    }

    public void storeMessage(Message message){
        String query="insert into Messages values('"+message.sender+"','"+message.receiver+"','"+message.text+"',datetime())";
        DBupdate(query);
    }

}
