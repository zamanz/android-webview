package com.zaman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private LinearLayout progressBarLayout;
    private ProgressBar progressBar;
    private WebView webView;
    private SwipeRefreshLayout swipeRefresh;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find by id
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);
        //set progress bar
        progressBar.setProgress(0);
        progressBar.setMax(100);
        //web view client
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBarLayout.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefresh.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });
        //web view chrome client
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                progressBarLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                setTitle("Loading...");
                if(progress == 100){
                    progressBarLayout.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, progress);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        //set url
        webView.loadUrl("http://192.168.0.120/sepl-cms");
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBarLayout.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(true);
                webView.reload();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    private ViewTreeObserver.OnScrollChangedListener scrollTopListener;
    @Override
    protected void onStart() {
        super.onStart();
        swipeRefresh.getViewTreeObserver().addOnScrollChangedListener(scrollTopListener= new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(webView.getScrollY() == 0){
                    swipeRefresh.setEnabled(true);
                }
                else {
                    swipeRefresh.setEnabled(false);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        swipeRefresh.getViewTreeObserver().removeOnScrollChangedListener(scrollTopListener);
        super.onStop();
    }
}