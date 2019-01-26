package com.wenlong.rbac.shiro;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author wenlongwljs@163.com
 * @date 2019-01-26
 */
public class ShiroUtils {
    /**
     * 随机生成 salt 需要指定 它的字符串的长度
     *
     * @param len 字符串的长度
     * @return salt
     */
    public static String generateSalt(int len) {
        //一个Byte占两个字节
        int byteLen = len >> 1;
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        return secureRandom.nextBytes(byteLen).toHex();
    }

    /**
     * 获取加密后的密码，需要指定 hash迭代的次数
     *
     * @param hashAlgorithm  hash算法名称 MD2、MD5、SHA-1、SHA-256、SHA-384、SHA-512、etc。
     * @param password       需要加密的密码
     * @param salt           盐
     * @param hashIterations hash迭代的次数
     * @return 加密后的密码
     */
    public static String encryptPassword(String hashAlgorithm, String password, String salt, int hashIterations) {
        SimpleHash hash = new SimpleHash(hashAlgorithm, password, salt, hashIterations);
        return hash.toString();
    }

    /**
     * 生成加密密码
     * @param
     */
    public static void main(String args[]) {
        // 密码加密 方式很多，任选
       /* String salt = RandomStringUtils.randomAlphanumeric(20);
        sysUser.setPassword(new Sha256Hash(sysUser.getPassword(), salt).toHex());*/
        String stringPwd = "12345";
        String salt = ShiroUtils.generateSalt(20 );

        String password = ShiroUtils.encryptPassword("MD5", stringPwd, salt, 2);
        System.out.println(password);
        System.out.println(salt);
    }
}
