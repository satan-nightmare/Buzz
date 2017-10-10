package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainController {

    @FXML
    private ListView<People> peopleListView;
    @FXML
    private ListView<Message> messageListView;
    @FXML
    private ListView<People> searchListView;
    @FXML
    private TextField sendInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField searchField;
    @FXML
    private Button closeButton;
    @FXML
    private SplitPane splitPane;

    public ObservableList<People> peopleList;
    public ObservableList<Message> messageList;
    public ObservableList<People> searchResultList;

    public People currentlyOpenUser;
    public LocalDB db;
    private Main main;

    @FXML
    public void initialize() {
        searchField.setFocusTraversable(false);
        sendButton.setStyle("-fx-padding: 10%");
        System.out.println("Initialize");
        currentlyOpenUser=null;
        nameLabel.setText(Main.user.name);
        db=new LocalDB(this);
        peopleList= FXCollections.observableArrayList();
        try {
            db.setUsers();
        } catch (SQLException e) {
            System.out.println("SQL error in setting users");
            e.printStackTrace();
        }
        peopleListView.setItems(peopleList);
        peopleListView.setCellFactory(peopleListView -> new UserListViewCell());

        peopleListView.setOnMouseClicked(mouseEvent -> {
            System.out.println("Button Clicked");
            try {
                currentlyOpenUser=peopleListView.getSelectionModel().getSelectedItem();
                System.out.println(peopleListView.getSelectionModel().getSelectedItem().userName);
                db.updateAllMessages(peopleListView.getSelectionModel().getSelectedItem(),messageList);
            } catch (SQLException e) {
                System.out.println("Local Database Error");
                e.printStackTrace();
            }
        });

        messageList=FXCollections.observableArrayList();
        messageListView.setItems(messageList);
        messageListView.setCellFactory(messageListView -> new MessageListViewCell());

        searchResultList=FXCollections.observableArrayList();
        searchListView.setItems(searchResultList);
        searchListView.setCellFactory(searchListView -> new SearchListViewCell());
    }

    @FXML
    public void send(ActionEvent event){
        if(sendInput.getText().equals(""))
            return;
        try {
            Message message = new Message(sendInput.getText(),Main.user.userName,currentlyOpenUser.userName,new Date());
            db.sendMessage(message);
            sendInput.setText("");
            messageList.clear();
            db.updateAllMessages(currentlyOpenUser,messageList);
        } catch (SQLException e) {
            System.out.println("ResultSet error in send");
            e.printStackTrace();
        }
    }

    @FXML
    public void pressedEnter(KeyEvent keyEvent){
        if(keyEvent.getCode()== KeyCode.ENTER)
            send(null);
    }

    @FXML
    public void closeSearchList(){
        searchListView.setVisible(false);
        closeButton.setVisible(false);
        splitPane.setDisable(false);
        searchField.setText("");
    }

    @FXML
    public void getSearchResults(){
        if(main.isConnected){
            Packet p = new Packet();
            p.operation="searchQuery";
            p.string1=searchField.getText();
            p.string2=Main.user.userName;
            SendingThread sendingThread = new SendingThread(main.objectOutputStream,p);
            Thread t=new Thread(sendingThread);
            t.start();
        }
    }

    @FXML
    public void openSearchList(){
        splitPane.setDisable(true);
        searchListView.setVisible(true);
        closeButton.setVisible(true);
    }

    public void setSearchResults(Packet p){
        searchResultList.clear();
        System.out.println(p.peopleList.size());
        searchResultList.addAll(p.peopleList);
    }

    public void receiveMessage(Message message){
        db.storeMessage(message);
        if(!message.sender.equals(currentlyOpenUser.userName)){
            for(People p:peopleList){
                if(p.userName.equals(message.sender)){
                    p.counter++;
                    break;
                }
            }
        }
        if(currentlyOpenUser!=null && message.sender.equals(currentlyOpenUser.userName))
            try {
                db.updateAllMessages(currentlyOpenUser,messageList);
            } catch (SQLException e) {
                System.out.println("SQL error while updating message list");
                e.printStackTrace();
            }
    }

    public void updateStatus(List<People> list){
        peopleList.clear();
        peopleList.addAll(list);
    }

    public void setMain(Main main){
        this.main=main;
        db.setMain(main);
        //Has to do it in here because main is null before this
        if(main.isConnected) {
            System.out.println("Check");
            OnlineStatusThread onlineStatusThread = new OnlineStatusThread(this, main);
            Thread t = new Thread(onlineStatusThread);
            t.setDaemon(true);
            t.start();
        }
    }

}
