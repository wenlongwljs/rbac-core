package com.wenlong.rbac.repository;

import com.wenlong.rbac.domain.UserInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-25
 */
public interface UserInfoRepository extends CrudRepository<UserInfo ,Long> {

    //通过用户名查找用户信息
    UserInfo findByUsername(String userName);

}
