package com.wenlong.rbac.shiro;


import com.wenlong.rbac.Service.UserInfoService;
import com.wenlong.rbac.domain.SysPermission;
import com.wenlong.rbac.domain.SysRole;
import com.wenlong.rbac.domain.UserInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-25
 */
public class RbacRealm extends AuthorizingRealm {
    private final Logger logger = LoggerFactory.getLogger(RbacRealm.class);

    @Autowired
    private UserInfoService userInfoService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        logger.info("RbacRealm-doGetAuthenticationInfo()");

        //加这一步的目的是在Post请求的时候会先进认证，然后再到请求
        if(authenticationToken.getPrincipal() == null){
            return null;
        }
        //UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //String name = token.getUsername();

        //获取用户的输入的账号.
        String userName = authenticationToken.getPrincipal().toString();
        //通过username从数据库中查找 UserInfo对象
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        UserInfo userInfo = userInfoService.findByUsername(userName);
        logger.info("UserInfo:"+userInfo);
        if(userInfo == null){
            //这里返回后会报出对应异常
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户名
                userInfo.getPassword(), //密码
                ByteSource.Util.bytes(userInfo.getSalt()),//salt=username+salt
                getName() //realm name
        );

        return authenticationInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("RbacRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        for(SysRole role : userInfo.getRoleList()){
            authorizationInfo.addRole(role.getRole());
            for(SysPermission permission : role.getPermissions()){
                authorizationInfo.addStringPermission(permission.getPermission());
            }
        }
        //当然也可以添加set集合：roles是从数据库查询的当前用户的角色，stringPermissions是从数据库查询的当前用户对应的权限
        return authorizationInfo;
    }
}
