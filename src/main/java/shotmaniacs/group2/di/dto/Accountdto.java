package shotmaniacs.group2.di.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import shotmaniacs.group2.di.model.AccountType;

public class Accountdto {

    private String email;
    private String password;
    private String tel;
    private String username;


    public Accountdto(){}

    public Accountdto(String email, String username, String password, String tel) {
        setUsername(username);
        settel(tel);
        setEmail(email);
        setPassword(password);
    }
    public void settel(String phone){
        this.tel = phone;
    };
    public String gettel(){
        return this.tel;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
