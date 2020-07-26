package bd.evaly.evalypaymentlibrary.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;


public final class Utils {

    public static HashMap<String, String> splitQuery(URL url) {
        HashMap<String, String> query_pairs = new HashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return query_pairs;
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
}
