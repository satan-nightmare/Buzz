package application;

import java.util.Date;

public class Message {
    public String text;
    public People from;
    public People to;
    public Date date;
    public Message(String text,People from,People to,Date date){
        this.text=text;
        this.from=from;
        this.to=to;
        this.date=date;
    }
}
