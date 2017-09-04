package application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalDB {

    private Connection conn;
    private MainController controller;

    public LocalDB(MainController controller){
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
        } /*finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }*/
    }

    public void setAllMessages(People user) throws SQLException {
        Statement stmt=null;
        stmt=conn.createStatement();
        System.out.println(user.userName);
        String query="select * from Messages where sender='"+user.userName+"' or receiver='"+user.userName+"';";
        ResultSet rs = stmt.executeQuery(query);
        controller.messageList.clear();
        int c=0;
        while(rs.next()){
            c++;
            Date date=null;
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
