package bd.com.evaly.evalyshop.ui.issue.adapter;

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
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.IssuesViewHolder> {

    private Activity context;
    private List<IssueListModel> list;
    private RecyclerViewOnItemClickListener listener;

    public IssuesAdapter(Activity context, List<IssueListModel> list, RecyclerViewOnItemClickListener listener) {
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
        IssueListModel model = list.get(i);

        holder.tvIssueType.setText(Utils.toFirstCharUpperAll(model.getCategory()));
        holder.tvDate.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMTIssue("", "", model.getCreatedAt())));

        holder.tvIssueStatus.setText(Utils.toFirstCharUpperAll(model.getStatus()));

        if (model.getStatus().toLowerCase().equalsIgnoreCase("active") || model.getStatus().toLowerCase().equalsIgnoreCase("submitted")) {
            holder.tvIssueStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
        } else if (model.getStatus().toLowerCase().equalsIgnoreCase("rejected")) {
            holder.tvIssueStatus.setBackgroundColor(Color.parseColor("#d9534f"));
        } else {
            holder.tvIssueStatus.setBackgroundColor(Color.parseColor("#33d274"));
        }

        holder.tvCommentCount.setText(Utils.truncateText(model.getAdditionalInfo(), 100, "..."));
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
