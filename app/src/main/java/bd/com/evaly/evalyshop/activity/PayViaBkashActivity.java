package bd.com.evaly.evalyshop.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.UserDetails;

public class PayViaBkashActivity extends BaseActivity {

    WebView webView;
    ProgressDialog prDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bkash_payment);
        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("bKash Payment");


        webView = findViewById(R.id.webView);

        UserDetails userDetails = new UserDetails(this);

        String url = "https://evaly.com.bd/payment?invoice_no=EVL348604781&amount=779";


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
                        "$(document).ready(function(){\n" +
                        "   $(\"#bKash_button\").trigger('click'); \n" +
                        "});" +

                "            clearInterval(nTimer);" +
                "        }" +
                "    }, 500);" +
                "})();" +
                "function initBkash() {" +
                "    var paymentID = '';" +
                "    bKash.init({" +
                "        paymentMode: 'checkout'," +
                "        paymentRequest: {" +
                "            amount: '10.00'," +
                "            intent: 'sale'," +
                "            context: 'order_payment'," +
                "            context_reference: 'EVL348604781'" +
                "        }," +
                "        createRequest: function(request) {" +
                "            $.ajax({" +
                "                url: 'https://api.evaly.com.bd/pay/bkash_payment/create'," +
                "                type: 'POST'," +
                "                contentType: 'application/json'," +
                "                headers: {" +
                "                    \"Authorization\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo2NjY2NywidXNlcm5hbWUiOiIwMTc1MTk3NzA0NSIsImV4cCI6MTU2Nzk0OTA0NCwiZW1haWwiOiJobXRhbWltbTdAZ21haWwuY29tIiwic2hvcHMiOltdLCJkZXZpY2UiOiJ3ZWIiLCJpc19zdXBlcnVzZXIiOmZhbHNlLCJpc19zdGFmZiI6ZmFsc2UsImdyb3VwcyI6W10sImlzX3ZlcmlmaWVkIjpmYWxzZX0.GdRwijkOxxZ3R0cqApHyLrQminianXhkuwbobMu3ZHc\"," +
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
                "                url: 'https://api.evaly.com.bd/pay/bkash_payment/execute'," +
                "                type: 'POST'," +
                "                contentType: 'application/json'," +
                "                headers: {" +
                "                    \"Authorization\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo2NjY2NywidXNlcm5hbWUiOiIwMTc1MTk3NzA0NSIsImV4cCI6MTU2Nzk0OTA0NCwiZW1haWwiOiJobXRhbWltbTdAZ21haWwuY29tIiwic2hvcHMiOltdLCJkZXZpY2UiOiJ3ZWIiLCJpc19zdXBlcnVzZXIiOmZhbHNlLCJpc19zdGFmZiI6ZmFsc2UsImdyb3VwcyI6W10sImlzX3ZlcmlmaWVkIjpmYWxzZX0.GdRwijkOxxZ3R0cqApHyLrQminianXhkuwbobMu3ZHc\"," +
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
                "                    }" +
                "                }," +
                "                error: function() {" +
                "                    bKash.execute().onError();" +
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
                "                        amount: '100.50'," +
                "                        intent: \"sale\"," +
                "                        context: \"order_payment\"," +
                "                        context_reference: 'EVL348604781'" +
                "                    }" +
                "                });" +
                "            } else {" +
                "                bKash.reconfigure({" +
                "                    paymentRequest: {" +
                "                        amount: '10.0'," +
                "                        intent: \"sale\"," +
                "                        context: \"order_payment\"," +
                "                        context_reference: 'EVL348604781'" +
                "                    }" +
                "                });" +
                "            }" +
                "        }" +
                "        return false;" +
                "    };" +
                "}";


        Log.d("js", javascript);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                webView.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("LogName", s); // Prints: {"var1":"variable1","var2":"variable2"}
                    }
                });





            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });





        webView.loadUrl("file:///android_asset/bkash.html");





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
