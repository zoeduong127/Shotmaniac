//package shotmaniacs.group2.di.dto;
//
//import jakarta.xml.bind.annotation.XmlAnyElement;
//import jakarta.xml.bind.annotation.XmlRootElement;
//import shotmaniacs.group2.di.model.Account;
//import shotmaniacs.group2.di.model.AccountType;
//
//@XmlRootElement
//public class AccountWithRole extends Account {
//    @XmlAnyElement
//    private String role;
//    public AccountWithRole(Integer id, String username, String email, String passwordHash, AccountType accountType, String salt, String telephone, String role) {
//        super(id, username, email, passwordHash, accountType, salt, telephone, role);
//        this.role = role;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//}
