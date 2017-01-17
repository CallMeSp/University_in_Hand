package com.sp.loginapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sp.loginapp.API.Root_login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/1/16.
 */

public class LoginActivity extends Activity {
    @BindView(R.id.account)EditText Edit_account;
    @BindView(R.id.password)EditText Edit_passwd;
    @BindView(R.id.check)EditText Edit_check;
    @BindView(R.id.img)ImageView Img_check;
    @BindView(R.id.sign_in_button)Button button;
    @BindView(R.id.update)Button button0;
    private String account="",passwd="",secretcode="";
    private static final String TAG = "LoginActivity";
    private OkHttpClient mclient=new OkHttpClient();
    private String cook="",viewstate="";
    private List<Cookie> mylist;
    @Override
    protected void onCreate(Bundle savedINstanceState){
        super.onCreate(savedINstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        GetVerification();
        login();
    }
    private void login(){
        Edit_account.setText("B14040210");
        Edit_passwd.setText("sp026818");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = Edit_account.getText().toString();
                passwd = Edit_passwd.getText().toString();
                secretcode = Edit_check.getText().toString();


                //获取结果
                FormBody formbody = new FormBody.Builder()
                        .add("__VIEWSTATE",viewstate)
                        .add("txtUserName", account)
                        .add("TextBox2", passwd)
                        .add("txtSecretCode", secretcode)
                        .add("ASP.NET_SessionId",cook.substring(19,43))
                        .add("RadioButtonList1", "%D1%A7%C9%FA")
                        .add("Button1","")
                        .add("lbLanguage","")
                        .build();
                Request request2=new Request.Builder()
                        .url("http://202.119.225.34/default2.aspx")
                        .post(formbody)
                        .build();
                OkHttpClient postClient2=new OkHttpClient.Builder()
                        .cookieJar(new CookieJar() {
                            @Override
                            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                Log.e(TAG, "login in  save" );
                            }
                            @Override
                            public List<Cookie> loadForRequest(HttpUrl url) {
                                Log.e(TAG, "loadForRequest: login" +mylist.toString());
                                return mylist;
                            }
                        }).build();
                postClient2.newCall(request2)
                        .enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                String str=response.body().string();
                                Document doc=Jsoup.parse(str);
                                Elements eles=doc.select("title");
                                Element ele=eles.first();
                                String x=ele.text();
                                Log.e(TAG, "onResponse: "+str);
                                if (x.length()=="正方教务管理系统".length()){
                                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(i);
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"请重新输入",Toast.LENGTH_SHORT).show();
                                            GetVerification();
                                        }
                                    });

                                }
                                Log.e(TAG, "onResponse: "+"title:"+x );
                            }
                        });

            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetVerification();
            }
        });
    }
    private void GetVerification(){
        Observable.just("http://202.119.225.34/CheckCode.aspx")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(String s) {
                        //获取验证码以及cookie
                        Request request=new Request.Builder().url(s).build();
                        OkHttpClient myclient=new OkHttpClient.Builder().cookieJar(new CookieJar() {
                            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
                            @Override
                            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                cookieStore.put(url.host(), cookies);
                                mylist=cookies;
                                cook=mylist.toString();
                                Log.e(TAG, "saveFromResponse: "+cook+","+cook.substring(19,43));
                            }
                            @Override
                            public List<Cookie> loadForRequest(HttpUrl url) {
                                List<Cookie> cookies = cookieStore.get(url.host());
                                return cookies != null ? cookies : new ArrayList<Cookie>();
                            }
                        }).build();
                        myclient.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                            }
                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                InputStream is = response.body().byteStream();
                                final Bitmap bm = BitmapFactory.decodeStream(is);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Img_check.setImageBitmap(bm);
                                    }
                                });
                            }
                        });
                        //获取viewState
                        Request request1=new Request.Builder()
                                .url("http://202.119.225.34/default2.aspx")
                                .build();
                        OkHttpClient postClient=new OkHttpClient.Builder()
                                .build();
                        postClient.newCall(request1)
                                .enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(okhttp3.Call call, IOException e) {
                                    }
                                    @Override
                                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                        String str=response.body().string();
                                        Document document= Jsoup.parse(str);
                                        Elements elements=document.select("input[name=__VIEWSTATE]");
                                        Element element=elements.get(0);
                                        viewstate=element.attr("value");
                                        Log.e(TAG, "onResponse: valuestate"+viewstate );
                                    }
                                });
                    }
                });
    }
    public void getViewState(){

    }

}

