package bd.com.evaly.evalyshop.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;

public class PayViaBkashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_via_bkash);
        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay Via bKash Payment");


        ImageView tutorial = findViewById(R.id.tutorial);


        Glide.with(this)
                .asBitmap()
                .load("https://evaly.com.bd/assets/images/bkash_payment.png")
                .skipMemoryCache(false)
                .into(tutorial);

        TextView copy = findViewById(R.id.copy);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        try{

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("bKash", "01704169596");
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(PayViaBkashActivity.this, "bKash number copied.", Toast.LENGTH_SHORT).show();

                        } catch (Exception e){

                            Toast.makeText(PayViaBkashActivity.this, "Can't copy bKash number.", Toast.LENGTH_SHORT).show();

                        }
            }
        });


        Button contact = findViewById(R.id.contact);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PayViaBkashActivity.this, ContactActivity.class);
                startActivity(intent);

            }
        });

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
