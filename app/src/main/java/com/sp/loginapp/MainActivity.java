package com.sp.loginapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sp.loginapp.Model.SerializableCookies;
import com.sp.loginapp.Model.SerializableOkHttpCookies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private List<Cookie> cookies=new ArrayList<>();
    private ArrayList<String> url_list=new ArrayList<>();
    private static final String TAG = "MainActivity";
    private String result="";
    @BindView(R.id.info)TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initconfig();
    }
    private void initconfig(){
        Intent intent=getIntent();
        url_list=intent.getStringArrayListExtra("url");
        Bundle bundle=intent.getBundleExtra("cookies_list");
        SerializableCookies serial=(SerializableCookies)bundle.getSerializable("list_cookies");
        List<SerializableOkHttpCookies> cookies_in_Ser=serial.getCookies();
        for (SerializableOkHttpCookies c:cookies_in_Ser){
            Cookie co=c.getCookies();
            cookies.add(co);
        }
        //Log.e(TAG, "initconfig: "+"url:"+url_list.toString() );
        Log.e(TAG, "initconfig: "+"cookies:"+cookies.toString() );
        Request request=new Request.Builder()
                .url(url_list.get(23))
                .build();
        OkHttpClient client=new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Log.e(TAG, "saveFromResponse: ");
                    }
                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        Log.e(TAG, "loadForRequest: ");
                        return cookies;
                    }
                }).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str=response.body().string();
                Log.e(TAG, "onResponse: "+str);

                Document doc= Jsoup.parse(str);
                Elements elements=doc.select("tr");
                for (Element element:elements){
                    result+=element.text()+"\n";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(result);
                    }
                });
            }
        });
    }
}
