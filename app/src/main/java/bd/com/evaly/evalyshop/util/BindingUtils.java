package bd.com.evaly.evalyshop.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPointGraphBinding;
import bd.com.evaly.evalyshop.models.image.Background;
import bd.com.evaly.evalyshop.models.image.CloudFrontRequest;
import bd.com.evaly.evalyshop.models.image.Edits;
import bd.com.evaly.evalyshop.models.image.Jpeg;
import bd.com.evaly.evalyshop.models.image.Resize;
import bd.com.evaly.evalyshop.models.points.PointsResponse;

public class BindingUtils {

    public static void bindPointsView(ItemPointGraphBinding binding, PointsResponse model, boolean isProfile) {

        if (model == null) {
            binding.slider.setValue(0);
            binding.points.setText("0 Pts");
            if (isProfile)
                binding.message.setVisibility(View.VISIBLE);
            binding.badge.setImageResource(R.drawable.ic_level_bronze);
            setSliderColor(binding, "<b>BRONZE</b> USER", "#B18608");
            return;
        }

        int score = model.getPoints();
        binding.slider.setValueTo(model.getInterval().get(model.getInterval().size() - 1));
        binding.slider.setValue((float) getProgressValue(score, model.getInterval()));
        binding.points.setText(Utils.formatEvalyPoints(score));
        binding.message.setVisibility(View.GONE);

        if (score < model.getInterval().get(1)) {
            if (isProfile)
                binding.message.setVisibility(View.VISIBLE);
            binding.badge.setImageResource(R.drawable.ic_level_bronze);
            setSliderColor(binding, "<b>BRONZE</b> USER", "#B18608");
        } else if (score < model.getInterval().get(2)) {
            if (isProfile)
                binding.message.setVisibility(View.VISIBLE);
            binding.badge.setImageResource(R.drawable.ic_level_silver);
            setSliderColor(binding, "<b>SILVER</b> USER", "#69C97A");
        } else if (score < model.getInterval().get(3)) {
            binding.badge.setImageResource(R.drawable.ic_level_gold);
            setSliderColor(binding, "<b>GOLD</b> USER", "#DDB635");
        } else if (score < model.getInterval().get(4)) {
            binding.badge.setImageResource(R.drawable.ic_level_diamond);
            setSliderColor(binding, "<b>DIAMOND</b> USER", "#915DB1");
        } else {
            binding.badge.setImageResource(R.drawable.ic_level_platinum);
            setSliderColor(binding, "<b>PLATINUM</b> USER", "#D6833B");
        }
    }

    public static void setSliderColor(ItemPointGraphBinding binding, String name, String color) {
        binding.levelName.setText(Html.fromHtml(name));
        binding.badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        binding.slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(color)));
        binding.slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    public static double getProgressValue(int value, List<Integer> interval) {

        int maxPoints = interval.get(interval.size() - 1);
        if (value >= maxPoints)
            return maxPoints;

        int intervalValue = maxPoints / (interval.size() - 1);

        double sum = 0;

        for (int i = 1; i <= interval.size(); i++) {

            double toDeduct = interval.get(i) - interval.get(i - 1);
            double weight = intervalValue / toDeduct;

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
