package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MessageListViewCell extends ListCell<Message>{

    @FXML
    private Label text;
    @FXML
    private Label date;
    @FXML
    private VBox body;
    @FXML
    private FXMLLoader loader;
    @FXML
    private Label name;

    @Override
    protected void updateItem(Message message,boolean empty){
        super.updateItem(message, empty);

        if(empty || message == null) {  //If empty, skip it

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {   //set message body FXML
                loader = new FXMLLoader(getClass().getResource("../resources/fxml/messagebody.fxml"));
                loader.setController(this); //and set this class as it's controller
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //text.setStyle("-fx-border-radius: 5px");
            //date.setStyle("-fx-border-radius: 5px");
            name.setText(message.receiver);
            text.setText(message.text);
            date.setText(message.date.toString());
            if(message.sender.equals(Main.user.userName)) {  //If you sent the message
                name.setStyle("-fx-border-color:lightblue; -fx-background-color: lightblue;");
                text.setStyle("-fx-border-color:lightblue; -fx-background-color: lightblue;");
                date.setStyle("-fx-border-color:lightblue; -fx-background-color: lightblue;");
                //body.setAlignment(Pos.CENTER_RIGHT);
                this.setAlignment(Pos.CENTER_RIGHT);
            }else{  //If you received the message
                name.setStyle("-fx-border-color:lightgreen; -fx-background-color: lightgreen;");
                text.setStyle("-fx-border-color:lightgreen; -fx-background-color: lightgreen;");
                date.setStyle("-fx-border-color:lightgreen; -fx-background-color: lightgreen;");
                //body.setAlignment(Pos.CENTER_RIGHT);
                this.setAlignment(Pos.CENTER_LEFT);
            }
            setText(null);
            setGraphic(body);
        }
    }

}
