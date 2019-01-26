package com.wenlong.rbac.Service.impl;

import com.wenlong.rbac.Service.UserInfoService;
import com.wenlong.rbac.domain.UserInfo;
import com.wenlong.rbac.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-25
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo findByUsername(String userName) {
        UserInfo userInfo = userInfoRepository.findByUsername(userName);
        return userInfo;
    }
}
