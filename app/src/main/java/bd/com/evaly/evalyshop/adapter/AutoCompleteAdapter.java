package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import bd.com.evaly.evalyshop.util.Utils;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> data;
    private String server = "https://nsuer.club/evaly/search-suggestions/?q=";
    Context context;
    RequestQueue queue;
    LayoutInflater inflater;
    String text = "";

    public AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
        this.data = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        server = server;
        queue = Volley.newRequestQueue(context);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        // tvViewResourceId = (TextView) view.findViewById(android.R.id.text1);
        String item = getItem(position);
        Log.d("item", "" + item);
        if (convertView == null) {
            convertView = view = inflater.inflate(
                    android.R.layout.simple_list_item_1, null);
        }
        // Lookup view for data population
        TextView myTv = (TextView) convertView.findViewById(android.R.id.text1);
        myTv.setText(highlight(text, item));
        return view;
    }

    public static CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer
                .normalize(originalText, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH);

        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());

               //  highlighted.setSpan(new ForegroundColorSpan(Color.BLUE), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                highlighted.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {

                    text = constraint.toString();

                    if(!containsSubString(data,text.toLowerCase())) {


                        String query = constraint.toString();

                        query = query.replaceAll(" ", "+");

                        String url = server + query;

                        try {
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    try {

                                        JSONArray list = response.getJSONArray("result");

                                        ArrayList<String> suggestions = new ArrayList<>();

                                        list = shuffleJsonArray(list);

                                        list = removeRandom(list);


                                        for (int i = 0; i < list.length(); i++) {

                                            String term = list.getJSONObject(i).getJSONObject("model").getString("query");

                                            suggestions.add(term);

                                        }

                                        data = suggestions;


                                        notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("onErrorResponse", error.toString());
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    return headers;
                                }
                            };
                            request.setShouldCache(true);
                            queue.add(request);


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    } else {



                        ArrayList<String> suggestions = new ArrayList<>();

                        for (int i = 0; i < data.size(); i++) {

                            String term = data.get(i);

                                suggestions.add(term);

                        }

                        //data = suggestions;

                        //notifyDataSetChanged();


                        results.values = suggestions;//.toArray();
                        results.count = suggestions.size();

                    }

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }


    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }




    public static JSONArray removeRandom(JSONArray array) throws JSONException{


        Random r = new Random();


        JSONArray newArray = array;

        int needToRemove = (int) (array.length()/3);


        for (int i =0; i < needToRemove; i++) {

            int result = r.nextInt(array.length());

            newArray.remove(i);


        }

        return newArray;


    }


    public static boolean containsSubString (ArrayList<String> col, String str) {
        for (String s: col) {
            if (s.contains(str)) {
                return true;
            }
        }
        return false;
    }

}