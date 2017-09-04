package application;

import java.util.Date;

public class Message {
    public String text;
    public String sender;
    public String receiver;
    public Date date;
    public Message(String text,String sender,String receiver,Date date){
        this.text=text;
        this.sender=sender;
        this.receiver=receiver;
        this.date=date;
    }
}
