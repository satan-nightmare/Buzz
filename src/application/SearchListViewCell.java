package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class SearchListViewCell extends ListCell<People>{

    @FXML
    private FXMLLoader loader;
    @FXML
    private Label nameLabel;
    @FXML
    private Button inviteButton;
    @FXML
    private HBox body;

    @Override
    protected void updateItem(People user, boolean empty){
        super.updateItem(user, empty);

        if(empty || user == null) {  //If empty, skip it
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {   //set message body FXML
                loader = new FXMLLoader(getClass().getResource("../resources/fxml/searchListCell.fxml"));
                loader.setController(this); //and set this class as it's controller
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("FUCK");
            nameLabel.setText(user.name+" - "+user.userName);
            if(!(user instanceof Group))
                inviteButton.setVisible(false);
            setText(null);
            setGraphic(body);
        }
    }
}
