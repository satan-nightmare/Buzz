package application;

import com.sun.org.apache.regexp.internal.RE;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    public static People user;  //Current User
    public Socket socket;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    public boolean isConnected;             //Is the user connected to server
    private MainController mainController;  //Holds reference to mainController instance

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
        isConnected=false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/fxml/main.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load fxml file");
            e.printStackTrace();
        }
        mainController=fxmlLoader.<MainController>getController();
        mainController.setMain(this);   //Set Main class reference in mainController class
        primaryStage.setTitle("Buzz");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //if(connection("localhost")) {
        try{
            connection("localhost");
            isConnected=true;

            //added by me
            getFiles();

            ReceivingThread receivingThread = new ReceivingThread(socket,null,mainController);
            Thread t = new Thread(receivingThread);
            t.start();
            System.out.println("Connection to sever established");
        }catch (IOException e) {
            System.out.println("Connection error");
            //e.printStackTrace();
        }
//        Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/login.fxml"));
//        primaryStage.setTitle("Buzz");
//        primaryStage.setResizable(false);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();

    }

    @Override
    public void stop(){
        System.out.println("Stop invoked");
        Packet packet = new Packet();
        packet.operation="logout";
        packet.string1=Main.user.userName;
        SendingThread sendingThread = new SendingThread(objectOutputStream,packet);
        //Thread t=new Thread(sendingThread);
        //t.start();
        sendingThread.run();
        System.out.println("Sent");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //user=new People("Sumit","sumit","");
        //user=new People("Garvit","garvit","");
        user=new People("Anubhav","anubhav","",false);
        launch(args);
    }

    //Method to receive file from client to server.........
    private void getFiles() throws IOException, ClassNotFoundException {

        String current1 = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir:"+current1);
        FileOutputStream fileoutputstream = new FileOutputStream("src/resources/clientImage/Computer_Organization_and_Design_4th_Ed.pdf");
        BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(fileoutputstream);
        System.out.println("File received");
        int bytesRead;
        bytesRead = (int)objectInputStream.readObject();
        byte [] bytearray = (byte[])objectInputStream.readObject();
        System.out.println("first"+bytesRead);

        bufferedoutputstream.write(bytearray,0,bytesRead);
        bufferedoutputstream.flush();
        fileoutputstream.close();
        bufferedoutputstream.close();
    }

    public boolean connection(String ip) throws IOException {
        socket=new Socket(ip,7777);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        Packet packet = new Packet();
        packet.operation="login";
        packet.string1=user.userName;
        SendingThread sendingThread = new SendingThread(objectOutputStream,packet);
        Thread t = new Thread(sendingThread);
        t.start();
        return true;
    }

}