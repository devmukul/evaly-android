package bd.com.evaly.evalyshop.util;


import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JsonCacheHelper {

    private Context context;
    private String key;
    private String cacheDir;


    /* use demo



        String slag = "mobile-phone";


        acCache = new CacheHelper(context, slag);


    */
    public JsonCacheHelper(Context context,String key){

        this.context = context;
        this.key = key;
        this.cacheDir = context.getFilesDir().getPath();

    }





    public void save(String value) {

        try {

            key = URLEncoder.encode(key, "UTF-8");

            File cache = new File(cacheDir + "/" + key + ".srl");

            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(cache));
            out.writeUTF(value);
            out.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }



    public boolean isExists(){

        File cache = new File(cacheDir + "/" + key + ".srl");

        if (cache.exists())
            return true;
        else
            return false;

    }

    public void clear(){


        File cache = new File(cacheDir + "/" + key + ".srl");

        cache.delete();


    }

    public int getTimeDiffMin(){

        File cache = new File(cacheDir + "/" + key + ".srl");

        if (cache.exists()) {

            Date lastModDate = new Date(cache.lastModified());
            Date now = new Date();

            long diffInMillisec = now.getTime() - lastModDate.getTime();
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);

            int ndiffInMin = (int) diffInMin;

            return ndiffInMin;
        } else {

            return 99999;

        }



    }

    public int getTimeDiffHours(){

        File cache = new File(cacheDir + "/" + key + ".srl");

        if (cache.exists()) {

            Date lastModDate = new Date(cache.lastModified());
            Date now = new Date();

            long diffInMillisec = now.getTime() - lastModDate.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillisec);

            int ndiffInHours = (int) diffInHours;

            return ndiffInHours;
        } else {

            return 99999;

        }

    }


    public String retrieve() {

        try {

            key = URLEncoder.encode(key, "UTF-8");

            File cache = new File(cacheDir + "/" + key + ".srl");

            if (cache.exists()) {

                ObjectInputStream in = new ObjectInputStream(new FileInputStream(cache));
                String value = in.readUTF();
                in.close();

                return value;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }
}
