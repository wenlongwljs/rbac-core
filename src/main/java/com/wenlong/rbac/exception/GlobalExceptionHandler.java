package com.wenlong.rbac.exception;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @项目名称：wyait-manage
 * @类名称：GlobalExceptionHandler
 * @类描述：统一异常处理，包括【普通调用和ajax调用】
 * </br>ControllerAdvice来做controller内部的全局异常处理，但对于未进入controller前的异常，该处理方法是无法进行捕获处理的，SpringBoot提供了ErrorController的处理类来处理所有的异常(TODO)。
 * </br>1.当普通调用时，跳转到自定义的错误页面；2.当ajax调用时，可返回约定的json数据对象，方便页面统一处理。
 * @创建人：wyait
 * @创建时间：2018年5月22日 上午11:44:55 
 * @version：
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static final String DEFAULT_ERROR_VIEW = "error";

    /**
     * 异常处理类
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ModelAndView errorHandler(HttpServletRequest request,Exception e) {

        ModelAndView mv=new ModelAndView();
        if(e instanceof UnauthorizedException){
            mv.setViewName("403");
        }else{
            mv.setViewName("error");
        }

        return mv;
    }
}