package com.atguigu.myzhxy.pojo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Su
 * @create 2022-05-11 12:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

    private String username;
    private String password;
    private String verifiCode;
    private Integer userType;
}
