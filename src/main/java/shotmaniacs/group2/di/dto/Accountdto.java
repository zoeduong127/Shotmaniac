package shotmaniacs.group2.di.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import shotmaniacs.group2.di.model.AccountType;

public class Accountdto {

    private String email;
    private String password;
    private String tel;


    public Accountdto(){}

    public Accountdto(String email, String password, String tel) {
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
