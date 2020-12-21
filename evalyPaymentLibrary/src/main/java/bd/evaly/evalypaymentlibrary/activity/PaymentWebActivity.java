package bd.evaly.evalypaymentlibrary.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import bd.evaly.evalypaymentlibrary.BuildConfig;
import bd.evaly.evalypaymentlibrary.R;
import bd.evaly.evalypaymentlibrary.builder.PaymentWebBuilder;
import im.delight.android.webview.AdvancedWebView;

public final class PaymentWebActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web);
        setupToolbar();
        openWebView();
    }

    private void setupToolbar() {
        if (getSupportActionBar() == null)
            return;
        getSupportActionBar().setElevation(4f);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(PaymentWebBuilder.getToolbarTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void openWebView() {
        mWebView = findViewById(R.id.webview);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.setListener(this, this);
        try {
            if (PaymentWebBuilder.getPurchaseInformation() != null && PaymentWebBuilder.getPurchaseInformation().getGateway() != null && !PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl")) {
                String postData = "?token=" + URLEncoder.encode(PaymentWebBuilder.getPurchaseInformation().getAuthToken(), "UTF-8")
                        + "&amount=" + URLEncoder.encode(String.valueOf(PaymentWebBuilder.getPurchaseInformation().getAmount()), "UTF-8")
                        + "&invoice_no=" + URLEncoder.encode(PaymentWebBuilder.getPurchaseInformation().getInvoiceNo(), "UTF-8");
                mWebView.loadUrl(PaymentWebBuilder.getRequestURL().concat(postData));
                Log.e("]]]]", PaymentWebBuilder.getRequestURL().concat(postData));
            } else {
                mWebView.loadUrl(PaymentWebBuilder.getRequestURL());
                Log.e("}}}}}", PaymentWebBuilder.getRequestURL());

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.e("+++", url);
         if (url.contains(PaymentWebBuilder.getSuccessUrl()) && !PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl")) {
            if (PaymentWebBuilder.getUpayListener() != null) {
                try {
                    URL successURL = new URL(url);
                    PaymentWebBuilder.getUpayListener().onPaymentSuccess("success");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            mWebView.destroy();
            finish();
        }else if (url.equalsIgnoreCase(PaymentWebBuilder.getSuccessUrl()) && PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl")){
             if (PaymentWebBuilder.getUpayListener() != null) {
                 try {
                     URL successURL = new URL(url);
                     PaymentWebBuilder.getUpayListener().onPaymentSuccess("success");
                 } catch (MalformedURLException e) {
                     e.printStackTrace();
                 }
             }
             mWebView.destroy();
             finish();
         }
    }

    @Override
    public void onBackPressed() {
        mWebView.destroy();
        finish();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPageFinished(String url) {

    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
