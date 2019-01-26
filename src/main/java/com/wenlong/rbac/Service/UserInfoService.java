package com.wenlong.rbac.Service;

import com.wenlong.rbac.domain.UserInfo;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-25
 */
public interface UserInfoService {

    UserInfo findByUsername(String userName);
}
