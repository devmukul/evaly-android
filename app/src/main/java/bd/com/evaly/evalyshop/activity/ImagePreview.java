package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import bd.com.evaly.evalyshop.R;

public class ImagePreview extends AppCompatActivity {
    ImageView imageViewPreview;
    String imageURL="";

    String userAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        userAgent = new WebView(this).getSettings().getUserAgentString();


        imageViewPreview = findViewById(R.id.image);

        ImageView back = findViewById(R.id.back);

        back.bringToFront();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Intent intent = getIntent();

        if (intent.getStringExtra("image") != null){

            imageURL = intent.getStringExtra("image");

        } else {
            return;
        }

        Glide.with(this)
                .load(imageURL)
                .placeholder(R.drawable.ic_image_placeholder)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageViewPreview.setImageDrawable(resource);
                        return true;
                    }}
                )
                .into(imageViewPreview);



    }





}
