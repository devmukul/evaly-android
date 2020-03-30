package bd.com.evaly.evalyshop.ui.issue;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.tvIssueType.setText(Utils.toFirstCharUpperAll(model.getIssue_type()));
        holder.tvDate.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMT("yyyy-MM-dd'T'HH:mm:ss", "", model.getCreated_at())));

        holder.tvIssueStatus.setText(Utils.toFirstCharUpperAll(model.getStatus()));

        if (model.getStatus().toLowerCase().equalsIgnoreCase("active")) {
            holder.tvIssueStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
        } else {
            holder.tvIssueStatus.setBackgroundColor(Color.parseColor("#33d274"));
        }

        int comment = model.getIssue_replies().size();
        String commentString = comment + " Comments";
        if (comment == 1)
            commentString = comment + " Comment";

        holder.tvCommentCount.setText(commentString);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class IssuesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvIssueType)
        TextView tvIssueType;
        @BindView(R.id.tvCommentCount)
        TextView tvCommentCount;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvIssueStatus)
        TextView tvIssueStatus;

        public IssuesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> listener.onRecyclerViewItemClicked(list.get(getLayoutPosition())));
        }
    }
}
