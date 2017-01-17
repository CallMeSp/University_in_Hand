package com.sp.loginapp.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/1/16.
 */

public interface Root_login {
    @POST("http://jwxt.njupt.edu.cn/")
    Call<ResponseBody> getInfo(@Path("txtUserName") String username, @Path("TextBox2") String passwd, @Path("txtSecretCode") String secretcode);

    @GET("http://jwxt.njupt.edu.cn/CheckCode.aspx")
    Observable<String> getbit();
}



