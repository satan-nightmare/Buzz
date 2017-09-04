package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.Date;

public class MainController {

    @FXML
    private ListView<People> list;
    private ObservableList<People> olist;
    private ObservableList<Message> mlist;
    @FXML
    private ListView<Message> messagelist;

    @FXML
    public void initialize() {
        System.out.println("Initialize");
        olist= FXCollections.observableArrayList();
        People sumit = new People("Sumit","satan_nigthmare","");
        People anubhav = new People("Anubhav","satan_nigthmare","");
        People shakti = new People("Shakti","satan_nigthmare","");
        olist.addAll(sumit,anubhav,shakti);
        list.setOnMouseClicked(mouseEvent -> {
            System.out.println("Button Clicked");
            //temp.setText(list.getSelectionModel().getSelectedItem().name);
        });
        list.setItems(olist);
        mlist=FXCollections.observableArrayList();
        String s="Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.Hey there.";
        mlist.addAll(
                new Message("Hey there.",anubhav,sumit, new Date()),
                new Message("How are you",anubhav,sumit, new Date()),
                new Message("I don't know",sumit,anubhav, new Date()),
                new Message("What's the problem",anubhav,sumit, new Date()),
                new Message("You know what, fuck you",sumit,anubhav, new Date()),
                new Message(s,anubhav,sumit,new Date())
                );
        messagelist.setItems(mlist);
        messagelist.setCellFactory(messageListView -> new MessageListViewCell());
    }

}
