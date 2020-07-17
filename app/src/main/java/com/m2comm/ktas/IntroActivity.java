package com.m2comm.ktas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.m2comm.ktas.module.ChromeclientPower;
import com.m2comm.ktas.module.Global;
import com.m2comm.ktas.module.IntroChromeclientPower;

public class IntroActivity extends AppCompatActivity {

    private WebView webView;
    private IntroChromeclientPower chromeclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        this.webView = findViewById(R.id.webview);

        this.chromeclient = new IntroChromeclientPower(this, this, this.webView );
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

        this.webView.loadUrl(Global.loginUrl);
    }

    private class WebviewCustomClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("NowUrl", url);
            String[] urlCut = url.split("/");
            String[] parmaCut = url.split("\\?");
            String param = "";
            Log.d("parmaCut=",parmaCut.length+"_");
            if ( parmaCut.length > 1 ) {
                param = parmaCut[1];
            }

            Log.d("urrllll=", param);


            if (urlCut[urlCut.length - 1].equals("back.php")) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
            } else if ( param.contains("limit_time") ) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                intent.putExtra("paramUrl",url);
                startActivity(intent);

                return true;
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
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(getApplicationContext(), "서버와 연결이 끊어졌습니다", Toast.LENGTH_SHORT ).show();
            view.loadUrl("about:blank");
        }
    }
}