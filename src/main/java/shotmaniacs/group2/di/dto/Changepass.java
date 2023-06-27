package shotmaniacs.group2.di.dto;

public class Changepass {
    String oldpass;
    String newpass;
    public Changepass(){}
    public Changepass(String oldpass, String newpass){

    }
    public void setOldpass(String pass){
        this.oldpass = pass;
    }
    public void setNewpass(String pass){
        this.newpass = pass;
    }
    public String getOldpass(){
        return oldpass;
    }
    public String getNewpass(){
        return newpass;
    }

}
