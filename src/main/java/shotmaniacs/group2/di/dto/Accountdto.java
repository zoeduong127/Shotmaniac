package shotmaniacs.group2.di.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import shotmaniacs.group2.di.model.AccountType;

public class Accountdto {

    private String username;

    private String email;

    private String passwordHash;


    public Accountdto(){}

    public Accountdto(String username, String email, String passwordHash, String phoe) {
        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
