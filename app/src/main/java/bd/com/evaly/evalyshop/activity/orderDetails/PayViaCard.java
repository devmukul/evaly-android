package bd.com.evaly.evalyshop.activity.orderDetails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.util.UserDetails;

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
        setContentView(R.layout.activity_pay_via_card);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_vector);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay via card");

        Intent intent = getIntent();

        url = intent.getStringExtra("url");


        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();


        UserDetails userDetails = new UserDetails(this);



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
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);


            }




            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                if (url.contains("evaly.com.bd/")){

                    Toast.makeText(PayViaCard.this, "Payment successful!", Toast.LENGTH_LONG);

                    setResult(Activity.RESULT_OK);
                    finish();

                }

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
