package application;

public class People {
    public String name;
    public String userName;
    public String email;

    public People(String name,String userName,String email){
        this.name=name;
        this.userName=userName;
        this.email=email;
    }

    public String toString(){
        return name;
    }
}
