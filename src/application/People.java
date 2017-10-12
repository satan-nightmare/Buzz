package application;

import java.io.Serializable;

public class People implements Serializable{
    public String name;
    public String userName;
    public String email;
    public boolean isActive;
    public boolean isSetProfile;
    public int counter;
    public String status;

    public People(String name,String userName,String email,boolean isSetProfile){
        this.name=name;
        this.userName=userName;
        this.email=email;
        isActive=false;
        this.isSetProfile=isSetProfile;
        counter=0;
    }

    public String toString(){
        return name;
    }
}
