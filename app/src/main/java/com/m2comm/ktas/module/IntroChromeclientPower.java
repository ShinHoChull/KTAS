package com.m2comm.ktas.module;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.m2comm.ktas.R;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.internal.http.HttpHeaders;





/*
 *  WebChromeClient 같은경우는 재정의를 해주지 않으면 WebView URL이 그대로 노출이 된다.
 * */

public class IntroChromeclientPower extends WebChromeClient
{
    Activity activity;
    Context context,subContext;
    WebView webView;
    private int doubleCheckedPopUp = 0;

    //File Upload를 위한 ...

    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public static final int FILECHOOSER_NORMAL_REQ_CODE = 2833;
    public static final int FILECHOOSER_LOLLIPOP_REQ_CODE = 2779;

    public IntroChromeclientPower(Activity activity, Context context, WebView webView) {
        this.context = context;
        this.subContext = activity;
        this.webView = webView;
        this.activity = activity;
    }

    // For Android < 3.0
    public void openFileChooser( ValueCallback<Uri> uploadMsg) {
        Log.d("MainActivity", "3.0 <");
        openFileChooser(uploadMsg, "");
    }
    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.d("MainActivity", "3.0+");
        filePathCallbackNormal = uploadMsg;
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
    }

    // For Android 4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Log.d("MainActivity", "4.1+");
        openFileChooser(uploadMsg, acceptType);
    }
    
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        
        Log.d("MainActivity", "5.0+");
        if (filePathCallbackLollipop != null) {
            filePathCallbackLollipop.onReceiveValue(null);
            filePathCallbackLollipop = null;
        }
        filePathCallbackLollipop = filePathCallback;
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_LOLLIPOP_REQ_CODE);
        
        return true;

    }


    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        Log.d("windowOpen", "open Url ="+view);
       // MainActivity.is_popup = false;

        webView.removeAllViews();

        final WebView childView = new WebView(this.activity);
//        subContext = activity;
//
//
//        childView.getSettings().setJavaScriptEnabled(true);
//        childView.getSettings().setDefaultTextEncodingName("utf-8");
//        childView.getSettings().setUseWideViewPort(true);
//        childView.getSettings().setDomStorageEnabled(true);
//
//
//        //팝업을 만들기위한 과정
//        childView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        childView.requestFocus();
//        childView.setWebChromeClient(this);
//        childView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//
//            /*Window.open 팝업 처리를 위하여 Main 레이아웃에 또다른 레이아웃의 스택을 쌓았다.
//            BackKey 를 누르면 Main 레이아웃이 작동을 하기 때문에 스텍에 올린 레이아웃이 삭제가안되어서
//             Main에서 removeAll을 통해서 스텍이 쌓인것을 모두 날렸다.
//             하지만 팝업창이 재요청이 되지않아 하는 수 없이 카운터를 적용시켜 설정값과 동일하다면 온클로즈윈도우를 호출하여
//              Webview를 destory시켜 버렸다.
//            */
//                if (MainActivity.is_popup == true) {
//                    onCloseWindow(childView);
//                    Log.e("onCloseWindow", "" + "if In");
//                }
//                Log.e("onPageStarted", "onPageStarted");
//            }
//        });
//
//        webView.addView(childView);
//        //팝업창이 최상위 WebViewStack의 첫부분(표시되는)을 focus하는 것 같은 느낌이 든다.
//        //그래서 oncreateWindow가 호출이되면 웹뷰의 page를 강제적으로 맨위로 focus를 주었다.
//
//        webView.pageUp(true);
//
//
//        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//        transport.setWebView(childView);
//        //transport.getWebView().pageUp(true);
//
//        resultMsg.sendToTarget();

        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
//        if(MainActivity.is_popup == true) {
//            window.destroy();
//        }

        //매개변수로 받은 webview 객체를 removeAllViews 시킴으로써 클로즈윈도우할때 같이 삭제를 해준다.
        webView.removeAllViews();
        doubleCheckedPopUp = 0;
        Log.e("windowClose", "close");
    }

    //dialogWindow cusomize 가능.
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
                             final JsResult result) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(view.getContext()).setTitle("알림")
		.setMessage(message)
		.setPositiveButton(android.R.string.ok,

				new AlertDialog.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				}).setCancelable(false)
				.create()
				.show();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, final String url, String message,
                               final JsResult result) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(view.getContext())
		.setTitle("알림")
		.setMessage(message)
		.setPositiveButton("YES", 
				new AlertDialog.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
                    result.confirm();
				}
			})
			.setNegativeButton("NO", 
					new AlertDialog.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					result.cancel();
				}
			})
			.setCancelable(false)
			.create()
			.show();
		return true;
	}
}
