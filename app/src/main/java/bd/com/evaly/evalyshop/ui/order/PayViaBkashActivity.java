package bd.com.evaly.evalyshop.ui.order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;

public class PayViaBkashActivity extends BaseActivity {

    WebView webView;
    ProgressDialog prDialog;

    ProgressBar progressBar;
    UserDetails userDetails;
    String amount = "0.0", context = "order_payment", context_reference = "", paymentID = "";
    private boolean loadingFinished = false;
    private boolean redirect = false;
    private boolean isShowed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPackageManager().hasSystemFeature("android.software.webview") && Utils.isPackageExisted("com.google.android.webview", this)) {

        } else {
            Toast.makeText(this, "Please install WebView from Google Play Store", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_bkash_payment);
        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("bKash Payment");

        Intent intent = getIntent();
        amount = intent.getStringExtra("amount");
        context_reference = intent.getStringExtra("invoice_no");
        context = intent.getStringExtra("context");

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        userDetails = new UserDetails(this);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setAppCacheEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        String javascript = "(function() {" +
                "    var nTimer = setInterval(function() {" +
                "        if (typeof bKash == 'undefined') {" +
                "        } else {" +
                "            initBkash(); " +
                "   $(document).ready(function(){\n" +
                "       $(\"#bKash_button\").trigger('click'); \n" +
                "   });" +

                "            clearInterval(nTimer);" +
                "        }" +
                "    }, 500);" +
                "})();" +
                "function initBkash() {" +
                "    var paymentID = '';" +
                "    bKash.init({" +
                "        paymentMode: 'checkout'," +
                "        paymentRequest: {" +
                "            amount: '" + amount + "'," +
                "            intent: 'sale'," +
                "            context: '" + context + "'," +
                "            context_reference: '" + context_reference + "'" +
                "        }," +
                "        createRequest: function(request) {" +
                "            $.ajax({" +
                "                url: '" + UrlUtils.DOMAIN + "pay/bkash_payment/create'," +
                "                type: 'POST'," +
                "                contentType: 'application/json'," +
                "                headers: {" +
                "                    \"Authorization\": \"Bearer " + userDetails.getToken() + "\"," +
                "                }," +
                "                data: JSON.stringify(request)," +
                "                success: function(data) {" +
                "                    if (data && data.paymentID != null) {" +
                "                        paymentID = data.paymentID;" +
                "                        bKash.create().onSuccess(data);" +
                "                    } else {" +
                "                        bKash.create().onError();" +
                "                    }" +
                "                }," +
                "                error: function() {" +
                "                    bKash.create().onError();" +
                "                }" +
                "            })" +
                "        }," +
                "        executeRequestOnAuthorization: function() {" +
                "            $.ajax({" +
                "                url: '" + UrlUtils.DOMAIN + "pay/bkash_payment/execute'," +
                "                type: 'POST'," +
                "                contentType: 'application/json'," +
                "                headers: {" +
                "                    \"Authorization\": \"Bearer " + CredentialManager.getToken() + "\"," +
                "                }," +
                "                data: JSON.stringify({" +
                "                    \"paymentID\": paymentID" +
                "                })," +
                "                success: function(data) {" +
                "                    if (data && data.paymentID != null) {" +
                "                        window.location.href =" +
                "                            \"success.html\";" +
                "                    } else {" +
                "                        bKash.execute().onError();" +
                "                        errorMessage(data.data);" +
                "                    }" +
                "                }," +
                "                error: function(err) {" +
                "                    bKash.execute().onError();" +
                "                    errorMessage(err.responseJSON);" +
                "                }" +
                "            });" +
                "        }" +
                "    });" +
                "    window.onerror = function(msg) {" +
                "        var string = msg.toLowerCase();" +
                "        var substring = \"script error\";" +
                "        if (string.indexOf(substring) > -1) {" +
                "            if (paymentID) {" +
                "                bKash.reconfigure({" +
                "                    paymentRequest: {" +
                "                        amount: '" + amount + "'," +
                "                        intent: \"sale\"," +
                "                        context: \"" + context + "\"," +
                "                        context_reference: '" + context_reference + "'" +
                "                    }" +
                "                });" +
                "            } else {" +
                "                bKash.reconfigure({" +
                "                    paymentRequest: {" +
                "                        amount: '" + amount + "'," +
                "                        intent: \"sale\"," +
                "                        context: \"" + context + "\"," +
                "                        context_reference: '" + context_reference + "'" +
                "                    }" +
                "                });" +
                "            }" +
                "        }" +
                "        return false;" +
                "    };" +
                "}";

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

                if (url.contains("success.html")) {

                    Toast.makeText(PayViaBkashActivity.this, "Payment successful! If your order's payment status doesn't get updated within 5 minutes, please contact support.", Toast.LENGTH_LONG).show();

                    setResult(Activity.RESULT_OK);
                    finish();

                    return;

                }


                loadingFinished = false;

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                webView.evaluateJavascript(javascript, s -> {
                    Log.d("LogName", s); // Prints: {"var1":"variable1","var2":"variable2"}
                });


                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && isShowed) {
                    isShowed = false;

                } else {
                    redirect = false;
                }

                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        if (BuildConfig.DEBUG)
            webView.loadUrl("https://beta.evaly.com.bd/bkash_payment?invoice_no=" + context_reference + "&amount=" + amount + "&token=" + userDetails.getToken());
        else
            webView.loadUrl("https://evaly.com.bd/bkash_payment?invoice_no=" + context_reference + "&amount=" + amount + "&token=" + userDetails.getToken());

//            webView.loadUrl("file:///android_asset/bkash_dev.html");

//            webView.loadUrl("file:///android_asset/bkash.html");
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


    // old code


//
//    ImageView tutorial = findViewById(R.id.tutorial);
//
//
//        Glide.with(this)
//                .asBitmap()
//                .load("https://evaly.com.bd/assets/images/bkash_payment.png")
//                .skipMemoryCache(false)
//                .into(tutorial);
//
//    TextView copy = findViewById(R.id.copy);
//
//        copy.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            try{
//
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("bKash", "01704169596");
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(PayViaBkashActivity.this, "bKash number copied.", Toast.LENGTH_SHORT).show();
//
//            } catch (Exception e){
//
//                Toast.makeText(PayViaBkashActivity.this, "Can't copy bKash number.", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    });
//
//
//    Button contact = findViewById(R.id.contact);
//
//        contact.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            Intent intent = new Intent(PayViaBkashActivity.this, ContactActivity.class);
//            startActivity(intent);
//
//        }
//    });


}
