package application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    Class to handle all the local database queries
    Current DB: sqlite
*/

public class LocalDB {

    private Connection conn;
    private MainController controller;

    public LocalDB(MainController controller){
        // Set MainController reference
        this.controller=controller;
        connect();
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

    // Method to fetch all messages from local DB and update the messages Observable list

    public void setAllMessages(People user) throws SQLException {
        Statement stmt=null;
        stmt=conn.createStatement();
        System.out.println(user.userName);
        // select all messages where either sender or receiver is current user
        String query="select * from Messages where sender='"+user.userName+"' or receiver='"+user.userName+"';";
        ResultSet rs = stmt.executeQuery(query);
        controller.messageList.clear();
        int c=0;
        while(rs.next()){
            c++;
            Date date=null;
            // Need to parse sqlite time to Java Date object
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("time"));
            } catch (ParseException e) {
                System.out.println("Error parsing date");

                e.printStackTrace();
            }
            controller.messageList.add(new Message(rs.getString("text"),rs.getString("sender"),rs.getString("receiver"),date));
        }
        System.out.println(""+c);
    }

    // Method to fetch all users from local DB and update the users Observable list

    public void setUsers() throws SQLException {
        Statement stmt=null;
        stmt=conn.createStatement();
        String query="select * from Users";
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            controller.peopleList.add(new People(rs.getString("name"),rs.getString("username"),rs.getString("email")));
        }
    }

}
