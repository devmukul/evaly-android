package bd.com.evaly.evalyshop.ui.payment.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import java.net.MalformedURLException;
import java.net.URL;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.payment.builder.PaymentWebBuilder;
import im.delight.android.webview.AdvancedWebView;

public final class PaymentWebActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    private ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        mWebView = findViewById(R.id.webview);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });

        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.setListener(this, this);
        mWebView.loadUrl(PaymentWebBuilder.getRequestURL());

//            if (PaymentWebBuilder.getPurchaseInformation() != null && PaymentWebBuilder.getPurchaseInformation().getGateway() != null && !PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl")) {
//                String postData = "?token=" + URLEncoder.encode(PaymentWebBuilder.getPurchaseInformation().getAuthToken(), "UTF-8")
//                        + "&amount=" + URLEncoder.encode(String.valueOf(PaymentWebBuilder.getPurchaseInformation().getAmount()), "UTF-8")
//                        + "&invoice_no=" + URLEncoder.encode(PaymentWebBuilder.getPurchaseInformation().getInvoiceNo(), "UTF-8");
//                mWebView.loadUrl(PaymentWebBuilder.getRequestURL().concat(postData));
//               // Log.e("]]]]", PaymentWebBuilder.getRequestURL().concat(postData));
//            } else {
//                mWebView.loadUrl(PaymentWebBuilder.getRequestURL());
//            }

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (BuildConfig.DEBUG) {
            Logger.d(url);
            Log.e("hmtz", url);
        }
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
//         if (url.contains(PaymentWebBuilder.getSuccessUrl()) && !(PaymentWebBuilder.getPurchaseInformation() != null && PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl"))) {
//            if (PaymentWebBuilder.getUpayListener() != null) {
//                try {
//                    URL successURL = new URL(url);
//                    PaymentWebBuilder.getUpayListener().onPaymentSuccess("success");
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }
//            mWebView.destroy();
//            finish();
//        }else if (url.toLowerCase().contains(PaymentWebBuilder.getSuccessUrl()) && PaymentWebBuilder.getPurchaseInformation().getGateway().equalsIgnoreCase("sebl")){
//             if (PaymentWebBuilder.getUpayListener() != null) {
//                 try {
//                     URL successURL = new URL(url);
//                     PaymentWebBuilder.getUpayListener().onPaymentSuccess("success");
//                 } catch (MalformedURLException e) {
//                     e.printStackTrace();
//                 }
//             }
//             mWebView.destroy();
//             finish();
//         }
        Logger.e(url);
        if (url.toLowerCase().contains(PaymentWebBuilder.getSuccessUrl())) {
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
        } else if (url.toLowerCase().equals(BuildConfig.WEB_URL)) {
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
        progressBar.setProgress(100);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        progressBar.setProgress(100);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
