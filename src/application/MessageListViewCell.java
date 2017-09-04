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

    @Override
    protected void updateItem(Message message,boolean empty){
        super.updateItem(message, empty);

        if(empty || message == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("../resources/fxml/messagebody.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //text.setStyle("-fx-border-radius: 5px");
            //date.setStyle("-fx-border-radius: 5px");
            text.setText(message.text);
            date.setText(message.date.toString());
            if(message.from.name.equals(Main.user.name)) {
                text.setStyle("-fx-border-color:lightblue; -fx-background-color: lightblue;");
                date.setStyle("-fx-border-color:lightblue; -fx-background-color: lightblue;");
                //body.setAlignment(Pos.CENTER_RIGHT);
                this.setAlignment(Pos.CENTER_RIGHT);
            }else{
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
