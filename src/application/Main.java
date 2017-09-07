package application;

import com.sun.org.apache.regexp.internal.RE;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    public static People user;  //Current User
    private Connection conn;
    public Socket socket;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    public boolean isConnected;
    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        isConnected=false;

        if(connection("localhost")) {
            isConnected=true;
            System.out.println("Connection Established");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/fxml/main.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        mainController=fxmlLoader.<MainController>getController();
        mainController.setMain(this);
        //Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/main.fxml"));
        primaryStage.setTitle("Buzz");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
//        Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/login.fxml"));
//        primaryStage.setTitle("Buzz");
//        primaryStage.setResizable(false);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();

    }

    public static void main(String[] args) {
        user=new People("Sumit","sumit","");
        launch(args);
    }

    public boolean connection(String ip){
        try {
            socket=new Socket(ip,12345);
        } catch (Exception e){
            System.out.println("fuck1");
            return false;
        }

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }catch (Exception e){
            System.out.println("fuck2");
            return false;
        }
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            ReceivingThread receivingThread = new ReceivingThread(socket,null,mainController.db);
            Thread thread = new Thread(receivingThread);
            thread.start();
        }catch (Exception e){
            System.out.println("fuck3");
            return false;
        }
        return true;
    }

}