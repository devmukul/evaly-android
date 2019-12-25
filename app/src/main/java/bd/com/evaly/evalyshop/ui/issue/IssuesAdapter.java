package bd.com.evaly.evalyshop.ui.issue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.issue.IssuesModel;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.IssuesViewHolder> {

    private Activity context;
    private List<IssuesModel> list;
    private RecyclerViewOnItemClickListener listener;

    public IssuesAdapter(Activity context, List<IssuesModel> list, RecyclerViewOnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IssuesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IssuesViewHolder(LayoutInflater.from(context).inflate(R.layout.issue_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesViewHolder holder, int i) {
        IssuesModel model = list.get(i);
        holder.tvBody.setText(model.getDescription());
        holder.tvDate.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMT("yyyy-MM-dd'T'HH:mm:ss","",model.getCreated_at())));

        if (model.getIssue_replies() != null)
            holder.commentCount.setText(model.getIssue_replies().size()+" Comments");

        if (model.getAttachment() != null){
            holder.ivIssueImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(model.getAttachment())
                    .into(holder.ivIssueImage);
        }else {
            holder.ivIssueImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class IssuesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.commentCount)
        TextView commentCount;
        @BindView(R.id.ivIssueImage)
        ImageView ivIssueImage;

        public IssuesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> listener.onRecyclerViewItemClicked(list.get(getLayoutPosition())));
        }
    }
}
