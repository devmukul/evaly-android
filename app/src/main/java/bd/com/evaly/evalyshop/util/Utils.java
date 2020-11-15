package bd.com.evaly.evalyshop.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;

public class Utils {

    public static final int REQUEST_CODE_CAMERA = 300;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static HashMap<String, String> objectToHashMap(Object body) {
        Gson gson = new Gson();
        Type stringStringMap = new TypeToken<HashMap<String, String>>() {
        }.getType();
        return gson.fromJson(gson.toJson(body), stringStringMap);
    }

    public static boolean isValidURL(String urlStr) {
        return Patterns.WEB_URL.matcher(urlStr).matches();
    }

    public static boolean isValidColor(String str) {
        String regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern p = Pattern.compile(regex);
        if (str == null) {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static int countWords(String s) {

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

    public static boolean canRefundRequest(String payment_status, String order_status, String payment_method) {

        if (payment_status.contains("refund_requested")) {
            return false;
        }
        if (order_status.contains("shipped") || order_status.contains("delivered")) {
            return false;
        }
        if (payment_status.contains("unpaid")) {
            return false;
        }
        if (payment_method.contains("bkash")) {
            return true;
        }
        if (payment_method.contains("card")) {
            return true;
        }
        if (payment_method.contains("bank")) {
            return true;
        }
        return false;
    }

    public static boolean canRefundToCard(String payment_method) {
        if (payment_method.contains("bkash")) {
            return false;
        }
        if (payment_method.contains("bank")) {
            return false;
        }
        if (payment_method.contains("card")) {
            return true;
        }
        return false;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static int daysBetween(Calendar day1, Calendar day2) {
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public static String formatPrice(double d) {
        if (d == (int) d)
            return String.format(Locale.ENGLISH, "%d", (int) d);
        else
            return String.format(Locale.ENGLISH, "%.2f", d);
    }

    public static String formatPrice(String s) {

        double d = Double.parseDouble(s);

        return formatPrice(d);
    }

    public static String formatPriceSymbol(String s) {
        if (s == null)
            return "৳ 0";
        return "৳ " + formatPrice(s);
    }


    public static String formatPriceSymbol(double s) {
        return "৳ " + formatPrice(s);
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int convertDpToPixel(int dp) {
        return (int) (dp * ((float) AppController.getmContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }

    public static boolean isPackageExisted(String targetPackage, Context context) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    public static boolean isValidNumber(String text) {

        return text.matches("^(01)[3-9][0-9]{8}$");
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String getConvertedTime(String time) {
        String dateTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6"));
        Date date = new Date();
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 6);

        dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm aaa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6"));
        dateTime = dateFormat.format(calendar.getTime());


        return dateTime;
    }

    public static String isStrongPassword(String password) {

        if (password.length() < 8) {
            return "Passwords must be at least 8 characters in length";
        } else {
            char c;
            int count = 0;
            int numCount = 0;
            for (int i = 0; i < password.length(); i++) {
                c = password.charAt(i);
                if (!Character.isLetterOrDigit(c))
                    count++;
                else if (Character.isDigit(c))
                    numCount++;

            }
            if (count < 1)
                return "Passwords must have at least one special character like @ ! # $ % ^ & * () {} etc";
            else if (numCount < 1)
                return "Passwords must have at least one digit(0-9)";
        }
        return "yes";
    }

    public static String getStatusMode(int status) {
        String mPresenceMode = "";
        switch (status) {
            case Constants.PRESENCE_MODE_AVAILABLE_INT:
                mPresenceMode = Constants.PRESENCE_MODE_AVAILABLE;
                break;
            case Constants.PRESENCE_MODE_AWAY_INT:
                mPresenceMode = Constants.PRESENCE_MODE_AWAY;
                break;
            case Constants.PRESENCE_MODE_DND_INT:
                mPresenceMode = Constants.PRESENCE_MODE_DND;
                break;
            case Constants.PRESENCE_MODE_OFFLINE_INT:
                mPresenceMode = Constants.PRESENCE_MODE_XA;
                break;
            default:
                mPresenceMode = Constants.PRESENCE_MODE_XA;
                break;
        }

        return mPresenceMode;
    }

    public static String getTimeAgoOnline(long time) {

        // TODO: localize
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }


    public static String truncateText(String text, int maxLength, String endWith) {
        if (text == null)
            return "";

        if (text.length() > maxLength) {
            BreakIterator bi = BreakIterator.getWordInstance();
            bi.setText(text);

            if (bi.isBoundary(maxLength - 1)) {
                return text.substring(0, maxLength - 2) + " " + endWith;
            } else {
                int preceding = bi.preceding(maxLength - 1);
                return text.substring(0, preceding - 1) + " " + endWith;
            }
        } else {
            return text;
        }
    }


    public static String getDeviceID(Context context) {


        String android_id = "";

        try {

            android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        } catch (Exception e) {
        }

        if (android_id == null || android_id.equals("")) {
            android_id = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
        }


        //String val = String.valueOf(Calendar.getInstance().getTimeInMillis()/100986056);

        android_id = "evaly" + android_id + "evalyevaly" + android_id + "evaly";

        String s1 = encode(new String(android.util.Base64.encode(android_id.getBytes(), android.util.Base64.DEFAULT)));


        String str = reverseIt(s1);

        return encode(new String(android.util.Base64.encode(str.getBytes(), android.util.Base64.DEFAULT)));


    }


    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }


    public static String reverseIt(String source) {
        int i, len = source.length();
        StringBuilder dest = new StringBuilder(len);

        for (i = (len - 1); i >= 0; i--) {
            dest.append(source.charAt(i));
        }

        return dest.toString();
    }


    public static String extraToken(String number) {


        String t = "";

        try {

            int temp = Integer.parseInt(number);
            String e = number;
            if (e.length() > 4) {
                t = String.valueOf(8 * Integer.parseInt(e.substring(e.length() - 4)) - 2019);
            }
        } catch (Exception e) {
            t = String.valueOf(Math.round(9e4 * Math.random()) + 1e4);
        }
        String encoded = t;

        try {
            encoded = new String(android.util.Base64.encode(t.getBytes(), android.util.Base64.DEFAULT));
        } catch (Exception e) {

        }

        return encoded;

    }


    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }


    public static void CustomTab(String url, Context context) {

        try {
            Uri uri = Uri.parse(url);

            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

            // set desired toolbar colors

            intentBuilder.setShowTitle(true);
            intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            CustomTabsIntent customTabsIntent = intentBuilder.build();

            customTabsIntent.launchUrl(context, uri);
        } catch (Exception e) {
            Toast.makeText(context, "Install Google Chrome", Toast.LENGTH_SHORT).show();
        }
    }

    public static String toFirstCharUpperAll(String string) {

        try {

            StringBuffer sb = new StringBuffer(string);
            for (int i = 0; i < sb.length(); i++)
                if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                    sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
            return sb.toString();

        } catch (Exception e) {

            return string;

        }
    }

    public static String formattedDateFromString(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);

        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat);
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));


        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }


    public static Date formattedDateFromString(String inputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));


        try {
            parsed = df_input.parse(inputDate);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return parsed;

    }


    public static Date getCampaignDate(String inputDate) {

        String inputFormat;

        if (inputDate.contains("."))
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        else
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        Date parsed = null;

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        try {
            parsed = df_input.parse(inputDate);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return parsed;

    }


    public static String getFormatedCampaignDate(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            if (inputDate.contains("."))
                inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            else
                inputFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));


        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }


    public static long formattedDateFromStringTimestamp(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        long outputDate = 0;


        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        df_input.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));


        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = parsed.getTime();
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }


    public static long formattedDateFromStringToTimestampGMT(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        long outputDate = 0;

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));


        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = parsed.getTime();
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }

    public static long formattedDateFromStringToTimestampGMTIssue(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        long outputDate = 0;

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        //  df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));


        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = parsed.getTime();
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }


    public static String titleBeautify(String str) {

        str = str.replace("-", " ");
        return str;


    }


    private static double colorDistance(int a, int b) {
        return Math.sqrt(Math.pow(Color.red(a) - Color.red(b), 2) + Math.pow(Color.blue(a) - Color.blue(b), 2) + Math.pow(Color.green(a) - Color.green(b), 2));
    }


    public static Bitmap changeColor(Bitmap src, int fromColor, int targetColor) {

        try {


            if (src == null) {
                return null;
            }
            // Source image size
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixels = new int[width * height];
            //get pixels
            src.getPixels(pixels, 0, width, 0, 0, width, height);

            for (int x = 0; x < pixels.length; ++x) {

                if (colorDistance(pixels[x], fromColor) < 10)
                    pixels[x] = targetColor;

            }
            // create result bitmap output
            Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
            //set pixels
            result.setPixels(pixels, 0, width, 0, 0, width, height);


            return result;

        } catch (Exception e) {

            return src;

        }
    }


    public static String getTimeAgo(long time) {


        Date curDate = currentDate();
        long now = curDate.getTime();

        if (time > now || time <= 0) {
            return "a moment ago";
        }

        int timeDIM = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (timeDIM == 0) {
            timeAgo = "Just now";
        } else if (timeDIM == 1) {
            return "1 minute ago";
        } else if (timeDIM >= 2 && timeDIM <= 44) {
            timeAgo = timeDIM + " minutes ago";
        } else if (timeDIM >= 45 && timeDIM <= 89) {
            timeAgo = "1 hour ago";
        } else if (timeDIM >= 90 && timeDIM <= 1439) {
            timeAgo = (Math.round(timeDIM / 60)) + " hours ago";
        } else if (timeDIM >= 1440) {

            Date tdate = new Date(time);

            SimpleDateFormat jdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.getDefault());
            jdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));

            timeAgo = jdf.format(tdate);

        }

        return timeAgo;
    }


    public static String getTimeAgoSmall(long time) {

        Date curDate = currentDate();
        long now = curDate.getTime();

        if (time > now || time <= 0) {
            return "a moment ago";
        }

        int timeDIM = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (timeDIM == 0) {
            timeAgo = "Just now";
        } else if (timeDIM == 1) {
            return "1 mins ago";
        } else if (timeDIM >= 2 && timeDIM <= 44) {
            timeAgo = timeDIM + " mins ago";
        } else if (timeDIM >= 45 && timeDIM <= 89) {
            timeAgo = "1 hour ago";
        } else if (timeDIM >= 90 && timeDIM <= 1439) {
            timeAgo = (Math.round(timeDIM / 60)) + " hours ago";
        } else if (timeDIM >= 1440) {

            Date tdate = new Date(time);

            SimpleDateFormat jdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            jdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));

            timeAgo = jdf.format(tdate);

        }

        return timeAgo;
    }


    public static String getHumanTime(long timestamp) {


        Date tdate = new Date(timestamp * 1000);

        SimpleDateFormat jdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.getDefault());


        String timeAgo = jdf.format(tdate);


        return timeAgo;
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }


    public static String decode(String input) {

        HashMap<String, String> hm = new HashMap();

        hm.put("a", "b");
        hm.put("b", "c");
        hm.put("c", "d");
        hm.put("d", "e");
        hm.put("e", "f");
        hm.put("f", "g");
        hm.put("g", "h");
        hm.put("h", "i");
        hm.put("i", "j");
        hm.put("j", "k");
        hm.put("k", "l");
        hm.put("l", "m");
        hm.put("t", "+");
        hm.put("m", "n");
        hm.put("1", "4");
        hm.put("2", "3");
        hm.put("3", "2");
        hm.put("4", "1");
        hm.put("5", "0");
        hm.put("/", "#");
        hm.put(":", ")");
        hm.put("=", "(");
        hm.put("?", "%");
        hm.put(".", "\\");


        StringBuffer output = new StringBuffer();
        for (int i = 0; i < input.length(); i++) {
            String ch = input.charAt(i) + "";
            if (hm.containsValue(ch)) {
                for (Map.Entry<String, String> entry : hm.entrySet()) {
                    if (entry.getValue().equals(ch)) {
                        output.append(entry.getKey());
                    }
                }
            } else {
                output.append(ch);
            }
        }
        return output.toString();
    }

    public static String encode(String input) {

        HashMap<String, String> hm = new HashMap();

        hm.put("a", "b");
        hm.put("b", "c");
        hm.put("c", "d");
        hm.put("d", "e");
        hm.put("e", "f");
        hm.put("f", "g");
        hm.put("g", "h");
        hm.put("h", "i");
        hm.put("i", "j");
        hm.put("j", "k");
        hm.put("k", "l");
        hm.put("l", "m");
        hm.put("m", "n");
        hm.put("n", "o");
        hm.put("o", "p");
        hm.put("p", "q");
        hm.put("q", "r");
        hm.put("r", "s");
        hm.put("s", "t");
        hm.put("t", "u");
        hm.put("u", "v");
        hm.put("v", "w");
        hm.put("w", "x");
        hm.put("x", "y");
        hm.put("y", "z");
        hm.put("z", "a");


        hm.put("0", "8");
        hm.put("1", "5");
        hm.put("2", "7");
        hm.put("3", "2");
        hm.put("4", "0");
        hm.put("5", "1");
        hm.put("6", "3");
        hm.put("7", "9");
        hm.put("8", "4");
        hm.put("9", "6");

        hm.put(".", "#");
        hm.put("=", "|");


        StringBuffer output = new StringBuffer();
        for (int i = 0; i < input.length(); i++) {
            String ch = input.charAt(i) + "";
            if (hm.containsKey(ch)) {
                output.append(hm.get(ch));
            } else {
                output.append(ch);
            }
        }
        return output.toString();
    }

    public static String urlEncode(String ul) {

        ul = "https://" + ul;

        String key = "i++ps)##suh\\ebrbz\\don\\ce#suh%brfb(mbzbeb_ce_wfc&doef(u+g-8&q(";

        return decode(key);

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static int getRandomColor(Context context) {
        int num = new Random().nextInt(10);
        List<Integer> colors = new ArrayList<>();
        colors.add(R.color.color1);
        colors.add(R.color.color2);
        colors.add(R.color.color3);
        colors.add(R.color.color4);
        colors.add(R.color.color5);
        colors.add(R.color.color6);
        colors.add(R.color.color7);
        colors.add(R.color.color8);
        colors.add(R.color.color9);
        colors.add(R.color.color10);
        return colors.get(num);
    }


}
