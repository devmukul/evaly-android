package bd.com.evaly.evalyshop.ui.order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.Utils;

public class PayViaCard extends AppCompatActivity {


    WebView webView;
    ProgressDialog prDialog;
    String url = "";

    ProgressBar progressBar;
    private boolean loadingFinished = false;
    private boolean redirect = false;
    private boolean isShowed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getPackageManager().hasSystemFeature("android.software.webview") && Utils.isPackageExisted("com.google.android.webview", this)) {

        }else {
            Toast.makeText(this, "Please install WebView from Google Play Store", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_pay_via_card);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay via card");

        Intent intent = getIntent();

        url = intent.getStringExtra("url");


        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAppCacheEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                view.loadUrl(urlNewString);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                if (url.contains("evaly.com.bd")) {

                    Toast.makeText(PayViaCard.this, "Payment successful! If your order's payment status doesn't get updated within 5 minutes, please contact support.", Toast.LENGTH_LONG);

                    setResult(Activity.RESULT_OK);
                    finish();

                    return;
                }

                super.onPageStarted(view, url, favicon);
                loadingFinished = false;

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                if(!redirect){
                    loadingFinished = true;
                }

                if(loadingFinished && isShowed){
                    isShowed = false;

                } else{
                    redirect = false;
                }

                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });

        webView.loadUrl(url);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}