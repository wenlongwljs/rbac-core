package com.wenlong.rbac.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping({"/","/index"})
    public String index(){
        return"/index";
    }

    /**
     * 登录
     *
     * shiro登录，shiro采用Facade模式（门面模式），所有与shiro的交互都通过Subject对象API。
     * 调用Subject.login后会触发RbacRealm的doGetAuthenticationInfo方法，进行具体的登录验证处理。
     *
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    @RequestMapping("/login")
    public String login(String userName, String password) {

        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            logger.info("用户名或密码不能为空");
            return "/login";
        }
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        try {
            currentUser.login(token);
            Session session = SecurityUtils.getSubject().getSession();
            return "/index";
        } catch (UnknownAccountException e) {
            logger.info("UnknownAccountException -- > 账号不存在：");
            return "/login";
        } catch (IncorrectCredentialsException e) {
            logger.info("IncorrectCredentialsException -- > 密码不正确");
            return "/login";
        }  catch (Exception e) {
            System.out.println("else -- >" + e);
            return "/login";
        }
    }

    @RequestMapping("/logout")
    public String logout(){
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return "/login";
    }

}