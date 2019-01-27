# 在使用 shiro-spring-boot-web-starter 进行整合进行整合时的问题
* 注意开发web项目是shiro-spring-boot-web-starter 而不是 shiro-spring-boot-starter
* 使用 shiro-spring-boot-web-starter 进行整合时报 The bean 'securityManager', defined in class path resource [org/apache/shiro
  改成 shiro-spring 整合则没问题。原因是 使用 shiro-spring-boot-web-starter 进行整合时启动了自动配置，配置了SecurityManager这个实例，所以再配置的话会报这个错误
  解决方法：将配置文件的名称改成SecurityManager的子类比如DefaultWebSecurityManager进行注册
* 使用 shiro-spring-boot-web-starter 时报：
  Method filterShiroFilterRegistrationBean in org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration required a bean named 'shiroFilterFactoryBean' that could not be found.
  解决方法，将配置文件中的 ShiroFilterFactoryBean Bean 方法名改为 shiroFilterFactoryBean()
* 使用shiro-spring配置如下即可实现认证、授权、注解支持：

~~~ java
    /**
     * 自定义Realm 实现登陆认证和授权
     * @return RbacRealm
     */
    @Bean
    public RbacRealm rbacRealm(){
        RbacRealm realm = new RbacRealm();
        realm.setCredentialsMatcher(hashedCredentialsMatcher());//凭证匹配器
        realm.setCachingEnabled(false);//不使用缓存
        return realm;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        //自定义session管理,可以不设置，采用默认的SessionManager
        //securityManager.setSessionManager(sessionManager());
        //自定义缓存实现
        //securityManager.setCacheManager(ehCacheManager());
        securityManager.setRealm(rbacRealm());
        return securityManager;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * ）
     * @return HashedCredentialsMatcher
     */
    @Bean()
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @return AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
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

~~~
#### 如果使用shiro-spring-boot-web-starter 进行整合时，则还需要加一下代码，否则使用权限注解时会报错

~~~ java
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        return autoProxyCreator;
    }
~~~

* 配置未授权界面无效，只能通过异常处理类来捕获权限访问不足异常
  filterFactoryBean.setUnauthorizedUrl("/403");
  
~~~ java
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @return ModelAndView
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ModelAndView errorHandler(Exception e) {
        ModelAndView mv=new ModelAndView();
        if(e instanceof UnauthorizedException){
            mv.setViewName("403");
        }else{
            e.printStackTrace();
            mv.setViewName("error");
        }
        return mv;
    }
}
~~~