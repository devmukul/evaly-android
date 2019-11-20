package bd.com.evaly.evalyshop.activity.chat.invite;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.chat.EvalyUserModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteViewHolder> {

    private Activity context;
    private List<EvalyUserModel> list;
    private RecyclerViewOnItemClickListener listener;

    public InviteAdapter(Activity context, List<EvalyUserModel> list, RecyclerViewOnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InviteViewHolder(LayoutInflater.from(context).inflate(R.layout.invite_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int i) {
        EvalyUserModel model = list.get(i);
        holder.tvName.setText(model.getFirst_name()+" "+model.getLast_name());
        holder.tvNumber.setText(model.getUsername());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class InviteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvNumber)
        TextView tvNumber;
        @BindView(R.id.llInvite)
        LinearLayout llInvite;
        @BindView(R.id.llInvited)
        LinearLayout llInvited;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            llInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (llInvited.getVisibility() == View.GONE){
                        listener.onRecyclerViewItemClicked(list.get(getLayoutPosition()));
                        llInvite.setVisibility(View.GONE);
                        llInvited.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
