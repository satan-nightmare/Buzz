package application;

import java.util.ArrayList;
import java.util.List;

public class Group extends People{
    String admin;
    public List<People> members;
    public Group(String name, String userName, String email,String admin) {
        super(name, userName, email,true);
        this.admin=admin;
        members=new ArrayList<>();
    }
}
