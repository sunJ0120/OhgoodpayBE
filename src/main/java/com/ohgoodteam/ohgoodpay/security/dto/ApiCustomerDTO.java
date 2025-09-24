package com.ohgoodteam.ohgoodpay.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiCustomerDTO extends User {

    private String customerId;
    private String pwd;

    public ApiCustomerDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.customerId = username;
        this.pwd = password;
    }
}
