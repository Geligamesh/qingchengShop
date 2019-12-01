package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.util.org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    @Reference
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了UserDetailServiceImpl");

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new User(username, "$2a$10$RVyVdTsBYWIBlrAjXqna7eaudnLEw.hhzVXmReugpNypA1gb0Qj/i", grantedAuthorities);

    }

    public static void main(String[] args) {
        String salt = BCrypt.gensalt();
        String hashpw = BCrypt.hashpw("123456", salt);
        System.out.println(hashpw);
    }
}
