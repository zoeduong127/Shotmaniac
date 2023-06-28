package shotmaniacs.group2.di.model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Account {
    @XmlAnyElement
    private int id;
    @XmlAnyElement
    private String username;
    @XmlAnyElement
    private String email;
    @XmlAnyElement
    private String passwordHash;
    @XmlAnyElement
    private AccountType accountType;
    @XmlAnyElement
    private String salt;
    @XmlAnyElement
    private String tel;

    public Account(){}

    public Account(int id, String username, String email, String passwordHash, AccountType accountType,String tel) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
        setAccountType(accountType);
        settel(tel);
    }
    public String gettel(){
        return this.tel;
    }
    public void settel(String tel){
        this.tel= tel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}