package bd.com.evaly.evalyshop.ui.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.chat.RequestedUserModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestListViewHolder> {
    private List<RequestedUserModel> list;
    private Activity context;
    private OnAcceptRejectListener listener;
    private List<String> nameList;

    public RequestListAdapter(List<RequestedUserModel> list, Activity context, OnAcceptRejectListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        nameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RequestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestListViewHolder(LayoutInflater.from(context).inflate(R.layout.requst_item_view, parent, false));
    }

    public interface OnAcceptRejectListener {
        void onRequestAccept(RequestedUserModel jid, String name);
        void onRequestReject(RequestedUserModel jid, String name);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestListViewHolder holder, int position) {
        RequestedUserModel model = list.get(position);

        XmlToJson xmlToJson = new XmlToJson.Builder(model.getVcard()).build();
        String name, imageUrl = null;
        try {
            JSONObject object = xmlToJson.toJson().getJSONObject("vCard");
            name = object.getString("FN");
            if (name == null) {
                name = "";
            }
            imageUrl = object.getString("URL");
            holder.tvName.setText(name);
            nameList.add(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            StringBuilder initials = new StringBuilder();
            for (String s : holder.tvName.getText().toString().split(" ")) {
                if (!s.trim().isEmpty()) {
                    if (initials.length() < 2) {
                        initials.append(s.charAt(0));
                    }
                }
            }
            holder.tvShortName.setVisibility(View.VISIBLE);
//            holder.tvShortName.setBackgroundTintList(ColorStateList.valueOf(Utils.getRandomColor()));
//            holder.tvShortName.setBackgroundColor(Utils.getRandomColor());
            holder.tvShortName.setText(initials.toString().toUpperCase());
            holder.ivProfileImage.setVisibility(View.GONE);
        } else {
            holder.ivProfileImage.setVisibility(View.VISIBLE);
            holder.tvShortName.setVisibility(View.GONE);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(holder.ivProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RequestListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvShortName)
        TextView tvShortName;
        @BindView(R.id.llAccept)
        LinearLayout llAccept;
        @BindView(R.id.llReject)
        LinearLayout llReject;


        public RequestListViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            llAccept.setOnClickListener(view -> {
                if (list.size()>0)
                    listener.onRequestAccept(list.get(getLayoutPosition()), nameList.get(getLayoutPosition()));
            });

            llReject.setOnClickListener(view -> {
                if (list.size() > 0)
                    listener.onRequestReject(list.get(getLayoutPosition()), nameList.get(getLayoutPosition()));
            });
        }
    }
}
