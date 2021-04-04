package bd.com.evaly.evalyshop.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ReferPref {

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "ReferPref";

    public ReferPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearAll(){

        editor.clear().commit();

    }

    // referral
    public String getRef() {
        return  pref.getString("referral","");
    }

    public void setRef(String name) {
        editor.putString("referral", name);
        editor.commit();
    }

    public String getRefMessage() {
        return  pref.getString("referral_message","Get 20 Tk. Evaly balance when someone uses your invitation code during registration on Evaly. Only new phone numbers with unique devices will be counter as valid registrations.");
    }

    public void setRefMessage(String name) {
        editor.putString("referral_message", name);
        editor.commit();
    }

    public String getRefStatistics() {
        return  pref.getString("referral_statistics","<b>0</b> people registered with your code.");
    }

    public void setRefStatistics(String name) {
        editor.putString("referral_statistics", name);
        editor.commit();
    }

    public boolean isRated() {
        return  pref.getBoolean("is_rated", false);
    }

    public void setRated(boolean name) {
        editor.putBoolean("is_rated", name);
        editor.commit();
    }

}