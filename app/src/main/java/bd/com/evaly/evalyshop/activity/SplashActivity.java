package bd.com.evaly.evalyshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int versionCode = BuildConfig.VERSION_CODE;

        AuthApiHelper.checkUpdate(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201){
                    String version = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("version").getAsString();
                    boolean isForce = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("force").getAsBoolean();
                    int v = Integer.parseInt(version);
                    Logger.d(v+"      "+isForce+"     "+versionCode);

                    if (versionCode != v && isForce){
                        update();
                    }else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int status) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void update() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("New update available!");
        builder.setMessage("Please update your app");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

}
