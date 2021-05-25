package bd.com.evaly.evalyshop.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPointGraphBinding;
import bd.com.evaly.evalyshop.models.image.Background;
import bd.com.evaly.evalyshop.models.image.CloudFrontRequest;
import bd.com.evaly.evalyshop.models.image.Edits;
import bd.com.evaly.evalyshop.models.image.Jpeg;
import bd.com.evaly.evalyshop.models.image.Resize;

public class BindingUtils {

    public static void bindPointsView(ItemPointGraphBinding binding, int score) {
        binding.slider.setValue((float) getProgressValue(score));
        binding.points.setText(score + " Pts");
        if (score <= 1000) {
            setSliderColor(binding, "BRONZE USER", "#B18608");
        } else if (score <= 3000)
            setSliderColor(binding, "SILVER USER", "#69C97A");
        else if (score <= 5000)
            setSliderColor(binding, "GOLD USER", "#DDB635");
        else if (score < 10000)
            setSliderColor(binding, "DIAMOND USER", "#915DB1");
        else
            setSliderColor(binding, "PLATINUM USER", "#D6833B");
    }

    public static void setSliderColor(ItemPointGraphBinding binding, String name, String color) {
        binding.levelName.setText(name);
        binding.slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(color)));
        binding.slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    public static double getProgressValue(int value) {

        if (value >= 10000)
            return 10000;

        double sum = 0;

        for (int i = 1; i <= 4; i++) {

            double weight = 2.5;

            if (i == 2 || i == 3)
                weight = 1.25;
            else if (i == 4)
                weight = 0.5;

            double toDeduct = 2500 / weight;

            if (value > toDeduct) {
                value -= toDeduct;
                sum += toDeduct * weight;
            } else {
                sum += value * weight;
                break;
            }
        }

        return sum;
    }

    public static void markImageVariation(RelativeLayout holder, boolean selected) {
        if (selected) {
            holder.setBackground(holder.getContext().getDrawable(R.drawable.variation_brd_selected));
        } else {
            holder.setBackground(holder.getContext().getDrawable(R.drawable.variation_brd));
        }
    }

    public static void markVariation(RelativeLayout holder, boolean selected) {
        if (selected) {
            holder.setBackground(holder.getContext().getDrawable(R.drawable.bg_variation_size_selected));
        } else {
            holder.setBackground(holder.getContext().getDrawable(R.drawable.bg_variation_size_default));
        }
    }

    public static void setImage(ImageView view, String url, int placeHolder, int errorImage) {
        if (url == null)
            return;
        url = url.replace("\n\r", "");
        if (!Utils.isValidURL(url)) return;

        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .error(errorImage)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeHolder)
                .into(view);
    }

    public static void setImage(ImageView view, String url, int placeHolder, int errorImage, int width, int height, boolean whiteBg) {

        if (view.getContext() == null)
            return;

        if (url == null)
            return;
        url = url.replace("\n\r", "");
        if (!Utils.isValidURL(url)) return;

        if (url.contains("/media.evaly.com.bd/"))
            Glide.with(view.getContext())
                    .asBitmap()
                    .load(generateResizeUrl(url, width, height, whiteBg))
                    .error(errorImage)
                    .placeholder(ContextCompat.getDrawable(view.getContext(), placeHolder))
                    .into(view);
        else
            Glide.with(view.getContext())
                    .asBitmap()
                    .load(url)
                    .error(errorImage)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(ContextCompat.getDrawable(view.getContext(), placeHolder))
                    .into(view);
    }


    public static String generateResizeUrl(String url, int width, int height, boolean whiteBg) {

        String[] split = url.split("/media.evaly.com.bd/");

        if (split.length < 2)
            return null;

        CloudFrontRequest requestBody = new CloudFrontRequest();
        requestBody.setBucket("media.evaly.com.bd");
        requestBody.setKey(split[1]);

        // edits
        Edits edits = new Edits();
        edits.setFlatten(true);

        // resize
        Resize resize = new Resize();
        if (whiteBg)
            resize.setFit("contain");
        else
            resize.setFit("cover");
        resize.setWidth(width);
        resize.setHeight(height);
        edits.setResize(resize);

        // background
        Background background = new Background();
        background.setAlpha(1);
        background.setR(255);
        background.setG(255);
        background.setB(255);
        edits.setBackground(background);

        // jpeg
        Jpeg jpeg = new Jpeg();
        jpeg.setQuality(80);
        edits.setJpeg(jpeg);

        requestBody.setEdits(edits);

        byte[] data = new byte[0];
        try {
            data = new Gson().toJson(requestBody).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        return "https://df17fp68uwcso.cloudfront.net/" + base64;
    }
}
