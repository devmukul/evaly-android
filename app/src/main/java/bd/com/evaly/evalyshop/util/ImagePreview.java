package bd.com.evaly.evalyshop.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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


        imageViewPreview = findViewById(R.id.image);

        ImageView back = findViewById(R.id.back);

        back.bringToFront();

        back.setOnClickListener(v -> finish());

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
