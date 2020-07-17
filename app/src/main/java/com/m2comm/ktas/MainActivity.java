package com.m2comm.ktas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.GzipRequestInterceptor;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.m2comm.ktas.databinding.ActivityMainBinding;
import com.m2comm.ktas.module.ChromeclientPower;
import com.m2comm.ktas.module.CustomHandler;
import com.m2comm.ktas.module.Custom_SharedPreferences;
import com.m2comm.ktas.module.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private CustomHandler customHandler;
    private ChromeclientPower chromeclient;

    private Timer timer;
    private TimerTask timerTask;
    private Custom_SharedPreferences csp;
    private TextView topTv;
    private int timeCount;

    private int nowStep = 1;
    private int totalStep = 4;

    private int timerCount = 0;
    private boolean isMore = false;
    private boolean isEnd = false;

    String url = "";
    FrameLayout timerV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.binding.setMain(this);
        this.init();
    }

    private void init() {
        this.csp = new Custom_SharedPreferences(this);
        this.customHandler = new CustomHandler(this);
        this.topTv = findViewById(R.id.top);
        this.timerV = findViewById(R.id.timerV);

        Intent intent = getIntent();
        this.url = intent.getStringExtra("paramUrl");

        this.chromeclient = new ChromeclientPower(this, this, this.binding.webview );
        this.binding.webview.setWebViewClient(new WebviewCustomClient());
        this.binding.webview.setWebChromeClient(this.chromeclient);
        this.binding.webview.getSettings().setUseWideViewPort(true);
        this.binding.webview.getSettings().setJavaScriptEnabled(true);
        this.binding.webview.getSettings().setLoadWithOverviewMode(true);
        this.binding.webview.getSettings().setDefaultTextEncodingName("utf-8");
        this.binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.binding.webview.getSettings().setSupportMultipleWindows(false);
        this.binding.webview.getSettings().setDomStorageEnabled(true);
        this.binding.webview.getSettings().setBuiltInZoomControls(true);
        this.binding.webview.getSettings().setDisplayZoomControls(false);

        if ( !this.url.equals("") ) {
            Log.d("urll==",this.url);
            String[] cut = this.paramCut(this.url);
            this.timerCount = Integer.parseInt(cut[0]);
            this.nowStep = Integer.parseInt(cut[1]);
            this.csp.put("nowUrl",this.url);
        }

        this.timeCount = this.csp.getValue("timeCount", 0);
        if ( this.timeCount <= 0 )this.binding.webview.loadUrl(this.url);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity=", "onResume()");
        //종료되어서 시간이 있을경우
        this.timeCount = this.csp.getValue("timeCount", 0);
        if ( this.timeCount > 0 ) {
            this.binding.webview.loadUrl(this.csp.getValue("nowUrl",""));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity=", "onDestroy()");
        this.timer.cancel();
        if (this.timeCount < this.timerCount) {
            csp.put("timeCount", timeCount);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity=", "onStop()");

        if ( timer != null ) {
            timer.cancel();
        }
        if (this.timeCount < this.timerCount) {
            csp.put("timeCount", this.timeCount);
        }
    }

    private void progressSetting(int maxCount) {
        //this.binding.progress.setMax(maxCount);
        //this.binding.progress.setProgress(this.timeCount);
        if (this.isMore)return;
        this.timerSetting(maxCount);
    }

    private void timerSetting(final int maxCount) {
        Log.d("MAAMAMAMX=",maxCount+"_");
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                timeCount += 1;
                if (timeCount > maxCount) {
                    timer.cancel();
                    Message msg = customHandler.obtainMessage();
                    msg.what = CustomHandler.FINISH_QUESTION;
                    customHandler.sendMessage(msg);
                    return;
                }

                csp.put("timeCount", timeCount);
                //타이머 변경
                Message msg = customHandler.obtainMessage();
                msg.what = CustomHandler.MAIN_CHANGE;
                customHandler.sendMessage(msg);
            }
        };

        this.timer = new Timer();
        this.timer.schedule(this.timerTask, 0, 1000);
    }

    public void timeTextChanger() {
        //this.binding.progress.setProgress(this.timeCount);
        binding.timer.setText(zeroPoint(String.valueOf(timerCount - timeCount)));
    }

    public void nextView() {

//        if ( csp.getValue("selectNum","0").equals("0") ) {
//            Toast.makeText(this , this.nowStep+"번 문제 \n선택 답 선택안함.",Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this , this.nowStep+"번 문제 \n선택 답 "+csp.getValue("selectNum","0")+" 번",Toast.LENGTH_SHORT).show();
//        }
        timer.cancel();
        isMore = false;
        if (!this.isEnd) this.binding.webview.loadUrl(Global.submitUrl+"?ans_num=" +nowStep+"&ans_val="+csp.getValue("selectNum","0"));
        //questionReset();
    }

    private void questionReset() {
        this.timeCount = 0;
        csp.put("selectNum",0);
        csp.put("timeCount", 0);
        csp.put("nowUrl","");
    }

    private void allReset() {
        if ( timer != null ) {
            timer.cancel();
        }
        this.nowStep = 1;
        this.timeCount = 0;
        csp.put("timeCount", 0);
        csp.put("selectNum",0);
        csp.put("nowUrl","");
        //this.binding.webview.loadUrl("http://ezv.kr/shin/ktas/step.php?page="+this.nowStep);
    }

    public String zeroPoint(String data) {
        data = data.trim();
        if (data.length() == 1) {
            data = "0" + data;
        }

        return data;
    }

    private class WebviewCustomClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String[] urlCut = url.split("/");
            Log.d("NowUrl", url);

            if (urlCut[urlCut.length - 1].equals("back.php")) {

                if (binding.webview.canGoBack()) {
                    binding.webview.goBack();
                } else {
                    finish();
                }
                return true;

            } else if ( url.contains("end.php") ) {

                Log.d("abc123","kkkk123");
                allReset();
                isEnd = true;
                timerV.setVisibility(View.GONE);

            } else if (url.contains("limit_time")) {

                if ( timer != null ) {
                    timer.cancel();
                }
                questionReset();
                isMore = false;
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("onPageStarted",url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d("onLoadResource",url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("onPageFinished",url);
            if (url.contains("end.php"))return;

            String[] timeCut = paramCut(url);
            timerCount = Integer.parseInt(timeCut[0]);
            nowStep = Integer.parseInt(timeCut[1]);

            Log.d("limitTime=", timerCount+"_");
            Log.d("page=", nowStep+"_");
            Log.d("timeCount=", timeCount+"_");

            Log.d("rerere=","isMore="+isMore);

            if ( timeCount > 0) isMore = false;
            if ( !isMore && timerCount > 0) {
                if (timeCount <= 0)questionReset();
                csp.put("nowUrl",url);
                progressSetting(timerCount);
                isMore = true;
            }

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(getApplicationContext(), "서버와 연결이 끊어졌습니다", Toast.LENGTH_SHORT ).show();
            view.loadUrl("about:blank");
        }
    }


    private String[] paramCut(String url) {

        String [] cut = new String[2];
        String[] urlCut = url.split("\\?");
        String[] paramCut = urlCut[1].split("&");
        String[] param1 = paramCut[0].split("=");//time
        String[] param2 = paramCut[1].split("=");//page
        cut[0] = param1[1];
        cut[1] = param2[1];

        return cut;
    }

    @Override
    public void onBackPressed() {

    }
}