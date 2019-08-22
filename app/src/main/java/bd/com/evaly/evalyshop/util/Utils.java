package bd.com.evaly.evalyshop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import bd.com.evaly.evalyshop.R;

public class Utils {


    public static String getDeviceID(Context context){


        String android_id = "";

        try {

            android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        } catch (Exception e){}

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

        android_id = "evaly"+android_id+"evalyevaly"+android_id+"evaly";

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
    public  static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
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

        for (i = (len - 1); i >= 0; i--){
            dest.append(source.charAt(i));
        }

        return dest.toString();
    }

    public static String extraToken(String number){


        String t = "";

        try {

            int temp = Integer.parseInt(number);
            String e = number;
            if (e.length() > 4) {
                t = String.valueOf(8 * Integer.parseInt(e.substring(e.length() - 4)) - 2019);
            }

            }catch(Exception e){

                t = String.valueOf(Math.floor(9e4 * Math.random()) + 1e4);
            }


            String encoded = t;

        try {

            encoded = new String(android.util.Base64.encode(t.getBytes(), android.util.Base64.DEFAULT));
        } catch (Exception e){

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






//
//    public static void CustomTab(String url, Context context)
//    {
//        Uri uri = Uri.parse(url);
//
//        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
//
//        // set desired toolbar colors
//
//        intentBuilder.setShowTitle(true);
//        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
//        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//
//        // add start and exit animations if you want(optional)
//    /*intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//    intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
//            android.R.anim.slide_out_right);*/
//
//        CustomTabsIntent customTabsIntent = intentBuilder.build();
//
//        customTabsIntent.launchUrl(context, uri);
//    }

    public static String toFirstCharUpperAll(String string){

        try {

            StringBuffer sb = new StringBuffer(string);
            for (int i = 0; i < sb.length(); i++)
                if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                    sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
            return sb.toString();

        } catch (Exception e){

            return string;

        }
    }

    public static String formattedDateFromString(String inputFormat, String outputFormat, String inputDate){
        if(inputFormat.equals("")){ // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        if(outputFormat.equals("")){
            outputFormat = "EEEE d',' MMMM  yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));

        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
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


    public static String titleBeautify(String str){

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

        } catch (Exception e){

            return src;

        }
    }




    public static String getTimeAgo(long timestamp) {


        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(timestamp));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
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

            Date tdate = new Date(timestamp);

            SimpleDateFormat jdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.ENGLISH);
            jdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));

            timeAgo = jdf.format(tdate);

        }

        return timeAgo;
    }


    public static String getHumanTime(long timestamp) {



        Date tdate = new Date(timestamp * 1000);

        SimpleDateFormat jdf = new SimpleDateFormat("MMM dd 'at' h:mm a", Locale.ENGLISH);


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
        for(int i = 0; i < input.length(); i++)
        {
            String ch = input.charAt(i)+"";
            if(hm.containsValue(ch))
            {
                for (Map.Entry<String, String> entry : hm.entrySet()) {
                    if (entry.getValue().equals(ch)) {
                        output.append(entry.getKey());
                    }
                }
            }
            else
            {
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


        hm.put("0","8");
        hm.put("1","5");
        hm.put("2","7");
        hm.put("3","2");
        hm.put("4","0");
        hm.put("5","1");
        hm.put("6","3");
        hm.put("7","9");
        hm.put("8","4");
        hm.put("9","6");

        hm.put(".","#");
        hm.put("=","|");



        StringBuffer output = new StringBuffer();
        for(int i = 0; i < input.length(); i++)
        {
            String ch = input.charAt(i)+"";
            if(hm.containsKey(ch))
            {
                output.append(hm.get(ch));
            }
            else
            {
                output.append(ch);
            }
        }
        return output.toString();
    }

    public static String urlEncode(String ul){

        ul = "https://"+ul;

        String key = "i++ps)##suh\\ebrbz\\don\\ce#suh%brfb(mbzbeb_ce_wfc&doef(u+g-8&q(";

        return decode(key);

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}
