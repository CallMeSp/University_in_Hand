package com.sp.loginapp.Model;

import java.io.Serializable;
import java.util.List;

import okhttp3.Cookie;

/**
 * Created by Administrator on 2017/1/19.
 */

public class SerializableCookies implements Serializable {
    private List<SerializableOkHttpCookies> cookies;
    public void setCookies(List<SerializableOkHttpCookies> cookies){
        this.cookies=cookies;
    }
    public List<SerializableOkHttpCookies> getCookies(){
        return cookies;
    }
}
