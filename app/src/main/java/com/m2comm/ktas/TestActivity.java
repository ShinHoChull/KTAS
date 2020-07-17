package com.m2comm.ktas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.IpSecAlgorithm;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.m2comm.ktas.module.ChromeclientPower;
import com.m2comm.ktas.module.CustomHandler;
import com.m2comm.ktas.module.Custom_SharedPreferences;
import com.m2comm.ktas.module.Global;
import com.m2comm.ktas.module.IntroChromeclientPower;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {

    private WebView webView;
    private ChromeclientPower chromeclient;
    private CustomHandler customHandler;
    private String TAG = "TEstActivity=";
    private Custom_SharedPreferences csp;
    private FrameLayout timerV;
    private TextView timerTv;
    private String paramUrl;

    //타이머
    private Timer timer;
    private TimerTask timerTask;

    private ImageView submitBt;

    private int totalTime = -1;
    private int page = -1;
    private int timeCount = 0;

    private boolean isFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        this.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //완전 종료 데이터저장,타이머 정지
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.csp.put("timeCount", timeCount);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");

        if (this.timer != null) {
            this.timer.cancel();
        }
        this.csp.put("timeCount", timeCount);

        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        Log.d(TAG, timeCount + "_");
        Log.d(TAG, totalTime + "_");
        if (this.timer != null && !this.isFinish) timerSetting();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        //this.timeCount = this.csp.getValue("timeCount", 0);
        Log.d("onResume=", timeCount + "_");
    }

    private void init() {
        this.webView = findViewById(R.id.webview);
        this.timerV = findViewById(R.id.timerV);
        this.timerTv = findViewById(R.id.timerTv);
        this.submitBt = findViewById(R.id.submitBt);

        Intent intent = getIntent();
        this.paramUrl = intent.getStringExtra("url");

        this.chromeclient = new ChromeclientPower(this, this, this.webView);
        this.customHandler = new CustomHandler(this);
        this.csp = new Custom_SharedPreferences(this);

        this.webView.setWebViewClient(new WebviewCustomClient());
        this.webView.setWebChromeClient(this.chromeclient);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setDefaultTextEncodingName("utf-8");
        //this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //this.webView.getSettings().setSupportMultipleWindows(false);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setDisplayZoomControls(false);

        this.webView.loadUrl(this.paramUrl);

        this.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btClickNextView(true);
            }
        });

    }

    public void timeTextChanger() {
        //this.binding.progress.setProgress(this.timeCount);
        timerTv.setText(zeroPoint(String.valueOf(totalTime - timeCount)));
    }

    private void reset() {
        csp.put("timeCount", 0);
        csp.put("selectNum", "0");
        csp.put("cycle","");
        if (timer != null) timer.cancel();
        timeCount = 0;
    }

    private void firstStartloadUrl() {
        Log.d("loadUrl=", this.paramUrl);
        Log.d("totalTime=", this.totalTime + "_");
        this.webView.loadUrl(this.paramUrl);

        if (paramUrl.contains("end.php")) {

        } else {
            timerSetting();
        }

    }

    private void loadUrl() {
        isFinish = false;
        Log.d("loadUrl=", this.paramUrl);
        Log.d("totalTime=", this.totalTime + "_");
        this.webView.loadUrl(this.paramUrl);

        timerSetting();
    }

    private void cycle2loadUrl() {
        this.webView.loadUrl(this.paramUrl);
    }


    private void timerSetting() {
        this.timerTask = new TimerTask() {
            @Override
            public void run() {

                timeCount += 1;
                if (timeCount > totalTime) {

                    Message msg = customHandler.obtainMessage();
                    msg.what = CustomHandler.TEST_FINISH_QUESTION;
                    customHandler.sendMessage(msg);

                    return;
                }

                csp.put("timeCount", timeCount);

//                //타이머 변경
                Message msg = customHandler.obtainMessage();
                msg.what = CustomHandler.TEST_MAIN_CHANGE;
                customHandler.sendMessage(msg);
                Log.d("timeCount=", timeCount + "_");
            }
        };

        this.timer = new Timer();
        this.timer.schedule(this.timerTask, 0, 1000);
        this.timerV.setVisibility(View.VISIBLE);
    }

    public void btClickNextView(boolean isClick) {

//        if (this.csp.getValue("selectNum", "0").equals("0")) {
//            Toast.makeText(this, this.page + "번 문제 \n선택 답 선택안함.", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, this.page + "번 문제 \n선택 답 " + this.csp.getValue("selectNum", "0") + " 번", Toast.LENGTH_SHORT).show();
//        }
        String end_yn = "N";
        String sel_yn = "N";

        if (isClick) {
            sel_yn = "Y";
        }

        AndroidNetworking.post(Global.submitUrl)
                .addBodyParameter("test_code", csp.getValue("test_code", ""))
                .addBodyParameter("ans_num", String.valueOf(page))
                .addBodyParameter("ans_val", this.csp.getValue("selectNum", "0"))
                .addBodyParameter("edu_type", this.csp.getValue("edu_type", ""))
                .addBodyParameter("sel_yn", sel_yn)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("nextMethod=", response.toString());
                        try {
                            if (response.getString("success").equals("Y")) {
                                paramUrl = response.getString("url");
                                csp.put("cycle", response.isNull("cycle") ? "" : response.getString("cycle"));

                                csp.put("ans_num", response.isNull("ans_num") ? 0 : response.getInt("ans_num"));
                                page = response.isNull("ans_num") ? 0 : response.getInt("ans_num");


                                if ( response.getString("cycle").equals("2") ) {
                                    if ( !response.isNull("limit_time") ) {
                                        csp.put("limit_time", response.isNull("limit_time") ? "" : response.getString("limit_time"));
                                        totalTime = response.isNull("limit_time") ? -1 : Integer.parseInt(response.getString("limit_time"));
                                        reset();
                                        loadUrl();
                                    } else {
                                        cycle2loadUrl();
                                    }

                                } else {
                                    csp.put("limit_time", response.isNull("limit_time") ? "" : response.getString("limit_time"));
                                    totalTime = response.isNull("limit_time") ? -1 : Integer.parseInt(response.getString("limit_time"));

                                    reset();
                                    loadUrl();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }



    public void nextView(boolean isClick) {

//        if (this.csp.getValue("selectNum", "0").equals("0")) {
//            Toast.makeText(this, this.page + "번 문제 \n선택 답 선택안함.", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, this.page + "번 문제 \n선택 답 " + this.csp.getValue("selectNum", "0") + " 번", Toast.LENGTH_SHORT).show();
//        }
        String end_yn = "N";
        String sel_yn = "N";

        if (isClick) {
            sel_yn = "Y";
        }

        if (csp.getValue("cycle", "").equals("2")) {
            end_yn = "Y";
        }

        AndroidNetworking.post(Global.submitUrl)
                .addBodyParameter("test_code", csp.getValue("test_code", ""))
                .addBodyParameter("ans_num", String.valueOf(page))
                .addBodyParameter("ans_val", this.csp.getValue("selectNum", "0"))
                .addBodyParameter("edu_type", this.csp.getValue("edu_type", ""))
                .addBodyParameter("sel_yn", sel_yn)
                .addBodyParameter("end_yn", end_yn)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("nextMethod=", response.toString());
                        try {
                            reset();
                            if (response.getString("success").equals("Y")) {
                                paramUrl = response.getString("url");

                                csp.put("limit_time", response.isNull("limit_time") ? "" : response.getString("limit_time"));
                                totalTime = response.isNull("limit_time") ? -1 : Integer.parseInt(response.getString("limit_time"));

                                csp.put("ans_num", response.isNull("ans_num") ? 0 : response.getInt("ans_num"));
                                page = response.isNull("ans_num") ? 0 : response.getInt("ans_num");

                                csp.put("cycle", response.isNull("cycle") ? "" : response.getString("cycle"));
                                if (paramUrl.contains("end.php")) {
                                    isFinish = true;
                                    timerV.setVisibility(View.GONE);
                                    reset();
                                    cycle2loadUrl();
                                } else {
                                    loadUrl();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


    private class WebviewCustomClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("NowUrl", url);

            if (url.contains("end.php")) {
                //타이머 종료하기.
                isFinish = true;
                timerV.setVisibility(View.GONE);
                reset();

            } else if (url.contains("logout_proc.php")) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            } else if (url.contains("start.php")) {
                AndroidNetworking.post(Global.get_start)
                        .addBodyParameter("test_code", csp.getValue("test_code", ""))
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Testresponse=", response.toString());
                                try {
                                    if (response.getString("success").equals("Y")) {
                                        paramUrl = response.getString("url");

                                        csp.put("limit_time", response.isNull("limit_time") ? "" : response.getString("limit_time"));
                                        totalTime = response.isNull("limit_time") ? Integer.parseInt(response.getString("base_limit_time")) : Integer.parseInt(response.getString("limit_time"));

                                        csp.put("ans_num", response.isNull("ans_num") ? 0 : response.getInt("ans_num"));
                                        page = response.isNull("ans_num") ? 0 : response.getInt("ans_num");

                                        csp.put("cycle", response.isNull("cycle") ? "" : response.getString("cycle"));

                                        timeCount = csp.getValue("timeCount", 0);

                                        firstStartloadUrl();


                                    } else {
                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("onPageStarted", url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d("onLoadResource", url);
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("onPageFinished", url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d("onPageFinished", description);
            Toast.makeText(getApplicationContext(), "서버와 연결이 끊어졌습니다", Toast.LENGTH_SHORT).show();
            view.loadUrl("about:blank");
        }
    }

    private int[] paramCut(String url) {

        int[] cut = new int[2];
        String[] urlCut = url.split("\\?");
        String[] paramCut = urlCut[1].split("&");
        String[] param1 = paramCut[0].split("=");
        String[] param2 = paramCut[1].split("=");
        cut[0] = Integer.parseInt(param1[1]);//time
        cut[1] = Integer.parseInt(param2[1]);//page

        return cut;
    }

    public String zeroPoint(String data) {
        data = data.trim();
        if (data.length() == 1) {
            data = "0" + data;
        }
        return data;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 메뉴키 막는방법..?
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {

    }


}