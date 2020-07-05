package bd.com.evaly.evalyshop.ui.issue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueTicketCommentModel;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IssueReplyAdapter extends RecyclerView.Adapter<IssueReplyAdapter.IssueReplyViewHolder> {

    private Activity context;
    private List<IssueTicketCommentModel> list;

    public IssueReplyAdapter(Activity context, List<IssueTicketCommentModel> list) {
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
        IssueTicketCommentModel model = list.get(i);
        holder.tvReply.setText(model.getComment());
        holder.tvName.setText(model.getCommentedBy().getFirstName() + " " + model.getCommentedBy().getLastName());
        holder.tvDate.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMTIssue("yyyy-MM-dd HH:mm:ss.SSS", "", model.getCreatedAt())));

//        if (model.getTicket().getAttachments() != null)
//            Glide.with(context)
//                    .load(model.getProfilePicture()).into(holder.picture);
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
        @BindView(R.id.picture)
        ImageView picture;


        public IssueReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
