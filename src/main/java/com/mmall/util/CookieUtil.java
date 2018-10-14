package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".wpj.com";
    private final static String COOKIE_NAME = "login_token";
    public static void writeLoginToken(HttpServletResponse response,String login_token){

        Cookie ck = new Cookie(COOKIE_NAME,login_token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setMaxAge(60 * 60 * 24 * 365);//如果不设置就不会写入硬盘，就只存在内存当中，在当前页面有效，如果值为-1则表示永久有效
        ck.setPath("/");//设置在根目录下，这样根目录所有页面都可以获取到该cookie
        log.info("cookie_name:{},cookie_value:{}",COOKIE_NAME,login_token);
        response.addCookie(ck);

    }
    public static String readLoginToken(HttpServletRequest request){
        Cookie [] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    return ck.getValue();
                }
            }
        }
        return null;
    }
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie [] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setMaxAge(0);
                    ck.setPath("/");
                    ck.setDomain(COOKIE_DOMAIN);
                    log.info("del cookie_name:{} cookie_value:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }

    }
}
