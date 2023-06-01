package shotmaniacs.group2.di.dto;

public class LoginInfor {
    private String email;
    private String password;
    public LoginInfor(){}
    public LoginInfor(String email, String password){
        setEmail(email);
        setPassword(password);
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "{" + "email='" + email + '\'' + ", password='" + password + '\'' + '}';
    }
}

