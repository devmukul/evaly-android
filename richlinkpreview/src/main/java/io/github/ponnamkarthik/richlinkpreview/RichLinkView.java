package io.github.ponnamkarthik.richlinkpreview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class RichLinkView extends RelativeLayout {

    private View view;
    Context context;
    private MetaData meta;
    private String price;

    LinearLayout linearLayout;
    ImageView imageView;
    TextView textViewTitle;
    TextView textViewDesp;
    TextView textViewUrl;

    int color = getResources().getColor(R.color.md_white_1000);

    private String main_url;

    private boolean isDefaultClick = true;

    private RichLinkListener richLinkListener;


    public RichLinkView(Context context) {
        super(context);
        this.context = context;
    }

    public RichLinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RichLinkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RichLinkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void initView() {
        if(findLinearLayoutChild() != null) {
            this.view = findLinearLayoutChild();
        } else  {
            this.view = this;
            inflate(context, R.layout.link_layout,this);
        }

        linearLayout = (LinearLayout) findViewById(R.id.rich_link_card);
        imageView = (ImageView) findViewById(R.id.rich_link_image);
        textViewTitle = (TextView) findViewById(R.id.rich_link_title);
        textViewDesp = (TextView) findViewById(R.id.rich_link_desp);
        textViewUrl = (TextView) findViewById(R.id.rich_link_url);

        linearLayout.setBackgroundColor(color);

        if(meta.getImageurl().equals("") || meta.getImageurl().isEmpty()) {
            imageView.setVisibility(GONE);
        } else {
            imageView.setVisibility(VISIBLE);
            Picasso.get()
                    .load(meta.getImageurl())
                    .into(imageView);
        }

        if(meta.getTitle().isEmpty() || meta.getTitle().equals("")) {
            textViewTitle.setVisibility(GONE);
        } else {
            textViewTitle.setVisibility(VISIBLE);
            textViewTitle.setText(meta.getTitle());
        }
        if(meta.getUrl().isEmpty() || meta.getUrl().equals("")) {
            textViewUrl.setVisibility(GONE);
        } else {
            textViewUrl.setVisibility(VISIBLE);
            textViewUrl.setText(meta.getUrl());
        }
        if (price != null){
            textViewDesp.setVisibility(VISIBLE);
            textViewDesp.setText(price+" BDT");
        }else {
            textViewDesp.setVisibility(GONE);
        }
//        if(meta.getDescription().isEmpty() || meta.getDescription().equals("")) {
//            textViewDesp.setVisibility(GONE);
//        } else {
//            textViewDesp.setVisibility(VISIBLE);
//            textViewDesp.setText(meta.getDescription());
//        }


//        linearLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isDefaultClick) {
//                    richLinkClicked();
//                } else {
//                    if(richLinkListener != null) {
//                        richLinkListener.onClicked(view, meta);
//                    } else {
//                        richLinkClicked();
//                    }
//                }
//            }
//        });

    }


    private void richLinkClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(main_url));
        context.startActivity(intent);
    }

    public void setBackground(int c) {
        color = c;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public void setDefaultClickListener(boolean isDefault) {
        isDefaultClick = isDefault;
    }

    public void setClickListener(RichLinkListener richLinkListener1) {
        richLinkListener = richLinkListener1;
    }

    protected LinearLayout findLinearLayoutChild() {
        if (getChildCount() > 0 && getChildAt(0) instanceof LinearLayout) {
            return (LinearLayout) getChildAt(0);
        }
        return null;
    }

    public void setLinkFromMeta(MetaData metaData) {
        meta = metaData;
        initView();
    }

    public MetaData getMetaData() {
        return meta;
    }

    public void setLink(String url, final ViewListener viewListener) {
        main_url = url;
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                meta = metaData;
                if(!meta.getTitle().isEmpty() || !meta.getTitle().equals("")) {
                    viewListener.onSuccess(true);
                }

                initView();
            }

            @Override
            public void onError(Exception e) {
                viewListener.onError(e);
            }
        });
        richPreview.getPreview(url);
    }

}
