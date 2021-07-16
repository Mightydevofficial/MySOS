package com.example.mysosapplication;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.security.PrivateKey;

public class Notes extends AppCompatActivity {

    private WebView webView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        CustomWebViewClientNotes client = new CustomWebViewClientNotes(this);
        webView1 = findViewById(R.id.webView1);
        webView1.setWebViewClient(client);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl("https://sites.google.com/view/mightydev/notes?authuser=1");


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && this.webView1.canGoBack()){
            this.webView1.goBack();
            return true;

        }
        return super.onKeyDown(keyCode,event);

    }
}




class CustomWebViewClientNotes extends WebViewClient {
    private Activity activity;

    public CustomWebViewClientNotes(Activity activity) {
        this.activity = activity;

    }
    //API less than 24 Lolipop
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String Url) {
        return false;
    }
    //API more the 24 nugget
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        return false;

    }
}

