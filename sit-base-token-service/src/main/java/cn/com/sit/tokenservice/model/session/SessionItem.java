package cn.com.sit.tokenservice.model.session;

import lombok.Data;

import java.util.List;

@Data
public class SessionItem {
    private String  token;
    private String  userId;
    private String  firstName;
    private String  lastName;
    private String  email;
    private List<String> roles;
}
