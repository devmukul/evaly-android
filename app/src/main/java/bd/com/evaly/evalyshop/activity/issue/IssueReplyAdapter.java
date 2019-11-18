package bd.com.evaly.evalyshop.activity.issue;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.issue.IssuesModel;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IssueReplyAdapter extends RecyclerView.Adapter<IssueReplyAdapter.IssueReplyViewHolder> {

    private Activity context;
    private List<IssuesModel.ReplyModel> list;

    public IssueReplyAdapter(Activity context, List<IssuesModel.ReplyModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IssueReplyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IssueReplyViewHolder(LayoutInflater.from(context).inflate(R.layout.issue_reply_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IssueReplyViewHolder holder, int i) {
        IssuesModel.ReplyModel model = list.get(i);
        holder.tvReply.setText(model.getBody());
        holder.tvName.setText(model.getReply_by().getFirst_name()+" "+model.getReply_by().getLast_name());
        holder.tvDate.setText(Utils.getConvertedTime(model.getUpdated_at()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class IssueReplyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvReply)
        TextView tvReply;


        public IssueReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
