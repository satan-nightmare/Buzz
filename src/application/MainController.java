package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.sql.SQLException;

public class MainController {

    @FXML
    private ListView<People> peopleListView;
    public ObservableList<People> peopleList;
    public ObservableList<Message> messageList;
    @FXML
    private ListView<Message> messageListView;
    private String currentUser;
    private LocalDB db;

    @FXML
    public void initialize() {
        System.out.println("Initialize");
        db=new LocalDB(this);
        peopleList= FXCollections.observableArrayList();
        try {
            db.setUsers();
        } catch (SQLException e) {
            System.out.println("SQL error in setting users");
            e.printStackTrace();
        }
        peopleListView.setOnMouseClicked(mouseEvent -> {
            System.out.println("Button Clicked");
            try {
                db.setAllMessages(peopleListView.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {
                System.out.println("Local Database Error");
                e.printStackTrace();
            }
            //temp.setText(list.getSelectionModel().getSelectedItem().name);
        });
        peopleListView.setItems(peopleList);
        messageList=FXCollections.observableArrayList();
        messageListView.setItems(messageList);
        messageListView.setCellFactory(messageListView -> new MessageListViewCell());
    }


}
