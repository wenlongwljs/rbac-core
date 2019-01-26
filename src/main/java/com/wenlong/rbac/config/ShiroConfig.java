package com.wenlong.rbac.config;

import com.wenlong.rbac.shiro.RbacRealm;
import com.wenlong.rbac.shiro.RbacSessionManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-25
 */
@Component("authenticator")
@Configuration
public class ShiroConfig {
    private final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    /**
     * 自定义Realm 实现登陆认证和授权
     * @return
     */
    @Bean
    public RbacRealm rbacRealm(){
        RbacRealm realm = new RbacRealm();
        realm.setCredentialsMatcher(hashedCredentialsMatcher());//凭证匹配器
        realm.setCachingEnabled(false);//不使用缓存
        return realm;
    }

    @Bean
    public SessionsSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        //自定义session管理,可以不设置，采用默认的SessionManager
        securityManager.setSessionManager(sessionManager());
        //自定义缓存实现
        //securityManager.setCacheManager(ehCacheManager());
        securityManager.setRealm(rbacRealm());
        return securityManager;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * ）
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(){
        logger.info("ShiroConfig.shiroFilter()");

        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager());

        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/**", "authc");
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面

        filterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        filterFactoryBean.setSuccessUrl("/index");
        //未授权界面;
        filterFactoryBean.setUnauthorizedUrl("/403");
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return filterFactoryBean;
    }

    //下面的代码可以不要

//    @Bean
//    public CacheManager cacheManager(){
//        return new EhCacheManager();
//    }

    /**
     * //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
     * @return
     */
    @Bean
    public SessionDAO sessionDAO(){
        return new EnterpriseCacheSessionDAO();
    }

    /**
     * 传统结构项目中，shiro从cookie中读取sessionId以此来维持会话，在前后端分离的项目中（也可在移动APP项目使用），
     * 我们选择在ajax的请求头中传递sessionId，因此需要重写shiro获取sessionId的方式。
     * 自定义ShiroSessionManager类继承DefaultWebSessionManager类，重写getSessionId方法
     * @return
     */
    @Bean
    public SessionManager sessionManager(){
        RbacSessionManager manager = new RbacSessionManager();
        //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
        manager.setSessionDAO(sessionDAO());
        manager.setGlobalSessionTimeout(3600000);
        manager.setSessionValidationInterval(3600000);
        return manager;
    }


}
