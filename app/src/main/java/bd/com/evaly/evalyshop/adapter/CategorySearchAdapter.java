package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.activity.SearchCategory;
import bd.com.evaly.evalyshop.util.TextTouchListener;

public class CategorySearchAdapter extends RecyclerView.Adapter<CategorySearchAdapter.MyViewHolder>{

    ArrayList<JSONArray> itemList;
    Context context;
    String query;

    public CategorySearchAdapter(ArrayList<JSONArray> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        this.query = query;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_search,viewGroup,false);
        return new MyViewHolder(view);
    }


    private View.OnClickListener clickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            String slug = v.getTag().toString();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("type", 4);
            intent.putExtra("category_slug", slug);
            context.startActivity(intent);

        }
    };


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {


        LinearLayout linearLayout = myViewHolder.linearLayout;

        linearLayout.removeAllViews();


        try {


            JSONArray ancestors = itemList.get(position);

            for (int i = 0; i < ancestors.length(); i++){

                JSONObject item = ancestors.getJSONObject(i);

                String name = item.getString("name");
                String slug = item.getString("slug");

                TextView htext =new TextView(context);
                htext.setText(highlight(((SearchCategory)context).getQuery(), name));
                htext.setTextColor(Color.parseColor("#333333"));
                htext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                htext.setTextSize(16f);
                Drawable img = context.getResources().getDrawable( R.drawable.ic_folder );
                img.setBounds( 0, 0, 50, 40 );
                htext.setCompoundDrawables(img, null, null, null);
                htext.setCompoundDrawablePadding(15);
                htext.setPadding((i+1)*70, 0 , 0 , 50);
                htext.setClickable(true);
                htext.setTag(slug);

                htext.setOnTouchListener(new TextTouchListener());

                htext.setOnClickListener(clickListener);

                linearLayout.addView(htext);

            }
        } catch (Exception ignored){

        }

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

                highlighted.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }

}
