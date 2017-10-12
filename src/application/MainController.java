package application;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.nio.charset.Charset;
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
    @FXML
    private HBox topHbox;
    @FXML
    private AnchorPane createGroupPane;
    @FXML
    private Label groupNotificationLabel;
    @FXML
    private TextField groupNameField;
    @FXML
    private Button notificationCounter;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileUsernameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label profileStatusLabel;
    @FXML
    private VBox profilePane;
    @FXML
    private ImageView profileImageView;

    public ObservableList<People> peopleList;
    public ObservableList<Message> messageList;
    public ObservableList<People> searchResultList;

    public static MainController controller;
    public People currentlyOpenUser;
    public LocalDB db;
    private Main main;
    private boolean isSearchOpen;
    public static boolean isGroupSelected;  // Used in SearchListViewCell class to get
                                            // info if invite button is required.

    @FXML
    public void initialize() {
        controller=this;
        byte[] emojiBytes = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81};
        String emojiAsString = new String(emojiBytes, Charset.forName("UTF-8"));
        sendInput.setText(emojiAsString);
        searchField.setFocusTraversable(false);
        sendButton.setStyle("-fx-padding: 10%");
        System.out.println("Initialize");
        isSearchOpen=false;
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
                currentlyOpenUser.counter=0;
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
        isGroupSelected=false;  //Default value is false i.e search is used without invite
        isSearchOpen=false;
        searchListView.setVisible(false);
        closeButton.setVisible(false);
        splitPane.setDisable(false);
        splitPane.setOpacity(1.0);
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
        isSearchOpen=true;
        splitPane.setDisable(true);
        FadeTransition fadeIn1 = new FadeTransition(Duration.millis(800));
        fadeIn1.setNode(splitPane);
        fadeIn1.setFromValue(1.0);
        fadeIn1.setToValue(0.4);
        fadeIn1.setCycleCount(1);
        fadeIn1.setAutoReverse(false);
        fadeIn1.playFromStart();

        searchListView.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800));
        fadeIn.setNode(searchListView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        fadeIn.playFromStart();

        ScaleTransition st = new ScaleTransition(Duration.millis(800),searchListView);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.play();

        closeButton.setVisible(true);
    }

    public void setSearchResults(Packet p){
        searchResultList.clear();
        System.out.println(p.peopleList.size());
        searchResultList.addAll(p.peopleList);
    }

    private void blurMainPane(){
        topHbox.setDisable(true);
        splitPane.setDisable(true);
        topHbox.setEffect(new GaussianBlur());
        splitPane.setEffect(new GaussianBlur());
    }

    private void unblurMainPane(){
        topHbox.setEffect(null);
        splitPane.setEffect(null);
        topHbox.setDisable(false);
        splitPane.setDisable(false);
    }

    @FXML
    private void openCreateGroupPane(){
        blurMainPane();
        if(isSearchOpen)
            blurSearch();
        createGroupPane.setVisible(true);
    }

    @FXML
    private void closeCreateGroupPane(){
        groupNotificationLabel.setVisible(false);
        createGroupPane.setVisible(false);
        unblurMainPane();
        if(isSearchOpen)
            unblurSearch();
    }

    @FXML
    private void createGroup() {
        if(groupNameField.getText().equals("")){
            groupNotificationLabel.setText("That's not how it works  : /");
            groupNotificationLabel.setVisible(true);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(5000));
            fadeOut.setNode(groupNotificationLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setCycleCount(1);
            fadeOut.setAutoReverse(false);
            fadeOut.playFromStart();
        }else {
            if (main.isConnected) {
                Packet p = new Packet();
                p.operation = "createGroupRequest";
                p.string1 = groupNameField.getText();
                p.string2 = Main.user.userName;
                SendingThread sendingThread = new SendingThread(main.objectOutputStream, p);
                Thread t = new Thread(sendingThread);
                t.start();
            } else {
                groupNotificationLabel.setText("You are currently offline");
                groupNotificationLabel.setVisible(true);
                groupNotificationLabel.setOpacity(1);
            }
        }
    }

    private void blurSearch(){
        searchListView.setDisable(true);
        closeButton.setDisable(true);
        searchListView.setEffect(new GaussianBlur());
        closeButton.setEffect(new GaussianBlur());
    }

    private void unblurSearch(){
        searchListView.setDisable(false);
        closeButton.setDisable(false);
        searchListView.setEffect(null);
        closeButton.setEffect(null);
    }

    @FXML
    public void openProfilePage(People user){
        blurMainPane();
        if(isSearchOpen)
            blurSearch();
        String path;
        if(user.isSetProfile)
            path="file:src/resources/images/profilepics/"+user.userName+".jpg";
        else
            path="file:src/resources/images/profilepics/default.jpg";
        profileImageView.setImage(new Image(path));
        profileNameLabel.setText(user.name);
        profileUsernameLabel.setText(user.userName);
        profileEmailLabel.setText(user.email);
        profileStatusLabel.setText(user.status);
        profilePane.setVisible(true);
    }

    @FXML
    private void closeProfilePage(){
        profilePane.setVisible(false);
        unblurMainPane();
        if(isSearchOpen)
            unblurSearch();
    }

    @FXML
    public void inviteSearch(){
        if(peopleListView.getSelectionModel().getSelectedItem() instanceof Group){
            isGroupSelected=true;
            openSearchList();
        }
    }

    public void createGroupResponse(Packet p){
        if(p.flag){
            groupNotificationLabel.setText("Group created successfully");

        }else
            groupNotificationLabel.setText("Group name already exist");
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
        //peopleList.clear();
        //peopleList.addAll(list);
        // Update people elements
        for(int i=0;i<list.size();i++){
            peopleList.set(i,list.get(i));
        }
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
